package com.comment.service;

/**
 * @author Administrator
 */
public interface SeckillService {
    /**
     * 秒杀业务
     * @param userId 用户id
     * @param commodityId 秒杀商品id
     * @return 返回结果
     */
    String commoditySeckill(long userId, long commodityId);
}
