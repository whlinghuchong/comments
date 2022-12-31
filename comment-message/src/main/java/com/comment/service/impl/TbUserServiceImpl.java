package com.comment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.comment.entity.TbUser;
import com.comment.mapper.TbUserMapper;
import com.comment.service.TbUserService;
import com.comment.vo.IphoneLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.comment.consts.LoginConst.LOGIN_PREFIX_IPHONE;
import static com.comment.consts.LoginConst.LOGIN_USER_TOKEN;
import static com.comment.consts.UserConst.USER_SIGN_ID;

/**
 * @author Administrator
 * @description 针对表【tb_user】的数据库操作Service实现
 * @createDate 2022-12-20 21:34:30
 */
@Service
@Slf4j
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser>
        implements TbUserService {

    @Resource
    TbUserService tbUserService;


    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public String loginCode(String iphone) {

        final String iphoneCode = stringRedisTemplate.opsForValue().get(LOGIN_PREFIX_IPHONE + iphone);
        if (Strings.isNotEmpty(iphoneCode)) {
            return "请30秒后在试";
        }

        final LambdaQueryWrapper<TbUser> tbUserServiceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tbUserServiceLambdaQueryWrapper.eq(TbUser::getPhone, iphone);
        final TbUser tbUser = tbUserService.getOne(tbUserServiceLambdaQueryWrapper);
        if (Objects.isNull(tbUser)) {
            return "用户手机号未注册";
        }

        final String randomNumbers = RandomUtil.randomNumbers(6);
        stringRedisTemplate.opsForValue().set(LOGIN_PREFIX_IPHONE + iphone, randomNumbers, 30, TimeUnit.SECONDS);
        return randomNumbers;
    }

    @Override
    public String login(IphoneLoginVO iphoneLogin) {

        final String iphoneCode = stringRedisTemplate.opsForValue().get(iphoneLogin.getIphone());
        if (Strings.isEmpty(iphoneCode) || !(iphoneLogin.getIphoneCode().equals(iphoneCode))) {
            return "验证码不正确";
        }

        final LambdaQueryWrapper<TbUser> tbUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tbUserLambdaQueryWrapper.eq(TbUser::getPhone, iphoneLogin.getIphone());
        final TbUser loginUser = tbUserService.getOne(tbUserLambdaQueryWrapper);
        loginUser.setPassword("");

        final String userJson = JSON.toJSONString(loginUser);
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(LOGIN_USER_TOKEN + token, userJson, 30, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public String userSign(Long userId) {
        final String userDate = DateUtil.format(new Date(), "yyyyMM");
        final long month = Long.parseLong(userDate.substring(4));
        final int day = Calendar.getInstance().get(Calendar.DATE) - 1;
        final Boolean bit = stringRedisTemplate.opsForValue().getBit(USER_SIGN_ID + userId + ":" + month, day);
        if (Boolean.TRUE.equals(bit)) {
            return "今天已经签到过了";
        }
        stringRedisTemplate.opsForValue().setBit(USER_SIGN_ID + userId + ":" + month, day, true);
        return "签到成功";
    }

    @Override
    public String contSign(Long userId) {
        final String userDate = DateUtil.format(new Date(), "yyyyMM");
        final long month = Long.parseLong(userDate.substring(4));
        final int day = Calendar.getInstance().get(Calendar.DATE);
        final List<Long> signList = stringRedisTemplate.opsForValue()
                .bitField(USER_SIGN_ID + userId + ":" + month,
                        BitFieldSubCommands.create()
                                .get(BitFieldSubCommands.BitFieldType.unsigned(day)).valueAt(0));
        if (CollectionUtil.isEmpty(signList)) {
            return "本月没有签到";
        }
        log.info(signList.toString());
        final Long contLong = signList.get(0);
        log.info(String.valueOf(contLong));
        final String string = Long.toBinaryString(contLong);
        final byte[] bytes = string.getBytes();
        int i = 0;
        for (byte aByte : bytes) {
            if (aByte == 49) {
                i++;
            }
        }
        return "本月签到天数为:" + i + "天";
    }
}




