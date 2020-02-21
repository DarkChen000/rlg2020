package com.itdr.utils;

import com.itdr.pojo.Product;
import com.itdr.pojo.User;
import com.itdr.pojo.vo.ProductVO;
import com.itdr.pojo.vo.UserVO;

public class ObjectToVOUtil {

    /**
     * 用户类封装
     * @param u
     * @return
     */
    public static UserVO UserToUserVO(User u){
        UserVO userVO = new UserVO();
        userVO.setId(u.getId());
        userVO.setUsername(u.getUsername());
        userVO.setEmail(u.getEmail());
        userVO.setPhone(u.getPhone());
        userVO.setCreateTime(u.getCreateTime());
        userVO.setUpdateTime(u.getUpdateTime());
        return userVO;
    }

    /**
     * 商品类封装
     * @param product
     * @return
     */
    public static ProductVO ProductToProductVO(Product product){
        ProductVO pv = new ProductVO();
        pv.setIsBanner(product.getBanner());
        pv.setCategoryId(product.getCategoryId());
        pv.setCreateTime(product.getCreateTime());
        pv.setDetail(product.getDetail());
        pv.setIsHot(product.getHot());
        pv.setMainImage(product.getMainImage());
        pv.setId(product.getId());
        pv.setName(product.getName());
        pv.setIsNew(product.getNew());
        pv.setUpdateTime(product.getUpdateTime());
        pv.setSubtitle(product.getSubtitle());
        pv.setStock(product.getStock());
        pv.setStatus(product.getStatus());
        pv.setSubImages(product.getSubImages());
        pv.setPrice(product.getPrice());
        return pv;
    }
}
