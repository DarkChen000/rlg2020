package com.itdr.service.impl;

import com.itdr.common.ServerResponse;
import com.itdr.config.ConstCode;
import com.itdr.config.TokenCache;
import com.itdr.mapper.UserMapper;
import com.itdr.pojo.User;
import com.itdr.service.UserService;
import com.itdr.utils.MD5Util;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.security.krb5.internal.PAData;

import javax.servlet.http.HttpSession;
import java.util.UUID;

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

        // MD5加密
        String md5Code = MD5Util.getMD5Code(password);

        // 查询用户
        User u = userMapper.selectByUserNameAndPassWord(username,md5Code);
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

        // MD5加密
        u.setPassword(MD5Util.getMD5Code(u.getPassword()));

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
        User u = new User();
        u.setId(user.getId());
        u.setEmail(email);
        u.setPhone(phone);
        u.setQuestion(question);
        u.setAnswer(answer);
        int i = userMapper.updateByPrimaryKeySelective(u);
        if (i <= 0){
            return ServerResponse.defeatedRS(ConstCode.UserEnum.NO_LOGIN.getDesc());
        }
        return ServerResponse.successRS(ConstCode.UserEnum.SUCCESS_USERMSG.getDesc());
    }

    @Override
    public ServerResponse checkVaild(String str, String type) {
        if (StringUtils.isEmpty(str)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.DEFAULT_FAIL.getCode(),ConstCode.UserEnum.DEFAULT_FAIL.getDesc());
        }
        if (StringUtils.isEmpty(type)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.DEFAULT_TYPE.getCode(),ConstCode.UserEnum.DEFAULT_TYPE.getDesc());
        }
        int i = userMapper.selectByUserNameOrEmails(str,type);
        if (i > 0){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.DEFAULT_EXIST.getCode(),ConstCode.UserEnum.DEFAULT_EXIST.getDesc());
        }
        return ServerResponse.successRS(ConstCode.UserEnum.SUCCESS_MSG.getDesc());
    }

    @Override
    public ServerResponse<User> forgetGetQuestion(String username) {
        if (StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_USERNAME.getCode(),ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
        // 判断用户是否存在（在前端用Ajax判断）
        User u = userMapper.selectByUsername(username);
        if (u == null){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.INEXISTENCE_USER.getCode(),ConstCode.UserEnum.INEXISTENCE_USER.getDesc());
        }

        // 获取用户密保问题
        String question = u.getQuestion();
        if (StringUtils.isEmpty(question)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.NO_QUESTION.getCode(),ConstCode.UserEnum.NO_QUESTION.getDesc());
        }

        return ServerResponse.successRS(ConstCode.DEFAULT_SUCCESS,question);
    }

    @Override
    public ServerResponse forgetCheckAnswer(String username, String question, String answer) {
        if (StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_USERNAME.getCode(),ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
        if (StringUtils.isEmpty(question)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_QUESTION.getCode(),ConstCode.UserEnum.EMPTY_QUESTION.getDesc());
        }
        if (StringUtils.isEmpty(answer)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_ANSWER.getCode(),ConstCode.UserEnum.EMPTY_ANSWER.getDesc());
        }

        // 判断答案是否正确
        int i = userMapper.selectByUserNameAndQuestionAndAnswer(username,question,answer);
        if (i <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.ERROR_ANSWER.getCode(),ConstCode.UserEnum.ERROR_ANSWER.getDesc());
        }

        // 正确则返回随机令牌
        String s = UUID.randomUUID().toString();
        //把令牌放入缓存中，这里使用的是Google的guava缓存，后期会使用redis替代
        TokenCache.set("token_" + username, s);

        return ServerResponse.successRS(ConstCode.DEFAULT_SUCCESS,s);
    }

    @Override
    public ServerResponse forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isEmpty(username)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_USERNAME.getCode(),ConstCode.UserEnum.EMPTY_USERNAME.getDesc());
        }
        if (StringUtils.isEmpty(passwordNew)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }
        if (StringUtils.isEmpty(forgetToken)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getCode(),ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getDesc());
        }

        // MD5加密
        String md5Code = MD5Util.getMD5Code(passwordNew);

        // 判断缓存中的token
        String token = TokenCache.get("token_"+username);
        if (token == null || token.equals("")){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.LOSE_EFFICACY.getCode(),ConstCode.UserEnum.LOSE_EFFICACY.getDesc());
        }
        if (! token.equals(forgetToken)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getCode(),ConstCode.UserEnum.UNLAWFULNESS_TOKEN.getDesc());
        }
        // 重置密码
        int i =userMapper.updateByUserNameAndPassWord(username,md5Code);

        if (i <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getCode(),ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getDesc());
        }

        // 成功返回
        return ServerResponse.successRS(ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getCode(),ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getDesc());
    }

    @Override
    public ServerResponse resetPassword(User user, String passwordOld, String passwordNew) {
        if (StringUtils.isEmpty(passwordOld)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }
        if (StringUtils.isEmpty(passwordNew)){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.EMPTY_PASSWORD.getCode(),ConstCode.UserEnum.EMPTY_PASSWORD.getDesc());
        }

        // MD5加密
        String md5Code = MD5Util.getMD5Code(passwordOld);
        String md5Code1 = MD5Util.getMD5Code(passwordNew);

        // 更新密码
        int i = userMapper.updateByUserNameAndPasswordOldAndPassWordNew(user.getUsername(),md5Code,md5Code1);

        if (i <= 0){
            return ServerResponse.defeatedRS
                    (ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getCode(),ConstCode.UserEnum.DEFEACTED_PASSWORDNEW.getDesc());
        }

        // 成功返回
        return ServerResponse.successRS(ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getCode(),ConstCode.UserEnum.SUCCESS_PASSWORDNEW.getDesc());
    }
}
