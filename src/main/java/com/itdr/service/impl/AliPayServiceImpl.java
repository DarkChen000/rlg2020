package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.User;
import com.itdr.service.AliPayService;
import org.springframework.stereotype.Service;

@Service
public class AliPayServiceImpl implements AliPayService {



    @Override
    public ServerResponse pay(User user, Long orderNum) {
        return null;
    }
}
