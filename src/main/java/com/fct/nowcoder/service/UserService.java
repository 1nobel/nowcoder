package com.fct.nowcoder.service;

import com.fct.nowcoder.entity.User;

import java.util.Map;

public interface UserService {

    User selectById(Integer userId);

    User selectByUsernameUser(String username);

    User selectByEmailUser(String email);

    Boolean insertUser(User user);

    Map<String,Object> register(User user);
}
