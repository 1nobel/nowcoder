package com.fct.nowcoder.service;

import com.fct.nowcoder.entity.DiscussPost;

import java.util.List;

public interface DiscussPostService {
    List<DiscussPost> selectDiscussPost(Integer userId, Integer offset, Integer limit);
    Integer selectCountDiscussPost(Integer userId);
    Integer addDiscussPost(DiscussPost discussPost);
    DiscussPost getDiscussPost(Integer id);
    Integer updateCommentCount(Integer id, Integer commentCount);
}
