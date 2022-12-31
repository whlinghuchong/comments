package com.comment.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author yf
 */
@ToString
@Data
public class OrderVO {
    private Long userId;

    private Object pop;

    private Long commodityId;
}
