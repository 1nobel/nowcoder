package com.fct.nowcoder.service.impl;

import com.fct.nowcoder.dao.DiscussPostMapper;
import com.fct.nowcoder.entity.DiscussPost;
import com.fct.nowcoder.service.DiscussPostService;
import com.fct.nowcoder.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<DiscussPost> selectDiscussPost(Integer userId, Integer offset, Integer limit) {
        return discussPostMapper.selectDiscussPost(userId, offset, limit);
    }

    @Override
    public Integer selectCountDiscussPost(Integer userId) {
        return discussPostMapper.selectCountDiscussPost(userId);
    }

    @Override
    public Integer addDiscussPost(DiscussPost discussPost) {
        //参数不能为null
        if(discussPost == null){
            try {
                throw new Exception("帖子的内容为null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);

    }

    @Override
    public DiscussPost getDiscussPost(Integer id) {
        return  discussPostMapper.getDiscussPost(id);
    }
}
