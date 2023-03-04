package com.fct.nowcoder.dao;


import com.fct.nowcoder.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //查询帖子
    List<DiscussPost> selectDiscussPost(Integer userId,Integer offset,Integer limit);

    //查询帖子总数
    //当只有一个参数且要在<if>中使用，必须要使用@Param注解取别名
    Integer selectCountDiscussPost(@Param("userId")Integer userId);

    //增加帖子
    int insertDiscussPost(DiscussPost discussPost);
}
