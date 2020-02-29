package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.mapper.*;
import com.itdr.pojo.*;
import com.itdr.pojo.vo.CartVO;
import com.itdr.pojo.vo.OrderItemVO;
import com.itdr.pojo.vo.OrderVO;
import com.itdr.pojo.vo.ShippingVO;
import com.itdr.service.CartService;
import com.itdr.service.OrderService;
import com.itdr.utils.BigDecimalUtil;
import com.itdr.utils.ObjectToVOUtil;
import com.itdr.utils.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    CartMapper cartMapper;

    @Autowired
    CartService cartService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    ShippingMapper shippingMapper;

    // 订单号生成   生成规则：当前系统时间+随机数
    private long dd(){
        // 订单编号生成规则
        // 生成一个随机数
        long round = Math.round(Math.random() * 100);
        long l = System.currentTimeMillis() + round;
        return l;
    }

    /**
     * 封装订单信息
     * @param shippingId
     * @param o
     * @param orderItemVOList
     * @param shippingVO
     * @return
     */
    private OrderVO getOrderVO(Integer shippingId, Order o, List<OrderItemVO> orderItemVOList, ShippingVO shippingVO){
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(o.getOrderNo());
        orderVO.setShippingId(shippingId);
        orderVO.setPayment(o.getPayment());
        orderVO.setPaymentType(o.getPaymentType());
        orderVO.setPostage(o.getPostage());
        orderVO.setStatus(o.getStatus());
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setShippingVO(shippingVO);
        orderVO.setImageHost(PropertiesUtil.getValue("ImageHost"));
        orderVO.setPaymentTime(o.getPaymentTime());
        orderVO.setSendTime(o.getSendTime());
        orderVO.setEndTime(o.getEndTime());
        orderVO.setCloseTime(o.getCloseTime());
        return orderVO;
    }

    @Override
    public ServerResponse create(User user, Integer shippingId) {
        // 参数非空判断
        if (shippingId == null || shippingId < 0){
            return ServerResponse.defeatedRS
                    (ConstCode.OrderEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.OrderEnum.UNLAWFUINESS_PARAM.getDesc());
        }

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

        // 根据用户名获取购物车中信息
        CartVO cartVO = ((CartServiceImpl) cartService).getCartVO(cartList);

        // 创建一个订单
        Order o = new Order();
        o.setUserId(user.getId());
        o.setOrderNo(dd());
        o.setShippingId(shippingId);
        o.setPayment(cartVO.getCartTotalPrice());
        o.setPaymentType(1);
        o.setPostage(0);
        o.setStatus(10);
        // 把创建的订单对象存储到数据库中
        int insert = orderMapper.insert(o);
        if (insert <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.OrderEnum.FAID_ORDER.getCode(),ConstCode.OrderEnum.FAID_ORDER.getDesc());
        }

        // 创建订单详情
        List<OrderItemVO> orderItemVOList = new ArrayList<OrderItemVO>();
        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(user.getId());
            orderItem.setOrderNo(o.getOrderNo());
            if (cart.getChecked() == 1){
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product.getStatus() == 1 && cart.getQuantity() <= product.getStock()){
                    orderItem.setProductId(cart.getProductId());
                    orderItem.setProductName(product.getName());
                    orderItem.setProductImage(product.getMainImage());
                    orderItem.setCurrentUnitPrice(product.getPrice());
                    orderItem.setQuantity(cart.getQuantity());
                    orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));
                }

                // 把创建的订单详情对象存储到数据库中
                int insert1 = orderItemMapper.insert(orderItem);
                if (insert1 <= 0){
                    return ServerResponse.defeatedRS
                            (ConstCode.OrderEnum.FAID_ORDERITEM.getCode(),ConstCode.OrderEnum.FAID_ORDERITEM.getDesc());
                }

                OrderItemVO orderItemVO = ObjectToVOUtil.orderItemToOrderItemVO(orderItem);
                orderItemVOList.add(orderItemVO);
            }
        }

        // 清空购物车数据
        int insert2 = cartMapper.deleteByChecked(user.getId());
        if (insert2 <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.OrderEnum.FAID_DELETE.getCode(),ConstCode.OrderEnum.FAID_DELETE.getDesc());
        }

        // 返回成功数据
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping == null){
            return ServerResponse.defeatedRS
                    (ConstCode.OrderEnum.FAIDED_SHIPPING.getCode(),ConstCode.OrderEnum.FAIDED_SHIPPING.getDesc());
        }
        ShippingVO shippingVO = ObjectToVOUtil.shippingToShippingVO(shipping);
        OrderVO orderVO = getOrderVO(shippingId, o, orderItemVOList, shippingVO);

        return ServerResponse.successRS(orderVO);
    }

}
