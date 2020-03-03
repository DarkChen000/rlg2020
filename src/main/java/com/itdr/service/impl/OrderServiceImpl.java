package com.itdr.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.mapper.*;
import com.itdr.pojo.*;
import com.itdr.pojo.vo.*;
import com.itdr.service.CartService;
import com.itdr.service.OrderService;
import com.itdr.utils.BigDecimalUtil;
import com.itdr.utils.ObjectToVOUtil;
import com.itdr.utils.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.math.BigDecimal;
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
        orderVO.setCreateTime(o.getCreateTime());
        return orderVO;
    }

    // 根据订单详情集合获取OrderItemVO集合
    private List<OrderItemVO> getOrderItemVO(List<OrderItem> itemList){
        List<OrderItemVO> itemVOList = new ArrayList<>();
        for (OrderItem orderItem : itemList) {
            OrderItemVO orderItemVO = ObjectToVOUtil.orderItemToOrderItemVO(orderItem);
            itemVOList.add(orderItemVO);
        }
        return itemVOList;
    }

    // 创建一个订单详情对象
    private List<OrderItem> getOrderItem(Integer uid, List<Product> productList,List<Cart> cartList){
        List<OrderItem> orderItemList = new ArrayList<>();
        for (Cart cart : cartList) {
            OrderItem o = new OrderItem();
            o.setQuantity(cart.getQuantity());
            for (Product product : productList) {
                if (product.getId().equals(cart.getProductId())){
                    o.setUserId(uid);
                    o.setProductId(cart.getProductId());
                    o.setProductName(product.getName());
                    o.setProductImage(product.getMainImage());
                    o.setCurrentUnitPrice(product.getPrice());
                    o.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));

                    orderItemList.add(o);
                }
            }
        }
        return orderItemList;
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

    @Override
    public ServerResponse getOrderCartProduct(User user, Long orderNo) {
        OrderItemVO_TotalPrice orderItemVOTotalPriceList = new OrderItemVO_TotalPrice();

        // 如果订单编号不为空，根据用户id和订单编号获取对应的订单详情信息
        if (orderNo != null){
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNumAndUserID(orderNo, user.getId());
            List<OrderItemVO> orderItemVO = this.getOrderItemVO(orderItemList);
            Order order = orderMapper.selectByOrderNo(orderNo);
            if (order == null){
                return ServerResponse.defeatedRS
                        (ConstCode.OrderEnum.FAIDLED_ORDER.getCode(),ConstCode.OrderEnum.FAIDLED_ORDER.getDesc());
            }

            orderItemVOTotalPriceList.setOrderItemVOList(orderItemVO);
            orderItemVOTotalPriceList.setImageHost(PropertiesUtil.getValue("ImageHost"));
            orderItemVOTotalPriceList.setProductTotalPrice(order.getPayment());
        }else {
            // 没有订单id，根据用户id获取订单详情
            // 获取购物车中选中的商品
            List<Cart> cartList = cartMapper.selectByChecked(user.getId());
            if (cartList.size() == 0){
                return ServerResponse.defeatedRS
                        (ConstCode.OrderEnum.NO_CHECKED.getCode(),ConstCode.OrderEnum.NO_CHECKED.getDesc());
            }

            // 存储订单选中的商品数据
            List<Product> productList = new ArrayList<>();
            // 计算总价
            BigDecimal payment = new BigDecimal("0");
            for (Cart cart : cartList) {
                // 判断商品是否失效
                Integer productId = cart.getProductId();
                // 根据商品id获取商品数据
                Product product = productMapper.selectByPrimaryKey(productId);
                // 判断商品是否有效
                if (product == null){
                    return ServerResponse.defeatedRS
                            (ConstCode.OrderEnum.EMPRY_PRODUCT.getCode(),ConstCode.OrderEnum.EMPRY_PRODUCT.getDesc());
                }
                // 判断商品是否下架
                if (product.getStatus() != 1){
                    return ServerResponse.defeatedRS
                            (ConstCode.OrderEnum.NO_STATUS.getCode(),ConstCode.OrderEnum.NO_STATUS.getDesc());
                }
                // 判断商品是否超出库存
                if (cart.getQuantity() > product.getStock()){
                    return ServerResponse.defeatedRS
                            (ConstCode.OrderEnum.OVER_STOCK.getCode(),ConstCode.OrderEnum.OVER_STOCK.getDesc());
                }
                // 根据购物车购物数量和商品单价计算一条购物车信息的总价
                BigDecimal mul = BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity());
                // 把所有购物车信息总价相加，得出订单总价
                payment = BigDecimalUtil.add(payment.doubleValue(), mul.doubleValue());
                // 存入商品集合
                productList.add(product);
            }

            List<OrderItem> orderItem = this.getOrderItem(user.getId(), productList, cartList);
            List<OrderItemVO> orderItemVO = this.getOrderItemVO(orderItem);

            orderItemVOTotalPriceList.setOrderItemVOList(orderItemVO);
            orderItemVOTotalPriceList.setImageHost(PropertiesUtil.getValue("ImageHost"));
            orderItemVOTotalPriceList.setProductTotalPrice(payment);
        }


        return ServerResponse.successRS(orderItemVOTotalPriceList);
    }

    // 获取用户订单列表
    @Override
    public ServerResponse getOrderList(Integer uid,Integer pageNum,Integer pageSize) {
        List<OrderVO> orderVOList = new ArrayList<>();

        // 获取用户的所有订单
        // 分页处理放在查询语句之上
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUid(uid);

        // 循环创建OrderVO对象
        for (Order o : orderList) {
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNumAndUserID(o.getOrderNo(), uid);
            // 判断是否有订单
            if (orderItemList.size() == 0){
                return ServerResponse.defeatedRS
                        (ConstCode.OrderEnum.NOT_FIND_ORDER.getCode(),ConstCode.OrderEnum.NOT_FIND_ORDER.getDesc());
            }

            List<OrderItemVO> orderItemVOList = this.getOrderItemVO(orderItemList);
            Shipping shipping = shippingMapper.selectByPrimaryKey(o.getShippingId());
            ShippingVO shippingVO = ObjectToVOUtil.shippingToShippingVO(shipping);

            // 封装OrderVO对象，添加至集合中
            OrderVO orderVO = getOrderVO(o.getShippingId(), o, orderItemVOList, shippingVO);

            orderVOList.add(orderVO);
        }

        PageInfo pageInfo = new PageInfo(orderList);
        // 用setList改变集合中内容为orderVOList
        pageInfo.setList(orderVOList);

        return ServerResponse.successRS(pageInfo);
    }
}
