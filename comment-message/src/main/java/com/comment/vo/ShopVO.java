package com.comment.vo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.geo.Distance;

/**
 * @author yf
 */
@ToString
@Data
@Accessors(chain = true)
public class ShopVO {

    private String shopName;
    private Long shopType;
    private Distance distance;
}
