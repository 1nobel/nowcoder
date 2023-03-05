package com.fct.nowcoder.service;

import com.fct.nowcoder.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByEntity(Integer entityType, Integer entityId, Integer offset, Integer limit);

    Integer getCommentCountByEntity(Integer entityType, Integer entityId);
}
