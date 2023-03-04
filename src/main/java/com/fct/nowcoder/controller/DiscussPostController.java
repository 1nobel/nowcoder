package com.fct.nowcoder.controller;


import com.fct.nowcoder.entity.DiscussPost;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.DiscussPostService;
import com.fct.nowcoder.util.HostHolder;
import com.fct.nowcoder.util.NowcoderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;

@Controller
@RequestMapping("/discussPost")
public class DiscussPostController {

    @Resource
    private DiscussPostService discussPostService;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = HostHolder.getUser();
        if(user == null){
            return NowcoderUtil.getJsonString(403,"您还没有登录");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId().toString());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //报错未来统一处理
        return NowcoderUtil.getJsonString(0,"发布帖子成功!!");
    }
}
