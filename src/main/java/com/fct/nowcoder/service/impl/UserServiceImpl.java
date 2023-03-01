package com.fct.nowcoder.service.impl;


import com.fct.nowcoder.dao.LoginTicketMapper;
import com.fct.nowcoder.dao.UserMapper;
import com.fct.nowcoder.entity.LoginTicket;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.UserService;
import com.fct.nowcoder.util.MailClient;
import com.fct.nowcoder.util.NowcoderUtil;
import com.fct.nowcoder.util.ValidateCodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.fct.nowcoder.util.CommunityConstant.*;


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

    @Autowired
    private LoginTicketMapper loginTicketMapper;


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

    /**
     * 处理激活方法
     * @Param userId
     * @Param code
     * @Return Integer
     * 三种激活状态:
     *      1. 重复激活status=1
     *      2. 成功 激活码和传入的一致,修改status
     *      3. 失败
     */
    public Integer activation(Integer userId, String code){
        User user = userMapper.selectById(userId);

        // 重复激活
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }

        // 激活成功
        if(user.getActivationCode().equals(code)){
            user.setStatus(1);
            userMapper.updateById(user);
            return ACTIVATION_SUCCESS;
        }
        // 激活失败
        return ACTIVATION_FAILURE;
    }

    /**
     * 编写登录业务
     * @Param username
     * @Param password
     * @Param expireTime
     * @Return Map<String,String>
     *  1.空值判断: username,password
     *  2.判断用户是否存在
     *  3.判断用户状态 0为未激活
     *  4.验证密码
     *  5.生成登录凭证,insertLoginTicket
     */
     public Map<String,String> login(String username, String password, Long expireTime){
         Map<String,String> map = new HashMap<String,String>();

         //1. 空值判断
         if(StringUtils.isBlank(username)){
             map.put("usernameMsg","您输入的用户名为空");
             return map;
         }
         if(StringUtils.isBlank(password)){
             map.put("passwordMsg","密码不能为空");
             return map;
         }

         //2.判断用户是否存在
         User user = userMapper.selectByUsernameUser(username);
         if(user == null){
             map.put("usernameMsg","您输入的用户不存在");
             return map;
         }

         //3. 检查用户是否激活
         if (user.getStatus() == 0) {
             map.put("usernameMsg","该用户未激活,请点击邮件链接进行激活!");
             return map;
         }

         NowcoderUtil coder = new NowcoderUtil();
         //4.验证密码
         if(!user.getPassword().equals(coder.md5(password+user.getSalt()))){
             map.put("passwordMsg","您输入的密码不正确");
             return map;
         }

         //生成登录凭证
         LoginTicket loginTicket = new LoginTicket();
         loginTicket.setUserId(user.getId());
         loginTicket.setStatus(0);
         loginTicket.setExpired(LocalDateTime.now().plusMinutes(expireTime));
         loginTicket.setTicket(coder.generateUUID());
         loginTicketMapper.insertLoginTicket(loginTicket);

         map.put("ticket",loginTicket.getTicket());
         return map;
     }

     public void logout(String ticket){
         loginTicketMapper.updateTicket(ticket, 1);
     }

    /**
     * @Param yourEmail
     * 发送忘记密码验证码邮件
     *   1.判断邮箱是否已注册,未注册返回错误信息
     *   2.使用UUID生成6位数字验证码
     *   3.发送邮件
     */
    public Map<String,String> sendCode(String yourEmail,HttpSession session){
        Map<String,String> map = new HashMap<String,String>();

        //1.判断邮箱是否已注册,未注册返回错误信息
        User user = userMapper.selectByEmailUser(yourEmail);
        if(StringUtils.isBlank(user.toString())){
            map.put("emailMsg","该邮箱未注册,请先进行注册!");
            return map;
        }

        //2.随机生成6位数字验证码,并发送邮件
        String code = ValidateCodeUtils.generateValidateCode(6).toString();
        session.setAttribute("code_"+yourEmail,code);
        //发送邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        context.setVariable("code",code);
        String content = templateEngine.process("/mail/forget", context);
        mailClient.sendMessage(user.getEmail(),"密码邮件", content);

        return map;
    }

    @Override
    public LoginTicket selectTicket(String ticket) {
        return loginTicketMapper.selectTicket(ticket);

    }

    /**
     * 重置密码业务
     * @param yourEmail 邮箱
     * @param password 密码
     *   1.邮箱不能为空
     *   2.验证码要相等
     *   3.密码长度不能小于8位
     */
    public Map<String,String> updatePassword(String yourEmail, String password, String code, HttpSession httpSession){
        Map<String,String> map = new HashMap<>();
        if(StringUtils.isBlank(yourEmail)){
            map.put("emailMsg","请输入邮箱");
        }

        if(!code.equals(httpSession.getAttribute("code_"+yourEmail)) || code == null || httpSession.getAttribute("code_"+yourEmail) == null ){
            map.put("codeMsg","请检查邮箱和验证码");
        }

        if(password.length() < 8){
            map.put("passwordMsg","您的新密码位数必须大于8位");
        }
        User user = userMapper.selectByEmailUser(yourEmail);

        NowcoderUtil nowcoderUtil = new NowcoderUtil();
        password =  nowcoderUtil.md5(password + user.getSalt());
        userMapper.updateByEmail(yourEmail,password);

        return map;
    }
}
