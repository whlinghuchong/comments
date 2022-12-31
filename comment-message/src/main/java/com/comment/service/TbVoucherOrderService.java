package com.comment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.comment.entity.TbVoucherOrder;

/**
 * @author Administrator
 * @description 针对表【tb_voucher_order】的数据库操作Service
 * @createDate 2022-12-25 01:34:46
 */
public interface TbVoucherOrderService extends IService<TbVoucherOrder> {
    /**
     * 秒杀商品生成订单
     *
     * @param userId 用户id
     * @param pop 秒杀卷编号
     * @param commodityId 商品秒杀id
     */
    void saveOrder(long userId, Object pop, long commodityId);
}
