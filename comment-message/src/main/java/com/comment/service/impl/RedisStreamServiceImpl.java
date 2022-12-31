package com.comment.service.impl;

import com.comment.service.RedisStreamService;
import com.comment.service.TbVoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.comment.consts.RedisConst.REDIS_STREAM_GROUP01;
import static com.comment.consts.RedisConst.REDIS_STREAM_QUEUE;

/**
 * @author yf
 */
@Service
@Slf4j
public class RedisStreamServiceImpl implements RedisStreamService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    TbVoucherOrderService tbVoucherOrderService;

    @Override
    public void redisStreamRead() {
        log.info("开始循环读取消息");
        while (true) {
            try {
                final List<MapRecord<String, Object, Object>> c1 = stringRedisTemplate.opsForStream().read(
                        Consumer.from(REDIS_STREAM_GROUP01, "c1"),
                        StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                        StreamOffset.create(REDIS_STREAM_QUEUE, ReadOffset.lastConsumed())
                );
                if (Objects.isNull(c1)) {
                    continue;
                }
                log.info("读取到消息进行订单处理");
                redisStreamOrder(c1);

            } catch (Exception e) {
                e.printStackTrace();
                log.info("处理消息失败进行二次处理{}", e.getMessage());
                handlePendingList();
            }
        }
    }

    private void handlePendingList() {
        while (true) {
            try {
                final List<MapRecord<String, Object, Object>> c1 = stringRedisTemplate.opsForStream().read(
                        Consumer.from(REDIS_STREAM_GROUP01, "c1"),
                        StreamReadOptions.empty().count(1),
                        StreamOffset.create(REDIS_STREAM_QUEUE, ReadOffset.from("0"))
                );
                if (Objects.isNull(c1)) {
                    return;
                }
                log.info("进行二次订单处理");
                redisStreamOrder(c1);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("二次订单处理失败,请联系管理员,失败原因{}", e.getMessage());
            }
        }
    }

    private void redisStreamOrder(List<MapRecord<String, Object, Object>> c1) {
        log.info("进行订单处理");
        final MapRecord<String, Object, Object> entries = c1.get(0);
        final Map<Object, Object> value = entries.getValue();
        final String userId = value.get("userId").toString();
        final String pop = value.get("pop").toString();
        final String commodityId = value.get("commodityId").toString();
        tbVoucherOrderService.saveOrder(Long.parseLong(userId), Long.parseLong(pop), Long.parseLong(commodityId));
        log.info("订单处理成功");
        stringRedisTemplate.opsForStream().acknowledge(REDIS_STREAM_QUEUE, REDIS_STREAM_GROUP01, entries.getId());
        log.info("手动ack成功");
    }

}
