package com.itdr.mapper;

import com.itdr.pojo.Cart;
import com.itdr.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<Cart> selectByUserID(Integer id);

    Cart selectByUserIDAndProductID(@Param("userID") Integer id, @Param("productID") Integer productID);

    int insertProduct(Cart cart);

    int deleteByProductIDAndUserID(@Param("userID") Integer id, @Param("pid") Integer productID);

    int deleteByChecked(@Param("id") Integer id);

    List<Cart> selectByChecked(@Param("userID") Integer id);

    int updateByUserIDAndProductID(@Param("userID")Integer id, @Param("pid")Integer productID,@Param("type")Integer type);

}