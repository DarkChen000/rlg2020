package com.itdr.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;


@Getter
@Setter
public class CartProductVO {

    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private String productName;

    private String productSubtitle;

    private String productMainImage;

    private BigDecimal productPrice;

    private Integer productStatus;

    private Integer productStock;

    private BigDecimal productTotalPrice;

    private Integer productChecked;

    private String limitQuantity;

}
