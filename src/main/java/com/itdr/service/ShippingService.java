package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.User;

public interface ShippingService {
    ServerResponse create(User user);
}
