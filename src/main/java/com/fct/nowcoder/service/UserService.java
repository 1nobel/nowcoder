package com.fct.nowcoder.service;

import com.fct.nowcoder.entity.User;

public interface UserService {

    User selectById(Integer userId);
}
