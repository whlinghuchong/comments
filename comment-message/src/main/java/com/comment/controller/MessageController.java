package com.comment.controller;

import com.alibaba.fastjson.JSON;
import com.comment.service.TbShopService;
import com.comment.service.TbUserService;
import com.comment.vo.IphoneLoginVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Pattern;

/**
 * @author yf
 */
@RestController
@RequestMapping("/comment")
@Validated
public class MessageController {

    @Resource
    TbShopService tbShopService;

    @Resource
    TbUserService tbUserService;

    @GetMapping("/list")
    public String commentList() {
        return JSON.toJSONString(tbShopService.list());
    }

    @PostMapping("/logincode")
    public String commentLoginCode(@Pattern(regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$") String iphone) {
        return tbUserService.loginCode(iphone);
    }

    @PostMapping("/login")
    public String commentLogin(@RequestBody IphoneLoginVO iphoneLogin){
        return tbUserService.login(iphoneLogin);
    }
}
