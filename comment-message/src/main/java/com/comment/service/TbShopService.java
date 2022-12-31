package com.comment.service;

import com.comment.entity.TbShop;
import com.baomidou.mybatisplus.extension.service.IService;
import com.comment.vo.NearbyShopVO;

/**
* @author Administrator
* @description 针对表【tb_shop】的数据库操作Service
* @createDate 2022-12-23 19:09:54
*/
public interface TbShopService extends IService<TbShop> {

    /**
     * 查询所有商户信息
     * @return 返回结果
     * @param page 页号
     */
    String shopList(Integer page);

    /**
     * 查询附近商户
     * @param nearbyShopVO 查询信息
     * @return 返回商户list
     */
    String nearbyShop(NearbyShopVO nearbyShopVO);
}
