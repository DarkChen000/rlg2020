package com.itdr.controller;


import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.pojo.User;
import com.itdr.pojo.vo.UserVO;
import com.itdr.service.UserService;
import com.itdr.utils.ObjectToVOUtil;
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
     * 获取当前登录用户信息
     * @param session
     * @return
     */
    @RequestMapping("get_user_info.do")
    public ServerResponse getUserInfo(HttpSession session){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        // 已登录返回封装类信息
        UserVO userVO = ObjectToVOUtil.UserToUserVO(user);
        return ServerResponse.successRS(userVO);
    }

    /**
     * 登陆状态下更新个人信息
     * @param session
     * @param email
     * @param phone
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping("update_information.do")
    public ServerResponse updateInformation(HttpSession session,String email,String phone,String question,String answer){
        User user = (User) session.getAttribute("user");
        return userService.updateInformation(user,email,phone,question,answer);
    }

    /**
     * 检查邮箱或用户名是否存在或者是否重复
     * @param str
     * @param type
     * @return
     */
    @RequestMapping("check_valid.do")
    public ServerResponse checkVaild(String str,String type){
        return userService.checkVaild(str,type);
    }

    /**
     * 忘记密码  第一步：获取密保问题
     * @param username
     * @return
     */
    @RequestMapping("forget_get_question.do")
    public ServerResponse forgetGetQuestion(String username){
        return userService.forgetGetQuestion(username);
    }

    /**
     * 提交问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping("forget_check_answer.do")
    public ServerResponse forgetCheckAnswer(String username,String question,String answer){
        return userService.forgetCheckAnswer(username,question,answer);
    }

    /**
     * 忘记密码的重设密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping("forget_reset_password.do")
    public ServerResponse<User> forgetResetPassword(HttpSession session,String username,String passwordNew,String forgetToken){
        ServerResponse<User> serverResponse = userService.forgetResetPassword(username, passwordNew, forgetToken);
        // 如果重设密码成功  清空session  重新登录
        if (serverResponse.isSuccess()){
            session.removeAttribute("user");
        }
        return serverResponse;
    }


    @RequestMapping("reset_password.do")
    public ServerResponse resetPassword(HttpSession session,String passwordOld,String passwordNew){
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (user == null){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return userService.resetPassword(user,passwordOld,passwordNew);
    }
}
