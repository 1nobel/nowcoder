package com.fct.nowcoder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 测试cookie
 */
@Controller
@RequestMapping("/cookie")
public class CookieController {

    @GetMapping("/set")
    public void testCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("pwd","abc123");
        cookie.setPath("/nowcoder/cookie");
        cookie.setMaxAge(60*7);
        response.addCookie(cookie);
    }

    @GetMapping("/get")
    @ResponseBody
    public String testGetCookie(@CookieValue String pwd){
        System.out.println(pwd);
        return "aaaaaa";
    }
}
