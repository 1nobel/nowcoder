package com.fct.nowcoder.controller;

import com.fct.nowcoder.annotation.LoginRequired;
import com.fct.nowcoder.entity.Comment;
import com.fct.nowcoder.entity.DiscussPost;
import com.fct.nowcoder.entity.Event;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.event.EventProducer;
import com.fct.nowcoder.service.CommentService;
import com.fct.nowcoder.service.DiscussPostService;
import com.fct.nowcoder.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;

import static com.fct.nowcoder.util.CommunityConstant.*;

/**
 * 评论
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private EventProducer eventProducer;

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

        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setUserID(user.getId())
                .setData("postId",discussPostId);
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.getDiscussPost(comment.getEntityId());
            event.setEntityUserId(Integer.parseInt(target.getUserId()));
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.selectCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        return "redirect:/discussPost/get/" +discussPostId;
    }
}
