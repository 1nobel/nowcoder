package com.fct.nowcoder.controller.interceptor;

import com.fct.nowcoder.entity.LoginTicket;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.UserService;
import com.fct.nowcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();

        //1. 判断cookie为空则拦截
        if (cookies == null){
            return true;
        }

        //2. 获取ticket
        String ticket = null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("ticket")){
                ticket = cookie.getValue();
                break;
            }
        }

        if(ticket == null){
            return true;
        }

        //3. 获取凭证并检查凭证是否有效
        LoginTicket loginTicket = userService.selectTicket(ticket);
        if(ticket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().isAfter(LocalDateTime.now()) ){
            //4. 根据userid查询用户
            User user = userService.selectById(loginTicket.getUserId());

            //5. 将user存入ThreadLocal
            HostHolder.setUser(user);
        }


        return true;
    }

    //controller之后,模板引擎之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = HostHolder.getUser();
        if (user != null && modelAndView != null){
            modelAndView.addObject("loginUser", user);
        }
    }

    //在模板引擎之后清除ThreadLocal对象
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HostHolder.clean();
    }
}
