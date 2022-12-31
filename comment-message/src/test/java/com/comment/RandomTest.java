package com.comment;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.comment.service.RedisIdWorkerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;

/**
 * @author yf
 */
@SpringBootTest
public class RandomTest {

    @Resource
    RedisIdWorkerService redisIdWorkerService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void randomSix() {
        System.out.println(RandomUtil.randomNumbers(6));
    }

    @Test
    public void redisIdTest() {
        final Long aLong = redisIdWorkerService.redisId();
        System.out.println(aLong);
    }

    @Test
    public void dateTest() {
        final DateTime date = DateUtil.date();
        final String orderId = DateUtil.format(date, "yyyyMMddHHmmSS");
        System.out.println(orderId);
        System.out.println(System.currentTimeMillis());
        final String userDate = DateUtil.format(new Date(), "yyyyMM");
        System.out.println(userDate);
        final long day = Long.parseLong(userDate.substring(3));
        System.out.println(day);
        final int day1 = Calendar.getInstance().get(Calendar.DATE);
        System.out.println(day1);
        String a = "0123456789123456789123456";
        System.out.println(Arrays.toString(a.getBytes()));
    }

    @Test
    public void redisStreamTest() {
        stringRedisTemplate.opsForStream().createGroup("k1", "g1");
        final HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("q1", "q1");
        stringRedisTemplate.opsForStream().add("k1", stringStringHashMap);
        final List<MapRecord<String, Object, Object>> c1 = stringRedisTemplate.opsForStream().read(Consumer.from("g1", "c1"), StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)), StreamOffset.create("k1", ReadOffset.lastConsumed()));
        final MapRecord<String, Object, Object> entries = c1.get(0);
        final Map<Object, Object> value = entries.getValue();
        final Object q1 = value.get("q1");
        final RecordId id = entries.getId();
        stringRedisTemplate.opsForStream().acknowledge("k1", "g", id);
        System.out.println(q1);
    }

    @Test
    public void redisCont() {
        for (int i = 0; i < 10; i++) {
            stringRedisTemplate.opsForValue().setBit("user:sign:id:1:12", i, true);
        }
    }
}
