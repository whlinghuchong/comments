package com.comment.service;

import com.comment.entity.TbUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.comment.vo.IphoneLoginVO;

/**
* @author Administrator
* @description 针对表【tb_user】的数据库操作Service
* @createDate 2022-12-20 21:34:30
*/
public interface TbUserService extends IService<TbUser> {

    /**
     * 生成验证码
     * @param iphone 手机号
     * @return 返回结果
     */
    String loginCode(String iphone);

    /**
     * 手机号登陆
     * @param iphoneLogin 登陆对象
     * @return 返回登陆结果
     */
    String login(IphoneLoginVO iphoneLogin);

    /**
     * 用户签到
     * @param userId 签到id
     * @return 返回结果
     */
    String userSign(Long userId);

    /**
     * 统计用户签到天数
     * @param userId 用户id
     * @return 返回结果
     */
    String contSign(Long userId);
}
