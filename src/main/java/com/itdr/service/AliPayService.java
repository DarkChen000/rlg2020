package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.User;

public interface AliPayService {
    ServerResponse pay(User user, Long orderNum);

}
