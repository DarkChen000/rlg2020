package com.itdr.pojo.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderItemVO_TotalPrice {
    private List<OrderItemVO> orderItemVOList;
    private String imageHost;
    private BigDecimal productTotalPrice;
}
