package com.fct.nowcoder.service;

import com.fct.nowcoder.entity.User;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface UserService {

    User selectById(Integer userId);

    User selectByUsernameUser(String username);

    User selectByEmailUser(String email);

    Boolean insertUser(User user);

    Map<String,Object> register(User user);

    Integer activation(Integer userId, String code);

    Map<String,String> login(String username, String password, Long expireTime);

    void logout(String ticket);

    Map<String,String> updatePassword(String yourEmail, String password, String code, HttpSession httpSession);

    Map<String,String> sendCode(String yourEmail,HttpSession session);
}
