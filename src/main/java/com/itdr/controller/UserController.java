package com.itdr.controller;


import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.pojo.User;
import com.itdr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@ResponseBody
@RequestMapping("/portal/user/")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 用户登录
     * @param Session
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("login.do")
    public ServerResponse login(HttpSession Session, String username, String password){
        ServerResponse sr = userService.login(username,password);
        if (sr.isSuccess()){
            // 当成功登陆时，在session中保存用户数据
            Session.setAttribute("user",sr.getData());
        }
        return sr;
    }

    /**
     * 注册
     * @param u
     * @return
     */
    @RequestMapping("register.do")
    public ServerResponse register(User u){
        return userService.register(u);
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @RequestMapping("logout.do")
    public ServerResponse logout(HttpSession session){
        User user = (User) session.getAttribute("user");
        return userService.logout(session,user);
    }

    /**
     * 获取当前登录用户的详细信息
     * @param session
     * @return
     */
    @RequestMapping("get_information.do")
    public ServerResponse get_information(HttpSession session){
        User user = (User) session.getAttribute("user");
        return userService.getInformation(user);
    }

    /**
     * 
     * @param session
     * @param email
     * @param phone
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping("update_information.do")
    public ServerResponse update_information(HttpSession session,String email,String phone,String question,String answer){
        User user = (User) session.getAttribute("user");
        return userService.updateInformation(user,email,phone,question,answer);
    }
}
