package com.comment.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.comment.entity.TbVoucherOrder;
import com.comment.mapper.TbVoucherOrderMapper;
import com.comment.service.RedisIdWorkerService;
import com.comment.service.TbVoucherOrderService;
import com.comment.service.TbVoucherService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Administrator
 * @description 针对表【tb_voucher_order】的数据库操作Service实现
 * @createDate 2022-12-25 01:34:46
 */
@Service
public class TbVoucherOrderServiceImpl extends ServiceImpl<TbVoucherOrderMapper, TbVoucherOrder>
        implements TbVoucherOrderService {

    @Resource
    RedisIdWorkerService redisIdWorkerService;

    @Resource
    TbVoucherOrderMapper tbVoucherOrderMapper;

    @Resource
    TbVoucherService tbVoucherService;

    @Override
    public void saveOrder(long userId, Object pop, long commodityId) {
        final TbVoucherOrder tbVoucherOrder = new TbVoucherOrder();
        tbVoucherOrder.setVoucherId((Long) pop);
        tbVoucherOrder.setUserId(userId);
        final Long aLong = redisIdWorkerService.redisId();
        final DateTime date = DateUtil.date();
        final String orderId = DateUtil.format(date, "yyyyMMddHH") + userId + aLong;
        tbVoucherOrder.setId(Long.valueOf(orderId));
        tbVoucherOrderMapper.insert(tbVoucherOrder);
        tbVoucherService.destocking(commodityId);
    }
}




