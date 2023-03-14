package com.fct.nowcoder.controller.interceptor;

import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.MessageService;
import com.fct.nowcoder.util.HostHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Resource
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = HostHolder.getUser();
        if (user != null) {
            Integer letterUnread = messageService.getLetterUnreadCount(user.getId(), null);
            Integer noticeUnread = messageService.findNoticeUnreadCount(user.getId(), null);
            Integer allUnreadCount = letterUnread + noticeUnread;
            modelAndView.addObject("allUnreadCount", allUnreadCount);
        }
    }
}