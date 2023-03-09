package com.fct.nowcoder.controller;


import com.fct.nowcoder.entity.DiscussPost;
import com.fct.nowcoder.entity.Page;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.DiscussPostService;
import com.fct.nowcoder.service.LikeService;
import com.fct.nowcoder.service.UserService;
import com.fct.nowcoder.util.NowcoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fct.nowcoder.util.CommunityConstant.ENTITY_TYPE_POST;

@Slf4j
@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Resource
    private LikeService likerService;

    //获取用户及帖子数据，跳转到主页面
    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用前，SpringMVC会自动实例化Model和Page,并将Page注入Model
        //所以在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.selectCountDiscussPost(0));
        page.setPath("/index");

        List<DiscussPost> discussPostList = discussPostService.selectDiscussPost(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();

        for(DiscussPost discussPost: discussPostList){
            Map<String,Object> map = new HashMap<>();
            String userId = discussPost.getUserId();
            User user = userService.selectById(Integer.parseInt(userId));

            map.put("post",discussPost);
            map.put("user",user);

            //查询帖子点赞数量
            long count = likerService.findEntityLikeCount(ENTITY_TYPE_POST,discussPost.getId());
            map.put("likeCount",count );

            discussPosts.add(map);
        }

        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

    @PostMapping("/ajax")
    @ResponseBody
    public String testAjax(String name, String password){
        log.info(name);
        log.info(password);
        return NowcoderUtil.getJsonString(0,"操作成功");
    }

    @GetMapping("/error")
    public String errorPage(){
        return "/error/500";
    }

}
