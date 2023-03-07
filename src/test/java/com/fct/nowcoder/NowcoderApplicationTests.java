package com.fct.nowcoder;

import com.fct.nowcoder.dao.DiscussPostMapper;
import com.fct.nowcoder.dao.LoginTicketMapper;
import com.fct.nowcoder.dao.MessageMapper;
import com.fct.nowcoder.dao.UserMapper;
import com.fct.nowcoder.entity.DiscussPost;
import com.fct.nowcoder.entity.LoginTicket;
import com.fct.nowcoder.entity.Message;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.DiscussPostService;
import com.fct.nowcoder.service.UserService;
import com.fct.nowcoder.util.MailClient;
import com.fct.nowcoder.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
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

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Resource
    private MessageMapper messageMapper;

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

    @Test
    public void testSensitiveFilter() {
        String text = "傻逼,二货,大撒旦发生v赌博,转***卖";
        String filter  = sensitiveFilter.filter(text);
        log.warn(filter);
    }

    @Test
    public void testMessage(){
        // 查看会话列表
        List<Message> messages = messageMapper.selectConversation(111, 0, 20);
        int i = 1;
        for(Message message: messages){

            log.info("message"+(i++)+":{}",message);
        }

        // 查看当前用户的会话数量
        Integer count = messageMapper.selectConversationCount(175);
        log.info("111用户的会话数量为:{}",count);

        // 查看某个会话的私信列表
        List<Message> messages1 = messageMapper.selectLetterLetters("111_113", 0, 10);
        int j = 1;
        for(Message message : messages1){
            log.info("会话111_113的私信列表"+(j++)+":{}",message);
        }

        // 查看某个会话包含的私信数量
        Integer count1 = messageMapper.selectLetterCount("111_113");
        log.info("会话111_113包含的私信数量:{}",count1);

        // 查看某个会话未读私信数量
        Integer count2 = messageMapper.selectLetterUnreadCount(131, "111_131");
        log.info("会话111_131中用户131未读的消息数量:{}",count2);

    }
}
