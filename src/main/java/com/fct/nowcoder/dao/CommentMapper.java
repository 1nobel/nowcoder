package com.fct.nowcoder.dao;

import com.fct.nowcoder.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    Integer selectCountByEntity(int entityType, int entityId);

    //添加评论
    Integer insertComment(Comment comment);

    //根据id查评论
    Comment selectCommentById(Integer id);

}
