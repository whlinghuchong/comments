package com.comment.vo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author yf
 */
@Data
@ToString
@Accessors(chain = true)
public class UserVO {
    private Long userId;
    private String nikeName;
    private String icon;
}
