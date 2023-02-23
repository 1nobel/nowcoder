package com.fct.nowcoder;

import com.fct.nowcoder.dao.DiscussPostMapper;
import com.fct.nowcoder.dao.UserMapper;
import com.fct.nowcoder.entity.DiscussPost;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.DiscussPostService;
import com.fct.nowcoder.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@Slf4j
@SpringBootTest
class NowcoderApplicationTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostService discussPostService;


    @Autowired
    private UserService userService;

    //测试帖子查询
    @Test
    void contextLoads() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPost(101, 0, 5);
        for (DiscussPost discusspost:discussPosts
             ) {
            System.out.println(discusspost);
        }

        Integer number = discussPostMapper.selectCountDiscussPost(101);
        System.out.println(number);
    }

    //测试查询用户信息
    @Test
    void testUser(){
        User user = userMapper.selectById(101);
        log.info("user:{}",user);
    }

    @Test
    void test01(){
        List<DiscussPost> discussPosts = discussPostService.selectDiscussPost(101, 0, 5);
        for (DiscussPost discusspost:discussPosts
        ) {
            System.out.println(discusspost);
        }

        User user = userService.selectById(101);
        log.info("user:{}",user);
    }

}
