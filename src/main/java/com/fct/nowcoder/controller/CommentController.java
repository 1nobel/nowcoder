package com.fct.nowcoder.controller;

import com.fct.nowcoder.annotation.LoginRequired;
import com.fct.nowcoder.entity.Comment;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.CommentService;
import com.fct.nowcoder.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    //发评论
    @PostMapping("/add/{discussPostId}")
    @LoginRequired
    public String addComment(@PathVariable("discussPostId") Integer discussPostId, Comment comment){

        User user = HostHolder.getUser();

        //补充comment
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
//        comment.setTargetId(discussPostId);
        commentService.addCommentAndUpdateCommentCount(comment);

        return "redirect:/discussPost/get/" +discussPostId;
    }
}
