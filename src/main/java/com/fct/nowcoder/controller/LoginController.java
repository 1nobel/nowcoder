package com.fct.nowcoder.controller;


import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }

    @GetMapping("/abc")
    public void das( @Validated User user){

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
}
