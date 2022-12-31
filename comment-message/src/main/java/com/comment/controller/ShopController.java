package com.comment.controller;

import com.comment.service.TbShopService;
import com.comment.vo.NearbyShopVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author yf
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    TbShopService tbShopService;

    @GetMapping("/list")
    public String shopList(Integer page){
        return tbShopService.shopList(page);
    }

    @PostMapping("/nearby")
    public String nearbyShop(@RequestBody NearbyShopVO nearbyShopVO) {
        return tbShopService.nearbyShop(nearbyShopVO);
    }
}
