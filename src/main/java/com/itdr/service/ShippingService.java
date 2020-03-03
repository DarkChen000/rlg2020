package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.Shipping;
import com.itdr.pojo.User;

public interface ShippingService {
    ServerResponse create(User user);

    ServerResponse deleteShipping(User user, Integer shippingId);

    ServerResponse addShipping(User user, String receiver_name, String receiver_phone, String receiver_mobile, String receiver_province, String receiver_city, String receiver_district, String receiver_address, String receiver_zip);

    ServerResponse updateShipping(User user, Shipping shipping);

}
