package com.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.comment.entity.TbVoucher;
import com.comment.mapper.TbVoucherMapper;
import com.comment.service.RedisIdWorkerService;
import com.comment.service.RedisStreamService;
import com.comment.service.SeckillService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.comment.consts.RedisConst.REDIS_STREAM_GROUP01;
import static com.comment.consts.RedisConst.REDIS_STREAM_QUEUE;
import static com.comment.consts.SeckillConst.SECKILL_ID;

/**
 * @author yf
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    @Resource
    TbVoucherMapper tbVoucherMapper;

    @Resource
    RedisIdWorkerService redisIdWorkerService;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RedisStreamService redisStreamService;


    @Override
    public String commoditySeckill(long userId, long commodityId) {
        final Boolean aBoolean = redisTemplate.hasKey(SECKILL_ID + commodityId);
        if (Boolean.FALSE.equals(aBoolean)) {
            synchronized (this) {
                if (Boolean.FALSE.equals(redisTemplate.hasKey(SECKILL_ID + commodityId))) {
                    final LambdaQueryWrapper<TbVoucher> voucherQuery = new LambdaQueryWrapper<>();
                    voucherQuery.eq(TbVoucher::getShopId, commodityId);
                    final TbVoucher tbVoucher = tbVoucherMapper.selectOne(voucherQuery);
                    if (Objects.isNull(tbVoucher)) {
                        return "没有查询到秒杀信息";
                    }

                    try {
                        for (int i = 0; i < tbVoucher.getPayValue(); i++) {
                            final Long aLong = redisIdWorkerService.redisId();
                            redisTemplate.opsForSet().add(SECKILL_ID + commodityId, aLong);
                        }
                    } finally {
                        redisTemplate.expire(SECKILL_ID + commodityId, 2, TimeUnit.HOURS);
                    }
                }
            }
        }

        final Object pop = redisTemplate.opsForSet().pop(SECKILL_ID + commodityId);
        if (Objects.isNull(pop)) {
            return "商品秒杀售罄";
        }
        final HashMap<String, String> orderMap = new HashMap<>(3);
        orderMap.put("userId", String.valueOf(userId));
        orderMap.put("pop", String.valueOf(pop));
        orderMap.put("commodityId", String.valueOf(commodityId));
        stringRedisTemplate.opsForStream().add(REDIS_STREAM_QUEUE, orderMap);
        return "秒杀成功";
    }

//    @PostConstruct
//    public void infoRedisStream() {
//        new Thread(() -> {
//            redisStreamService.redisStreamRead();
//        }).start();
//    }
}
