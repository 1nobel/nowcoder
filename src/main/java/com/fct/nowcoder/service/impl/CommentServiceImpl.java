package com.fct.nowcoder.service.impl;

import com.fct.nowcoder.dao.CommentMapper;
import com.fct.nowcoder.entity.Comment;
import com.fct.nowcoder.service.CommentService;
import com.fct.nowcoder.service.DiscussPostService;
import com.fct.nowcoder.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fct.nowcoder.util.CommunityConstant.ENTITY_TYPE_POST;

@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Comment> getCommentsByEntity(Integer entityType, Integer entityId, Integer offset, Integer limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    @Override
    public Integer getCommentCountByEntity(Integer entityType, Integer entityId) {
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    /**
     * 添加评论并且更新评论数量
     * @param comment
     * @return
     */
    @Override
    @Transactional
    public Integer addCommentAndUpdateCommentCount(Comment comment) {


        // 1. 简单的校验
        if(comment == null){
            try {
                throw new IllegalAccessException("参数不能为空");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // 2. 敏感词过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));


        Integer row = commentMapper.insertComment(comment);

        //更新帖子数量
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            Integer count = commentMapper.selectCountByEntity(ENTITY_TYPE_POST, comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }

        return row;
    }
}
