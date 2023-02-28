package com.fct.nowcoder.dao;

import com.fct.nowcoder.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(Integer userId);

    User selectByUsernameUser(String username);

    User selectByEmailUser(String email);

    Boolean insertUser(User user);

    Boolean updateById(User user);

    Integer updateByEmail(String email,String password);
}
