package com.comment.controller;

import com.comment.service.TbFollowService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * @author yf
 */
@RestController
@RequestMapping("/follow")
@Validated
public class FollowController {

    @Resource
    TbFollowService followService;

    @PostMapping("/userfollow/{userId}/{userFollowId}")
    public String userFollow(@NotNull
                             @PathVariable Long userId,
                             @NotNull
                             @PathVariable Long userFollowId) {
        return followService.userFollow(userId, userFollowId);
    }

    @GetMapping("/common/{userId}/{userCommonFollowId}")
    public String commonFollow(@PathVariable Long userCommonFollowId,
                               @PathVariable Long userId) {
        return followService.commonFollow(userCommonFollowId, userId);
    }
}
