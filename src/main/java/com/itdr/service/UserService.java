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
}
