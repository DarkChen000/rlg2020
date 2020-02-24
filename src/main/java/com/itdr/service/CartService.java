package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.User;

public interface CartService {
    ServerResponse list(User user);


    ServerResponse add(Integer productID, Integer count, User user);

    ServerResponse deleteProduct(String productIDs, User user);

    ServerResponse update(Integer productID, Integer count, User user);
}
