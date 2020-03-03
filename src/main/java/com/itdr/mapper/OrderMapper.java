package com.itdr.mapper;

import com.itdr.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNumAndUserID(@Param("orderNum") Long orderNum, @Param("uid")Integer id);

    Order selectByOrderNo(Long orderNo);

    List<Order> selectByUid(@Param("uid")Integer uid);
}