package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.mapper.ShippingMapper;
import com.itdr.pojo.Shipping;
import com.itdr.pojo.User;
import com.itdr.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse create(User user) {
        List<Shipping> list = shippingMapper.selectByUserID(user.getId());

        if (list.size() == 0){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.EMPTY_ADDRESS.getCode(),ConstCode.ShippingEnum.EMPTY_ADDRESS.getDesc());
        }

        return ServerResponse.successRS(list);
    }
}
