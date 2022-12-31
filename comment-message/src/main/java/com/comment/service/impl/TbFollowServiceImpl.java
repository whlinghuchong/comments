package com.comment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.comment.entity.TbFollow;
import com.comment.entity.TbUser;
import com.comment.mapper.TbFollowMapper;
import com.comment.mapper.TbUserMapper;
import com.comment.service.TbFollowService;
import com.comment.service.TbUserService;
import com.comment.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.comment.consts.FollowConst.FOLLOW_SET_ID;

/**
 * @author Administrator
 * @description 针对表【tb_follow】的数据库操作Service实现
 * @createDate 2022-12-27 17:20:29
 */
@Service
@Slf4j
public class TbFollowServiceImpl extends ServiceImpl<TbFollowMapper, TbFollow>
        implements TbFollowService {

    @Resource
    TbFollowMapper tbFollowMapper;


    @Resource
    TbUserMapper tbUserMapper;

    @Resource
    TbUserService tbUserService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public String userFollow(Long userId, Long userFollowId) {
        final LambdaQueryWrapper<TbFollow> tbFollowQuery = new LambdaQueryWrapper<>();
        tbFollowQuery
                .eq(TbFollow::getUserId, userFollowId)
                .eq(TbFollow::getFollowUserId, userFollowId);
        final TbFollow tbFollow = tbFollowMapper.selectOne(tbFollowQuery);
        if (Objects.isNull(tbFollow)) {
            final TbFollow userFollow = new TbFollow();
            userFollow
                    .setUserId(userId)
                    .setFollowUserId(userFollowId);
            tbFollowMapper.insert(userFollow);
            stringRedisTemplate.opsForSet().add(FOLLOW_SET_ID + userFollowId, userFollowId.toString());
            return "添加关注成功";
        }
        tbFollowMapper.deleteById(tbFollow);
        stringRedisTemplate.opsForSet().remove(FOLLOW_SET_ID + userFollowId, userFollowId.toString());
        return "取消关注成功";
    }

    @Override
    public String commonFollow(Long userCommonFollowId, Long userId) {
        final Set<String> intersect = stringRedisTemplate
                .opsForSet()
                .intersect(FOLLOW_SET_ID + userId, FOLLOW_SET_ID + userCommonFollowId);
        if (CollectionUtil.isEmpty(intersect)) {
            return "没有共同关注";
        }
        log.info("redis获取set共同关注成功{}", intersect);
        final List<Long> collect = intersect.stream().map(Long::valueOf).collect(Collectors.toList());
        log.info("redis获取set转list{}", collect);
        log.info("开始批量查询");
        final List<TbUser> tbUsers = tbUserService.listByIds(collect);
        final List<UserVO> commonList = tbUsers.stream().map(user -> {
            final UserVO userVO = new UserVO();
            userVO
                    .setUserId(user.getId())
                    .setIcon(user.getIcon())
                    .setNikeName(user.getNickName());
            return userVO;
        }).collect(Collectors.toList());

        return JSON.toJSONString(commonList);
    }
}




