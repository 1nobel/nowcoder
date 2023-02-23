package com.fct.nowcoder.service.impl;

import com.fct.nowcoder.dao.UserMapper;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectById(Integer userId) {
        return userMapper.selectById(userId);
    }
}
