package com.fct.nowcoder.controller;


import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.UserService;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static com.fct.nowcoder.util.CommunityConstant.*;

@Slf4j
@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String proName;

    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }

    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);

        if(map == null || map.isEmpty()){
            model.addAttribute("rightMsg","您已成功注册账号，请点如下链接进行激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }

        else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("pwdMsg",map.get("pwdMsg"));
            model.addAttribute("email",map.get("email"));
            return "/site/register";
        }
    }

    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") Integer userId, @PathVariable("code") String code){
        Integer activation = userService.activation(userId, code);

        if (activation == ACTIVATION_SUCCESS){
            model.addAttribute("rightMsg","激活成功,您的账号可以正常使用了!");
            model.addAttribute("target","/login");
        }else if(activation == ACTIVATION_REPEAT){
            model.addAttribute("rightMsg","无效操作,该账号已经激活过了");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("rightMsg","激活失败,您提供的激活码不正确");
            model.addAttribute("target","/index");
        }

        return "/site/operate-result";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        session.setAttribute("kaptcha",text);

        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //登录功能表现层
    @PostMapping("/login")
    public String login(Model model, String username, String password, String code, boolean rememberme,
                                    HttpSession session, HttpServletResponse response){
        model.addAttribute("username",username);
        model.addAttribute("password",password);

        //1. 验证验证码
        String kaptcha = (String)session.getAttribute("kaptcha");
        if(StringUtils.isBlank(kaptcha)|| StringUtils.isBlank(code)|| !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确!");
            return "/site/login";
        }

        //2.检查账号,密码
        log.info("{}",rememberme);
        Long expiredMinutes = rememberme ? REMEMBER_EXPIRED_MINUTES: DEFAULT_EXPIRED_MINUTES;


        Map<String, String> map = userService.login(username, password, expiredMinutes);
        //2.1 map里存放着登录验证数据就将其存入Cookie并重定向到首页
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket"));
            cookie.setPath("proName");
            cookie.setMaxAge(expiredMinutes.intValue());
            response.addCookie(cookie);
            return "redirect:/index";
        }

        //2.2 map里存放着错误信息就将其放入model
        model.addAttribute("usernameMsg",map.get("usernameMsg"));
        model.addAttribute("passwordMsg",map.get("passwordMsg"));
        return "/site/login";
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }

    @GetMapping("/forget")
    public String forget(){
        return "/site/forget";
    }


    /**
     * 忘记密码--发送邮件
     * @param model
     * @param yourEmail
     * @param session
     * @return
     */
    @GetMapping("/emailCode")
    public String sendCode(Model model, String yourEmail,HttpSession session){
        model.addAttribute("yourEmail",yourEmail);

        Map<String, String> map = userService.sendCode(yourEmail,session);
        if(StringUtils.isNotBlank(map.toString())) {
            model.addAttribute("emailMsg", map.get("emailMsg"));
        }
        log.warn("验证码发送成功!");
        return "/forget";

    }

    /**
     * 修改密码
     * @param yourEmail
     * @param code
     * @param password
     * @param httpSession
     * @param model
     * @return
     */
    @PostMapping("/updatePwd")
    public String updatePwd(String yourEmail, String code, String password,HttpSession httpSession,Model model){
        model.addAttribute("yourEmail",yourEmail);

        Map<String, String> map = userService.updatePassword(yourEmail, password, code, httpSession);
        if(map != null || !map.isEmpty()){
                model.addAttribute("emailMsg",map.get("emailMsg"));
                model.addAttribute("codeMsg",map.get("codeMsg"));
                model.addAttribute("passwordMsg",map.get("passwordMsg"));
                return "/forget";
        }

        return "/login";
    }
}
