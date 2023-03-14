package com.fct.nowcoder.controller;

import com.fct.nowcoder.entity.Event;
import com.fct.nowcoder.entity.Page;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.event.EventProducer;
import com.fct.nowcoder.service.FollowService;
import com.fct.nowcoder.service.UserService;
import com.fct.nowcoder.util.HostHolder;
import com.fct.nowcoder.util.NowcoderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

import static com.fct.nowcoder.util.CommunityConstant.ENTITY_TYPE_USER;
import static com.fct.nowcoder.util.CommunityConstant.TOPIC_FOLLOW;

/**
 * 关注
 */
@Controller
public class FollowController {

    @Resource
    private FollowService followService;

    @Resource
    private UserService userService;

    @Resource
    private EventProducer eventProducer;

    //关注
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = HostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        //触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserID(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);


        return NowcoderUtil.getJsonString(0, "已关注");
    }

    //取关
    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = HostHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return NowcoderUtil.getJsonString(0, "已取消关注");
    }

    //查看某用户的关注列表
    @GetMapping("/followee/{userId}")
    public String getFollowees(@PathVariable("userId")int userId, Page page, Model model){
        User user = userService.selectById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);

        page.setPath("/followee/" + userId );
        page.setLimit(5);
        page.setRows((int)followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> followees = followService.findFollowee(userId, page.getOffset(), page.getLimit());
        for(Map<String, Object> map : followees ){
            User u = (User)map.get("user");
            map.put("hasFollowed", hasFollowed(u.getId()));

        }

        model.addAttribute("users", followees);

        return "site/followee";
    }

    //查看某个用户的粉丝列表
    @GetMapping("/follower/{userId}")
    public String getFollowers(@PathVariable("userId")int userId, Page page, Model model){
        User user = userService.selectById((userId));
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);

        page.setPath("/follower" + userId);
        page.setLimit(5);
        page.setRows((int)followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> list = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        for(Map<String,Object> map : list){
            User user1 = (User)map.get("user");
            map.put("hasFollowed", hasFollowed(user1.getId()));

        }

        model.addAttribute("users", list);

        return "/site/follower";
    }

    public boolean hasFollowed(int userId){
        User user = HostHolder.getUser();
        if(user == null){
            return false;
        }
        return followService.hasFollowed(user.getId(), ENTITY_TYPE_USER, userId);

    }

}
