package com.fct.nowcoder.exception;

import com.fct.nowcoder.util.NowcoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@ControllerAdvice(annotations = {Controller.class})
public class GlobalException {

    @ExceptionHandler({Exception.class})
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发生异常");
        for(StackTraceElement element: e.getStackTrace()){
            log.error(element.toString());
        }

        //判断浏览器发送的是异步请求(返回json)还是普通请求(返回页面)
        String header = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(header)){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(NowcoderUtil.getJsonString(1,"服务器异常"));
        }else{
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }

}
