package com.comment.service;

import com.comment.entity.TbFollow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【tb_follow】的数据库操作Service
* @createDate 2022-12-27 17:20:29
*/
public interface TbFollowService extends IService<TbFollow> {

    /**
     * 关注或取关
     * @param userId 用户id
     * @param userFollowId 关注或取关用户id
     * @return 返回结果
     */
    String userFollow(Long userId, Long userFollowId);

    /**
     * 共同关注
     * @param userCommonFollowId 目标id
     * @param userId 用户id
     * @return 返回共同关注集合
     */
    String commonFollow(Long userCommonFollowId, Long userId);
}
