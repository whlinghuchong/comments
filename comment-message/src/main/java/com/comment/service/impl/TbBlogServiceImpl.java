package com.comment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.comment.entity.TbBlog;
import com.comment.entity.TbFollow;
import com.comment.mapper.TbBlogMapper;
import com.comment.mapper.TbFollowMapper;
import com.comment.service.TbBlogService;
import com.comment.vo.NearbyShopVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.comment.consts.BlogConst.*;
import static com.comment.consts.ShopConst.SHOP_GEO_KEY;

/**
 * @author Administrator
 * @description 针对表【tb_blog】的数据库操作Service实现
 * @createDate 2022-12-27 12:08:22
 */
@Service
@Slf4j
public class TbBlogServiceImpl extends ServiceImpl<TbBlogMapper, TbBlog>
        implements TbBlogService {

    @Resource
    TbBlogMapper tbBlogMapper;

    @Resource
    TbFollowMapper tbFollowMapper;


    @Resource
    StringRedisTemplate stringRedisTemplate;


    @Override
    public String blogLike(Long userId, Long blogId) {
        final Set<String> members = stringRedisTemplate.opsForSet().members(BLOG_SET_ID + blogId);
        boolean contains = false;
        if (Objects.nonNull(members)) {
            contains = members.contains(userId.toString());
        }

        log.info("博客点赞用户为:{}", contains);
        if (Boolean.TRUE.equals(contains)) {
            final TbBlog tbBlog = tbBlogMapper.selectById(blogId.toString());
            tbBlog.setLiked(tbBlog.getLiked() - 1);
            tbBlogMapper.updateById(tbBlog);
            stringRedisTemplate.opsForSet().remove(BLOG_SET_ID + blogId, userId.toString());
            return "取消点赞成功";
        }

        final TbBlog tbBlog = tbBlogMapper.selectById(blogId);
        if (Objects.isNull(tbBlog)) {
            return "找不到该博客";
        }

        tbBlog.setLiked(tbBlog.getLiked() + 1);
        tbBlogMapper.updateById(tbBlog);
        stringRedisTemplate.opsForSet().add(BLOG_SET_ID + blogId, String.valueOf(userId));

        return "点赞成功";
    }

    @Override
    public String saveBlog(TbBlog tbBlog) {
        tbBlogMapper.insert(tbBlog);
        final long timeMillis = System.currentTimeMillis();

        stringRedisTemplate.opsForZSet()
                .add(BLOG_PUSH_ID + tbBlog.getId(), String.valueOf(tbBlog.getId()), timeMillis);
        final Boolean aBoolean = blogPush(tbBlog.getUserId(), tbBlog.getId(), timeMillis);

        return aBoolean ? "添加微博成功" : "添加微博成功,但是用户没有关注列表";
    }

    @Override
    public String getBlog(Long userId, Long start) {
        final Set<String> range = stringRedisTemplate
                .opsForZSet()
                .range(BLOG_PUSH_MAILBOX_ID + userId, start, start + 5);

        if (CollectionUtil.isEmpty(range)) {
            return "没有更新博客";
        }

        final List<Long> collect = range.stream().map(Long::valueOf).collect(Collectors.toList());
        final List<TbBlog> tbBlogs = tbBlogMapper.selectBatchIds(collect);

        return JSON.toJSONString(tbBlogs);
    }

    @Override
    public Boolean blogPush(Long userId, Long blogId, Long timeMillis) {
        final LambdaQueryWrapper<TbFollow> tbFollowQuery = new LambdaQueryWrapper<>();
        tbFollowQuery.eq(TbFollow::getFollowUserId, userId);
        final List<TbFollow> tbFollows = tbFollowMapper.selectList(tbFollowQuery);

        if (CollectionUtil.isEmpty(tbFollows)) {
            return false;
        }

        final List<Long> idList = tbFollows.stream().map(TbFollow::getUserId).collect(Collectors.toList());
        idList.forEach((i) -> stringRedisTemplate.opsForZSet()
                .add(BLOG_PUSH_MAILBOX_ID + i, String.valueOf(blogId), timeMillis));

        return true;
    }
}




