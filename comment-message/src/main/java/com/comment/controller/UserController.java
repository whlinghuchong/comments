package com.comment.controller;

import com.comment.service.TbUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yf
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    TbUserService tbUserService;

    @GetMapping("/sign/{userId}")
    public String userSign(@PathVariable Long userId) {
        return tbUserService.userSign(userId);
    }

    @GetMapping("/sign/cont/{userId}")
    public String contSign(@PathVariable Long userId) {
        return tbUserService.contSign(userId);
    }
}
