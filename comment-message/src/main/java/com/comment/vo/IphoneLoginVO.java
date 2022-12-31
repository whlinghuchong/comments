package com.comment.vo;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Pattern;

/**
 * @author yf
 */
@ToString
@Data
public class IphoneLoginVO {

    @Pattern(regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$",message = "手机号不正确")
    private String iphone;

    @Pattern(regexp = "^\\d{6}$",message = "验证码不正确")
    private String iphoneCode;
}
