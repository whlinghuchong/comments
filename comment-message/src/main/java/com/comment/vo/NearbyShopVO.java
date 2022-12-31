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
public class NearbyShopVO {

    private Long typeId;
    private Long page;
    private Double x;
    private Double y;
}
