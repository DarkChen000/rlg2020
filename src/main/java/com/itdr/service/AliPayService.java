package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.User;

import java.util.Map;

public interface AliPayService {
    ServerResponse pay(User user, Long orderNum);

    ServerResponse alipayCallback(Map<String, String> params);

}
