package com.comment.controller;

import com.comment.service.SeckillService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yf
 */
@RestController
@RequestMapping("/commodity")
public class SeckillController {

    @Resource
    SeckillService seckillService;

    @PostMapping("/seckill/{userId}/{commodityId}")
    public String commoditySeckill(@PathVariable Long commodityId, @PathVariable Long userId){
        return seckillService.commoditySeckill(userId,commodityId);
    }
}
