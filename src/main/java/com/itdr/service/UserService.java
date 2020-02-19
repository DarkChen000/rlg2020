package com.itdr.service;

import com.itdr.common.ServerResponse;
import com.itdr.pojo.User;

import javax.servlet.http.HttpSession;

public interface UserService {
    ServerResponse login(String username, String password);

    ServerResponse register(User u);

    ServerResponse getInformation(User user);

    ServerResponse logout(HttpSession session,User user);

    ServerResponse updateInformation(User user, String email, String phone, String question, String answer);

    ServerResponse checkVaild(String str, String type);

    ServerResponse<User> forgetGetQuestion(String username);

    ServerResponse forgetCheckAnswer(String username, String question, String answer);

    ServerResponse forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse resetPassword(User user, String passwordOld, String passwordNew);
}
