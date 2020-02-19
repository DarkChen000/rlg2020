package com.itdr.utils;

import com.itdr.pojo.User;
import com.itdr.pojo.bo.UserVO;

public class ObjectToUserVOUtil {

    public static UserVO UserToUserVO(User u){
        UserVO userVO = new UserVO();
        userVO.setId(u.getId());
        userVO.setUsername(u.getUsername());
        userVO.setEmail(u.getEmail());
        userVO.setPhone(u.getPhone());
        userVO.setCreateTime(u.getCreateTime());
        userVO.setUpdateTime(u.getUpdateTime());
        return userVO;
    }
}
