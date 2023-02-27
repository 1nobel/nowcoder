package com.fct.nowcoder.controller;


import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.fct.nowcoder.util.CommunityConstant.ACTIVATION_REPEAT;
import static com.fct.nowcoder.util.CommunityConstant.ACTIVATION_SUCCESS;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

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


}
