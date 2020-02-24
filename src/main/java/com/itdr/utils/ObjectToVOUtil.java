package com.itdr.utils;

import com.itdr.pojo.Cart;
import com.itdr.pojo.Product;
import com.itdr.pojo.User;
import com.itdr.pojo.vo.CartProductVO;
import com.itdr.pojo.vo.CartVO;
import com.itdr.pojo.vo.ProductVO;
import com.itdr.pojo.vo.UserVO;

import java.math.BigDecimal;
import java.util.List;

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

    /**
     * 购物车和商品数据封装
     * @param cart
     * @param product
     * @return
     */
    public static CartProductVO CartAndProductToVO(Cart cart,Product product){
        CartProductVO cvo = new CartProductVO();
        cvo.setId(cart.getId());
        cvo.setUserId(cart.getUserId());
        cvo.setProductId(cart.getProductId());
        cvo.setQuantity(cart.getQuantity());
        cvo.setProductName(product.getName());
        cvo.setProductSubtitle(product.getSubtitle());
        cvo.setProductMainImage(product.getMainImage());
        cvo.setProductPrice(product.getPrice());
        cvo.setProductStatus(product.getStatus());
        cvo.setProductStock(product.getStock());
        // 一条购物信息的总价
        BigDecimal tp = BigDecimalUtil.mul(cart.getQuantity(),product.getPrice().doubleValue());
        cvo.setProductTotalPrice(tp);
        // 判断商品是否被选中
        cvo.setProductChecked(cart.getChecked());
        // 购买的商品数量是否超过库存
        String limitQuantity = "SUCCESS";
        if (cart.getQuantity() > product.getStock()){
            limitQuantity = "FAILED";
        }
        cvo.setLimitQuantity(limitQuantity);
        return cvo;
    }

    /**
     * 购物车二次封装
     * @param cartProductVOList
     * @return
     */
    public static CartVO toCartVO(List<CartProductVO> cartProductVOList,boolean bol,BigDecimal cartTotalPrice){
        CartVO cvo = new CartVO();
        // 购物车商品数据
        cvo.setCartProductVOList(cartProductVOList);
        // 购物车是否全选
        cvo.setAllChecked(bol);
        // 购物车总价
        cvo.setCartTotalPrice(cartTotalPrice);
        return cvo;
    }
}
