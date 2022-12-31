package com.comment.controller;

import com.comment.entity.TbBlog;
import com.comment.service.TbBlogService;
import com.comment.vo.NearbyShopVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * @author yf
 */
@RequestMapping("/blog")
@RestController
public class BlogController {

    @Resource
    TbBlogService tbBlogService;

    @GetMapping("/like/{userId}/{blogId}")
    public String blogLike(@Validated
                           @NotNull
                           @PathVariable Long blogId,
                           @NotNull
                           @PathVariable Long userId) {
        return tbBlogService.blogLike(userId, blogId);
    }

    @GetMapping("/push")
    public String getBlog(@RequestParam Long userId, @RequestParam(required = false, defaultValue = "0") Long start) {
        return tbBlogService.getBlog(userId, start);
    }

    @PostMapping("/save")
    public String saveBlog(@RequestBody TbBlog tbBlog) {
        return tbBlogService.saveBlog(tbBlog);
    }

}
