package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.User;

public interface CartService {
    ServerResponse list(User user);


    ServerResponse add(Integer productID, Integer count, Integer type ,User user);

    ServerResponse deleteProduct(Integer productID, User user);

    ServerResponse update(Integer productID, Integer count,Integer type, User user);

    ServerResponse deleteProducts(User user);

    ServerResponse getCartProductCount(User user);

    ServerResponse checked(Integer productID, Integer type,User user);

    ServerResponse toPayFor(User user);
}
