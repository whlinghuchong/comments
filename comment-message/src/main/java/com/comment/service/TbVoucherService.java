package com.comment.service;

import com.comment.entity.TbVoucher;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【tb_voucher】的数据库操作Service
* @createDate 2022-12-24 21:53:10
*/
public interface TbVoucherService extends IService<TbVoucher> {

    /**
     * 扣减库存
     * @param commodityId 商品秒杀id
     */
    void destocking(long commodityId);

}
