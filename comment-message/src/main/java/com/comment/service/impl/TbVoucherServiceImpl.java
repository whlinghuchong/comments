package com.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.comment.entity.TbVoucher;
import com.comment.mapper.TbVoucherMapper;
import com.comment.service.TbVoucherService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Administrator
 * @description 针对表【tb_voucher】的数据库操作Service实现
 * @createDate 2022-12-24 21:53:10
 */
@Service
public class TbVoucherServiceImpl extends ServiceImpl<TbVoucherMapper, TbVoucher>
        implements TbVoucherService {

    @Resource
    TbVoucherMapper tbVoucherMapper;

    @Override
    public  void destocking(long commodityId) {
        final LambdaQueryWrapper<TbVoucher> voucherQuery = new LambdaQueryWrapper<>();
        voucherQuery.eq(TbVoucher::getShopId, commodityId);
        final TbVoucher tbVoucher = tbVoucherMapper.selectOne(voucherQuery);
        tbVoucher.setPayValue(tbVoucher.getPayValue() - 1);
        tbVoucherMapper.updateById(tbVoucher);
    }
}




