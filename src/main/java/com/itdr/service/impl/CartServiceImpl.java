package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.mapper.CartMapper;
import com.itdr.mapper.ProductMapper;
import com.itdr.pojo.Cart;
import com.itdr.pojo.Product;
import com.itdr.pojo.User;
import com.itdr.pojo.vo.CartProductVO;
import com.itdr.pojo.vo.CartVO;
import com.itdr.service.CartService;
import com.itdr.utils.BigDecimalUtil;
import com.itdr.utils.ObjectToVOUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductMapper productMapper;

    // 获取cartVO
    protected CartVO getCartVO(List<Cart> cartList){
        // 创建一个存放商品的集合
        List<CartProductVO> cartProductVOList = new ArrayList<>();

        // 获取购物车中对应的商品信息
        boolean bol = true;
        BigDecimal cartTotalPrice = new BigDecimal("0");

        for (Cart cart : cartList) {
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());

            // 商品不为空的时候进行封装
            if (product != null){
                // 第一次封装  把商品和购物车信息进行数据封装
                CartProductVO cartProductVO = ObjectToVOUtil.CartAndProductToVO(cart, product);
                cartProductVOList.add(cartProductVO);

                // 计算购物车总价，只计算选中的商品
                if (cartProductVO.getProductChecked() == 1){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
                }
            }

            // 判断是否全选
            if (cart.getChecked() == 0){
                bol = false;
            }

        }

        // 返回数据
        CartVO cartVO = ObjectToVOUtil.toCartVO(cartProductVOList,bol,cartTotalPrice);

        return cartVO;
    }

    // 获取用户购物车列表
    private ServerResponse getUserCart(User user){
        // 查询登录用户的购物车信息
        List<Cart> cartList = cartMapper.selectByUserID(user.getId());

        // 判断用户购物车中是否有数据
        if (cartList.size() == 0){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.EMPTY_CART.getCode(),ConstCode.CartEnum.EMPTY_CART.getDesc());
        }
        return ServerResponse.successRS(cartList);
    }

    // 获取购物车列表
    @Override
    public ServerResponse list(User user) {

        ServerResponse<List<Cart>> cartList = getUserCart(user);
        if (! cartList.isSuccess()){
            return cartList;
        }

        // 获取一个封装对象
        CartVO cartVO = getCartVO(cartList.getData());
        return ServerResponse.successRS(cartVO);

    }

    // 添加商品
    @Override
    public ServerResponse add(Integer productID, Integer count,Integer type, User user) {
        // 参数合法判断
        if (StringUtils.isEmpty(productID) || productID < 0){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.CartEnum.UNLAWFUINESS_PARAM.getDesc());
        }
        if (StringUtils.isEmpty(count) || count < 0){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.CartEnum.UNLAWFUINESS_PARAM.getDesc());
        }

        // 向购物车中加入数据
        Cart cart = new Cart();
        cart.setUserId(user.getId());
        cart.setProductId(productID);
        cart.setQuantity(count);

        // 查找商品
        Product product = productMapper.selectByPrimaryKey(productID);
        // 判断要添加的商品是否在售
        if (product == null || product.getStatus() != 1){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.EMPTY_FOUND.getCode(),ConstCode.CartEnum.EMPTY_FOUND.getDesc());
        }

        // 判断要添加到购物车中的商品数量是否超出商品库存
        if (count > product.getStock()){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.OUT_STOCK.getCode(),ConstCode.CartEnum.OUT_STOCK.getDesc());
        }

        // 向购物车中添加商品或更新商品数据
        // 查询购物车中是否有此商品
        Cart c = cartMapper.selectByUserIDAndProductID(user.getId(),productID);

        if (c == null){
            // 如果没有，直接添加
            int insert = cartMapper.insertProduct(cart);
            // 添加失败
            if (insert <= 0){
                return ServerResponse.defeatedRS
                        (ConstCode.CartEnum.FAILED_INSERT.getCode(),ConstCode.CartEnum.FAILED_INSERT.getDesc());
            }
        }else {
            // 如果有，更新商品数量,根据type判断要执行的更新方式
            if (type == ConstCode.CartEnum.CART_TYPE.getCode()){
                c.setQuantity(count + cart.getQuantity());
            }else if (type == ConstCode.CartEnum.CART_TYPE2.getCode()){
                c.setQuantity(count);
            }else {
                return ServerResponse.defeatedRS
                        (ConstCode.CartEnum.FAILED_CART_TYPE.getCode(),ConstCode.CartEnum.FAILED_CART_TYPE.getDesc());
            }
            int insert2 = cartMapper.updateByPrimaryKey(c);
            // 更新失败
            if (insert2 <= 0){
                return ServerResponse.defeatedRS
                        (ConstCode.CartEnum.FAILED_UPDATE.getCode(),ConstCode.CartEnum.FAILED_UPDATE.getDesc());
            }
        }

        ServerResponse<List<Cart>> cartList = getUserCart(user);
        if (! cartList.isSuccess()){
            return cartList;
        }

        // 获取一个封装对象
        CartVO cartVO = getCartVO(cartList.getData());
        return ServerResponse.successRS(cartVO);
    }

    // 移除购物车中某一个商品
    @Override
    public ServerResponse deleteProduct(Integer productID, User user) {
        // 参数合法判断
        if (StringUtils.isEmpty(productID)){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.CartEnum.UNLAWFUINESS_PARAM.getDesc());
        }

        // 查询购物车中是否有此商品
        Cart c = cartMapper.selectByUserIDAndProductID(user.getId(),productID);
        if (c == null){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.EMPTY_FOUND.getCode(),ConstCode.CartEnum.EMPTY_FOUND.getDesc());
        }

        // 删除此商品
        int i = cartMapper.deleteByProductIDAndUserID(user.getId(),productID);
        if (i <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.FAILED_DELETE.getCode(),ConstCode.CartEnum.FAILED_DELETE.getDesc());
        }

        ServerResponse<List<Cart>> cartList = getUserCart(user);
        if (! cartList.isSuccess()){
            return cartList;
        }

        // 获取一个封装对象
        CartVO cartVO = getCartVO(cartList.getData());
        return ServerResponse.successRS(cartVO);
    }

    // 移除购物车所有被选中商品
    @Override
    public ServerResponse deleteProducts(User user) {
        // 查询是否有选中商品
        List<Cart> c = cartMapper.selectByChecked(user.getId());
        if (c.size() == 0){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.NO_CHECKED.getCode(),ConstCode.CartEnum.NO_CHECKED.getDesc());
        }

        // 删除所有被选中的商品
        int i = cartMapper.deleteByChecked(user.getId());
        if (i <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.FAILED_DELETE.getCode(),ConstCode.CartEnum.FAILED_DELETE.getDesc());
        }

        ServerResponse<List<Cart>> cartList = getUserCart(user);
        if (! cartList.isSuccess()){
            return cartList;
        }

        // 获取一个封装对象
        CartVO cartVO = getCartVO(cartList.getData());
        return ServerResponse.successRS(cartVO);
    }

    // 更新商品
    @Override
    public ServerResponse update(Integer productID, Integer count,Integer type, User user) {
        // 商品详情页面使用数值加减器改变

        // 参数合法判断
        if (StringUtils.isEmpty(productID)){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.CartEnum.UNLAWFUINESS_PARAM.getDesc());
        }
        if (StringUtils.isEmpty(count)){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.CartEnum.UNLAWFUINESS_PARAM.getDesc());
        }

        // 查找商品
        Product product = productMapper.selectByPrimaryKey(productID);
        // 判断要更新的商品是否在售
        if (product == null || product.getStatus() != 1){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.EMPTY_FOUND.getCode(),ConstCode.CartEnum.EMPTY_FOUND.getDesc());
        }

        // 向购物车中添加商品或更新商品数据
        // 查询购物车中是否有此商品
        Cart cart = cartMapper.selectByUserIDAndProductID(user.getId(),productID);

        // 根据type类型判断执行的更新方式
        if (type == ConstCode.CartEnum.CART_TYPE.getCode()){
            cart.setQuantity(count + cart.getQuantity());
        }else if (type == ConstCode.CartEnum.CART_TYPE2.getCode()){
            cart.setQuantity(count);
        }else {
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.FAILED_CART_TYPE.getCode(),ConstCode.CartEnum.FAILED_CART_TYPE.getDesc());
        }
        int insert2 = cartMapper.updateByPrimaryKey(cart);
        // 更新失败
        if (insert2 <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.FAILED_UPDATE.getCode(),ConstCode.CartEnum.FAILED_UPDATE.getDesc());
        }


        ServerResponse<List<Cart>> cartList = getUserCart(user);
        if (! cartList.isSuccess()){
            return cartList;
        }

        // 获取一个封装对象
        CartVO cartVO = getCartVO(cartList.getData());
        return ServerResponse.successRS(cartVO);
    }

    // 查询在购物车里的商品数量
    @Override
    public ServerResponse getCartProductCount(User user) {
        List<Cart> cartList = cartMapper.selectByUserID(user.getId());
        Integer num = 0;
        for (Cart cart : cartList) {
            num += cart.getQuantity();
        }

        return ServerResponse.successRS(num);
    }

    // 商品选中、全选等
    @Override
    public ServerResponse checked(Integer productID, Integer type,User user) {
        // 参数合法判断
        if (productID != null && productID < 0){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.CartEnum.UNLAWFUINESS_PARAM.getDesc());
        }
        if (type == null){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.CartEnum.UNLAWFUINESS_PARAM.getDesc());
        }
        if (!(type == 0 || type == 1)){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.CartEnum.UNLAWFUINESS_PARAM.getDesc());
        }

        // 选中或取消选中一个商品
        int i = cartMapper.updateByUserIDAndProductID(user.getId(),productID,type);
        if (i <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.CartEnum.FAILED_UPDATE.getCode(),ConstCode.CartEnum.FAILED_UPDATE.getDesc());
        }

        ServerResponse<List<Cart>> cartList = getUserCart(user);
        if (! cartList.isSuccess()){
            return cartList;
        }

        // 获取一个封装对象
        CartVO cartVO = getCartVO(cartList.getData());
        return ServerResponse.successRS(cartVO);
    }

    @Override
    public ServerResponse toPayFor(User user) {
        // 判断当前用户购物车中有没有数据
        List<Cart> cartList = cartMapper.selectByUserID(user.getId());
        if (cartList.size() == 0){
            return ServerResponse.defeatedRS
                    (ConstCode.OrderEnum.EMPTY_CART.getCode(),ConstCode.OrderEnum.EMPTY_CART.getDesc());
        }

        // 判断购物车中商品是否被选中
        boolean bol = false;
        for (Cart cart : cartList) {
            if (cart.getChecked() == 1){
                bol = true;
            }
        }
        if (!bol){
            return ServerResponse.defeatedRS
                    (ConstCode.OrderEnum.NO_CHECKED.getCode(),ConstCode.OrderEnum.NO_CHECKED.getDesc());
        }

        return ServerResponse.successRS(true);
    }
}
