package com.fct.nowcoder.service;

import com.fct.nowcoder.entity.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService {
    List<Comment> getCommentsByEntity(Integer entityType, Integer entityId, Integer offset, Integer limit);

    Integer getCommentCountByEntity(Integer entityType, Integer entityId);

    Integer addCommentAndUpdateCommentCount(Comment comment);

    Comment selectCommentById(Integer id);
}
