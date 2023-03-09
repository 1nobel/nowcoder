package com.fct.nowcoder.config;


import com.fct.nowcoder.controller.interceptor.AlphaInterceptor;
import com.fct.nowcoder.controller.interceptor.LoginRequiredInterceptor;
import com.fct.nowcoder.controller.interceptor.LoginTicketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private AlphaInterceptor alphaInterceptor;

    @Resource
    private LoginTicketInterceptor loginTicketInterceptor;

    @Resource
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(alphaInterceptor)
//                .excludePathPatterns("/**/*.css"
//                                     ,"/**/*.js"
//                                     ,"/**/*.png"
//                                     ,"/**/*.jpg"
//                                     ,"/**/*.jpeg")
//                .addPathPatterns("/register","/login");

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css"
                        ,"/**/*.js"
                        ,"/**/*.png"
                        ,"/**/*.jpg"
                        ,"/**/*.jpeg"
                        );

        registry.addInterceptor(loginRequiredInterceptor);

    }
}
