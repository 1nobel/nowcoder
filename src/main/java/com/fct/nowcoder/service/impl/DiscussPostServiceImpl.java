package com.fct.nowcoder.service.impl;

import com.fct.nowcoder.dao.DiscussPostMapper;
import com.fct.nowcoder.entity.DiscussPost;
import com.fct.nowcoder.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Override
    public List<DiscussPost> selectDiscussPost(Integer userId, Integer offset, Integer limit) {
        return discussPostMapper.selectDiscussPost(userId, offset, limit);
    }

    @Override
    public Integer selectCountDiscussPost(Integer userId) {
        return discussPostMapper.selectCountDiscussPost(userId);
    }
}
