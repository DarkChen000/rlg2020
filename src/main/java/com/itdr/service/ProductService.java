package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.Category;
import com.itdr.pojo.Product;

public interface ProductService {
    ServerResponse<Category> topCategory(Integer sid);

    ServerResponse detail(Integer productId);

    ServerResponse<Product> list(String keyWord, Integer pageName, Integer pageSize, String orderBy);
}
