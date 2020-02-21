package com.itdr.mapper;

import com.itdr.pojo.Product;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKey(Product record);

    Product selectByPrimaryKey(Integer productId);

    List<Product> selectByName(String word);

}