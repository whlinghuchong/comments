package com.comment.service;

import com.comment.entity.TbBlog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.comment.vo.NearbyShopVO;

/**
* @author Administrator
* @description 针对表【tb_blog】的数据库操作Service
* @createDate 2022-12-27 12:08:22
*/
public interface TbBlogService extends IService<TbBlog> {

    /**
     * 点赞
     *
     * @param userId 点赞用户id
     * @param blogId 点在blogId
     * @return 返回点赞结果
     */
    default String blogLike(Long userId, Long blogId) {
        return null;
    }

    /**
     * 保存微博
     *
     * @param tbBlog 微博详情
     * @return 返回结果
     */
    default String saveBlog(TbBlog tbBlog) {
        return null;
    }

    /**
     * 获取关注列表博客
     *
     * @param userId 登陆用户id
     * @param start  起始页数
     * @return 返回博客列表
     */
    default String getBlog(Long userId, Long start) {
        return null;
    }

    /**
     * 推送博客
     *
     * @param userId     登陆用户id
     * @param blogId     推送的博客id
     * @param timeMillis 博客生成时间
     * @return 返回博客list
     */
    default Boolean blogPush(Long userId, Long blogId, Long timeMillis) {
        return null;
    }
}
