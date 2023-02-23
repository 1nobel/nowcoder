package com.fct.nowcoder.dao;

import com.fct.nowcoder.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(Integer userId);
}
