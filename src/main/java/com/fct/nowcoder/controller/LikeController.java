package com.fct.nowcoder.controller;

import com.fct.nowcoder.annotation.LoginRequired;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.LikeService;
import com.fct.nowcoder.util.HostHolder;
import com.fct.nowcoder.util.NowcoderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Resource
    private LikeService likeService;

    /**
     * 处理点赞
     * @param entityType 实体类型 1为帖子 2为评论
     * @param entityId 实体id
     * @return json
     */
    @PostMapping("/like")
    @ResponseBody
//    @LoginRequired
    public String like(int entityType, int entityId, int entityUserId){
        User user = HostHolder.getUser();

        if(user == null ){
            return NowcoderUtil.getJsonString(1,"");
        }

        //点赞
        likeService.like(user.getId(),entityType, entityId, entityUserId);
        //数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //状态
        int status = likeService.findEntityLikeStatus(entityType, entityId, user.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", status);

        return NowcoderUtil.getJsonString(0, null, map);
    }
}
