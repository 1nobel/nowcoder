package com.fct.nowcoder.controller;


import com.fct.nowcoder.dao.CommentMapper;
import com.fct.nowcoder.entity.Comment;
import com.fct.nowcoder.entity.DiscussPost;
import com.fct.nowcoder.entity.Page;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.CommentService;
import com.fct.nowcoder.service.DiscussPostService;
import com.fct.nowcoder.service.LikeService;
import com.fct.nowcoder.service.UserService;
import com.fct.nowcoder.util.HostHolder;
import com.fct.nowcoder.util.NowcoderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

import static com.fct.nowcoder.util.CommunityConstant.*;

@Controller
@RequestMapping("/discussPost")
public class DiscussPostController {

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private UserService userService;

    @Resource
    private CommentService commentService;

    @Resource
    private LikeService likeService;

    /**
     * 发布帖子
     * @param title 标题
     * @param content 内容
     * @return
     */
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

    /**
     * 查询帖子
     * @param discussPostId 帖子id
     * @param model
     * @return
     * 评论的分页信息(一页的数量setLimit,路径path,评论数量setRows)
     * 评论: 给帖子的评论
     * 回复: 给评论的评论
     */
    @GetMapping("/get/{discussPostId}")

    public String getDetail(@PathVariable("discussPostId") Integer discussPostId, Model model, Page page){

        User loginUser = HostHolder.getUser();



        DiscussPost discussPost = discussPostService.getDiscussPost(discussPostId);
        User user = userService.selectById(Integer.parseInt(discussPost.getUserId()));
        model.addAttribute("post",discussPost);
        model.addAttribute("user",user);

        //帖子的赞
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("postLike", likeCount);
        //帖子的点赞状态
        int postLikeStatus = loginUser == null ? 0 : likeService.findEntityLikeStatus(ENTITY_TYPE_POST, discussPostId, loginUser.getId());
        model.addAttribute("postLikeStatus", postLikeStatus);

        // 评论的分页信息
        page.setLimit(5);
        page.setPath("/discussPost/get/"+ discussPostId);
        page.setRows(discussPost.getCommentCount());

        //评论列表
        List<Comment> commentList = commentService.getCommentsByEntity(ENTITY_TYPE_POST, discussPostId, page.getOffset(), page.getLimit());
        //评论VO列表
        List<Map<String,Object>> commentVOList = new ArrayList<>();
        //遍历评论列表
        if(commentList != null){
            for(Comment comment : commentList){
                // 评论VO
                Map<String,Object> commentVO = new HashMap<>();
                // 评论
                commentVO.put("comment", comment);
                commentVO.put("user", userService.selectById(comment.getUserId()));

                //赞
                long count = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVO.put("likeCount", count);
                //评论的点赞状态
                int commentLikeStatus = loginUser == null? 0 : likeService.findEntityLikeStatus(ENTITY_TYPE_COMMENT, comment.getId(), loginUser.getId());
                commentVO.put("commentLikeStatus", commentLikeStatus);

                //回复列表
                List<Comment> replyList = commentService.getCommentsByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                //回复VO列表
                List<Map<String,Object>> replyVOList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply: replyList){

                        // 回复VO
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复内容
                        replyVo.put("reply",reply);
                        // 回复作者
                        replyVo.put("user",userService.selectById(reply.getUserId()));
                        // 回复目标
                        User target = (reply.getTargetId() == null|| reply.getTargetId() == 0)? null : userService.selectById(reply.getTargetId());
                        replyVo.put("target", target);

                        //赞
                        long replyCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("replyCount", replyCount);
                        //回复的点赞状态
                        int replyLikeStatus = loginUser == null? 0 : likeService.findEntityLikeStatus(ENTITY_TYPE_COMMENT, reply.getId(), loginUser.getId());
                        replyVo.put("replyLikeStatus", replyLikeStatus);


                        replyVOList.add(replyVo);
                    }
                }
                commentVO.put("replys",replyVOList);

                // 回复数量
                Integer countReply = commentService.getCommentCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("countReply", countReply);

                commentVOList.add(commentVO);
            }
        }

        model.addAttribute("comments", commentVOList);

        return "/site/discuss-detail";
    }
}
