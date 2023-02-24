package com.fct.nowcoder.service.impl;


import com.fct.nowcoder.dao.UserMapper;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.UserService;
import com.fct.nowcoder.util.MailClient;
import com.fct.nowcoder.util.NowcoderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    //1.注入邮件类
    @Autowired
    private MailClient mailClient;

    //2. 注入模板引擎
    @Resource
    private TemplateEngine templateEngine;

    //3. 从配置文件中引入域名
    @Value("${nowcoder.path}")
    private String domain;

    //4. 从配置文件中引入项目名
    @Value("${server.servlet.context-path}")
    private String proName;

    @Override
    public User selectById(Integer userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User selectByUsernameUser(String username) {
        return userMapper.selectByUsernameUser(username);
    }

    @Override
    public User selectByEmailUser(String email) {
        return userMapper.selectByEmailUser(email);
    }

    @Override
    public Boolean insertUser(User user) {
        return userMapper.insertUser(user);
    }

    /**
     * 编写注册业务
     * @Param user
     * @Return Map<String,Object>
     * 空值判断，user，username,password,email
     * 验证账号，用户名是否存在
     * 验证邮箱，邮箱是否被注册
     * 注册用户： 密码加密，setSalt,setPassword,setType(0),setStatus(0),设置激活码setActivationCode()
     *
     * 发送邮件：挪用test中的代码
     */
    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<String,Object>();

        //空值判断
        //1. user空值判断
        if(StringUtils.isBlank(user.toString())){
            map.put("userMsg","用户不能为空");
            return map;
        }

        //2. username空值判断
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }

        //3. 密码空值判断
        if(StringUtils.isBlank(user.getPassword())){
            map.put("pwdMsg","密码不能为空");
            return map;
        }

        //验证账户是否存在
        User user1 = this.selectByUsernameUser(user.getUsername());
        if(user1 != null){
            map.put("usernameMsg","用户名已存在");
            return map;
        }
        //验证邮箱是否已被注册
        User user2 = this.selectByEmailUser(user.getEmail());
        if(user2 != null){
            map.put("email","邮箱已被注册！！！");
            return map;
        }

        //注册用户，密码加密，setSalt,setPassword,setType(0),setStatus(0),设置激活码setActivationCode()
        user.setSalt(NowcoderUtil.generateUUID().substring(0,5));

        //将密码拼接并加密
        String pwd = NowcoderUtil.md5(user.getPassword() + user.getSalt());
        user.setPassword(pwd);

        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(NowcoderUtil.generateUUID());
        String headerUrl = String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000));
        user.setHeaderUrl(headerUrl);
        user.setCreateTime(LocalDateTime.now());

        this.insertUser(user);

        //发送邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //拼接邮件，http://localhost:8010/nowcoder/activation/xxx/code
        String url = domain + proName + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMessage(user.getEmail(),"激活账号", content);

        return map;

    }
}
