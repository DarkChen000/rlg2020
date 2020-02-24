package com.itdr.pojo.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class CartVO {

    private List<CartProductVO> cartProductVOList;

    private Boolean allChecked;

    private BigDecimal cartTotalPrice;
}
