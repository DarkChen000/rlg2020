package com.itdr.mapper;

import com.itdr.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUserNameAndPassWord(String username, String password);

    int selectByUserName(String username);

    int selectByEmails(String email);

    User getInformation(String username);

    int updateInformation(String username, String email, String phone, String question, String answer);
}