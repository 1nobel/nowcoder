package com.fct.nowcoder;

import com.fct.nowcoder.dao.DiscussPostMapper;
import com.fct.nowcoder.dao.LoginTicketMapper;
import com.fct.nowcoder.dao.UserMapper;
import com.fct.nowcoder.entity.DiscussPost;
import com.fct.nowcoder.entity.LoginTicket;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.DiscussPostService;
import com.fct.nowcoder.service.UserService;
import com.fct.nowcoder.util.MailClient;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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

    @Autowired
    private StringEncryptor stringEncryptor;


    @Autowired
    private MailClient mailClient;

    @Resource
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

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

    @Test
    void testjiami(){
        //将密码加密
        String s = stringEncryptor.encrypt("hjk");
        log.info("{}",s);

        //将密码解密
        String pass = stringEncryptor.decrypt(s);
        log.info("{}",pass);
    }

    //测试邮箱
    @Test
    void testMailClient(){
        mailClient.sendMessage("jia869928@gmail.com","Test","Welcom!!!");
    }

    //测试发送HTML邮件
    @Test
    void testHtmlMail(){
        //设置html的内容
        Context context = new Context();
        context.setVariable("username","niubei");
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        //将内容content写入发送邮件方法中
        mailClient.sendMessage("jia869928@gmail.com","Test", content);

    }

    //测试LoginTicket
    @Test
    void testLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(15);
        loginTicket.setTicket("qqq");
        loginTicket.setStatus(0);
        loginTicket.setExpired(LocalDateTime.now().plusMinutes(10) );
//
//        Integer flag = loginTicketMapper.insertLoginTicket(loginTicket);
//        log.warn("{}",flag);
//
//        LoginTicket ticket = loginTicketMapper.selectTicket("qqq");
//        log.error("{}",ticket);

        Boolean aBoolean = loginTicketMapper.updateTicket("qaqq", 1);
        log.error("{}",aBoolean);
    }

    //根据邮箱修改密码
//    @Test
//    void updatePwd(){
//        userService.updatePassword("jia869928@gmail.com","123456","");
//    }

    //发送邮箱验证码
//    @Test
//    void testSendCode(){
//        userService.sendCode("jia869928@gmail.com");
//    }

}
