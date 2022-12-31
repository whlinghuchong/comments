package com.comment.service;

/**
 * @author Administrator
 */
public interface RedisIdWorkerService {
    /**
     * 生成全局唯一id
     * @return 返回结果
     */
    Long redisId();
}
