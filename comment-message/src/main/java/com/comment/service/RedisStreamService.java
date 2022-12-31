package com.comment.service;

/**
 * @author Administrator
 */
public interface RedisStreamService {

    /**
     * 消息队列消费者
     */
    void redisStreamRead();

}
