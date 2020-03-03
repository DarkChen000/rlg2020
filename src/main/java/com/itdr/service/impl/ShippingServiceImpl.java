package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.mapper.ShippingMapper;
import com.itdr.pojo.Shipping;
import com.itdr.pojo.User;
import com.itdr.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    ShippingMapper shippingMapper;

    private Shipping getShipping(User user, String name, String phone, String mobile, String province,
                                 String city, String district, String address, String zip){
        Shipping shipping = new Shipping();
        shipping.setUserId(user.getId());
        shipping.setReceiverName(name);
        shipping.setReceiverPhone(phone);
        shipping.setReceiverMobile(mobile);
        shipping.setReceiverProvince(province);
        shipping.setReceiverCity(city);
        shipping.setReceiverDistrict(district);
        shipping.setReceiverAddress(address);
        shipping.setReceiverZip(zip);
        return shipping;
    }

    @Override
    public ServerResponse create(User user) {
        List<Shipping> list = shippingMapper.selectByUserID(user.getId());

        if (list.size() == 0){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.EMPTY_ADDRESS.getCode(),ConstCode.ShippingEnum.EMPTY_ADDRESS.getDesc());
        }

        return ServerResponse.successRS(list);
    }

    @Override
    public ServerResponse deleteShipping(User user, Integer shippingId) {
        // 参数合法判断
        if (shippingId == null || shippingId <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getDesc());
        }

        int i = shippingMapper.deleteByUserIdAndShippingId(user.getId(),shippingId);
        if (i <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.FAILED_DELETE.getCode(),ConstCode.ShippingEnum.FAILED_DELETE.getDesc());
        }

        return ServerResponse.defeatedRS
                (ConstCode.ShippingEnum.SUCCESS_DELETE.getCode(),ConstCode.ShippingEnum.SUCCESS_DELETE.getDesc());
    }

    @Override
    public ServerResponse addShipping(User user, String name, String phone, String mobile, String province,
                                      String city, String district, String address, String zip) {
        // 参数合法判断
        if (StringUtils.isEmpty(name)){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getDesc());
        }
        if (StringUtils.isEmpty(phone)){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getDesc());
        }if (StringUtils.isEmpty(mobile)){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getDesc());
        }if (StringUtils.isEmpty(province)){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getDesc());
        }if (StringUtils.isEmpty(city)){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getDesc());
        }if (StringUtils.isEmpty(district)){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getDesc());
        }if (StringUtils.isEmpty(address)){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getDesc());
        }if (StringUtils.isEmpty(zip)){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getDesc());
        }
        // 封装对象
        Shipping shipping = getShipping(user, name, phone, mobile, province, city, district, address, zip);
        int i = shippingMapper.insert(shipping);
        if (i <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.FAILED_ADD.getCode(),ConstCode.ShippingEnum.FAILED_ADD.getDesc());
        }
        return ServerResponse.defeatedRS
                (ConstCode.ShippingEnum.SUCCESS_ADD.getCode(),ConstCode.ShippingEnum.SUCCESS_ADD.getDesc());
    }

    @Override
    public ServerResponse updateShipping(User user, Shipping shipping) {
        // 除了id不需要判断非空，如果都是空，默认不更改
        if (shipping.getId() == null){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getCode(),ConstCode.ShippingEnum.UNLAWFUINESS_PARAM.getDesc());
        }

        // 更改地址信息
        int i = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (i <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.ShippingEnum.FAILED_UPDATE.getCode(),ConstCode.ShippingEnum.FAILED_UPDATE.getDesc());
        }

        return ServerResponse.successRS
                (ConstCode.ShippingEnum.SUCCESS_UPDATE.getCode(),ConstCode.ShippingEnum.SUCCESS_UPDATE.getDesc());
    }
}
