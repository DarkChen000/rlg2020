package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.mapper.UserMapper;
import com.itdr.pojo.User;
import com.itdr.service.UserService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.security.krb5.internal.PAData;

import javax.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public ServerResponse login(String username, String password) {
        // 参数非空判断
        if (StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_USERNAME.getCode(), ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
        if (StringUtils.isEmpty(password)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_PASSWORD.getCode(), ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }

        // 查询用户
        User u = userMapper.selectByUserNameAndPassWord(username,password);
        if (u == null){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.FAIL_LOGIN.getCode(),ConstCode.UserEnum.FAIL_LOGIN.getDesc());
        }
        // 返回成功数据
        return ServerResponse.successRS(u);
    }

    @Override
    public ServerResponse register(User u) {
        // 参数非空判断
        if (StringUtils.isEmpty(u.getUsername())){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_USERNAME.getCode(), ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
        if (StringUtils.isEmpty(u.getPassword())){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_PASSWORD.getCode(), ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }
        if (StringUtils.isEmpty(u.getEmail())){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_EMAILS.getCode(), ConstCode.UserEnum.EMPTY_EMAILS.getDesc());
        }
        if (StringUtils.isEmpty(u.getQuestion())){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_QUESTION.getCode(), ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }
        if (StringUtils.isEmpty(u.getAnswer())){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_ANSWER.getCode(), ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }

        // 查找用户名是否存在
        int i = userMapper.selectByUserName(u.getUsername());
        if (i > 0){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EXIST_USER.getCode(), ConstCode.UserEnum.EXIST_USER.getDesc());
        }
        // 查找邮箱是否存在
        int m = userMapper.selectByEmails(u.getEmail());
        if (m > 0){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EXIST_EMAIL.getCode(), ConstCode.UserEnum.EXIST_EMAIL.getDesc());
        }
        // 注册用户信息
        int insert = userMapper.insert(u);
        if (insert <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.FAIL_REGISTER.getCode(), ConstCode.UserEnum.FAIL_REGISTER.getDesc());
        }
        // 成功返回默认成功状态码和成功信息
        return ServerResponse.successRS(ConstCode.UserEnum.SUCCESS_USER.getDesc());
    }

    @Override
    public ServerResponse getInformation(User user) {
        if (StringUtils.isEmpty(user)){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        User u = userMapper.getInformation(user.getUsername());
        return ServerResponse.successRS(u);
    }

    @Override
    public ServerResponse logout(HttpSession session,User user) {
        if (StringUtils.isEmpty(user)){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.EXCEPTION_SERVER.getDesc());
        }
        session.removeAttribute("user");
        return ServerResponse.successRS(ConstCode.UserEnum.LOGOUT.getDesc());
    }

    @Override
    public ServerResponse updateInformation(User user, String email, String phone, String question, String answer) {
        if (StringUtils.isEmpty(user)){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        int i = userMapper.updateInformation(user.getUsername(),email,phone,question,answer);
        if (i <= 0){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return ServerResponse.successRS(ConstCode.UserEnum.SUCCESS_USERMSG.getDesc());
    }
}
