package com.comment.service.impl;

import com.comment.service.RedisIdWorkerService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yf
 */
@Service
public class RedisIdWorkerServiceImpl implements RedisIdWorkerService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Long redisId() {
        return stringRedisTemplate.opsForValue().increment("incr:order");
    }
}
