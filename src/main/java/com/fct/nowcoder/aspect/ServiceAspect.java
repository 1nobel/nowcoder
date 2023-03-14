package com.fct.nowcoder.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 使用AOP思想记录日志
 * 切面
 */
@Component
@Aspect
@Slf4j
public class ServiceAspect {

    @Pointcut("execution(* com.fct.nowcoder.service.impl.*Impl.*(..))")
    public void pointcut(){}

    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        // 用户[1.2.3.4],在[某时刻],访问了[com.fct.nowcoder.service.xxx()]
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();


        //消费者调用kafka不会有request
        if (attributes == null){
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        //ip
        String ip = request.getRemoteHost();
        //now
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //方法名
        String name = joinPoint.getSignature().getDeclaringTypeName()+"."+ joinPoint.getSignature().getName();
        log.info(String.format("用户[%s],在[%s],访问了[%s].",ip, now, name));
    }

}
