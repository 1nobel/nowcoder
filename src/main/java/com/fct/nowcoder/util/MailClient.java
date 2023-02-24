package com.fct.nowcoder.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 邮件类，用于发邮件,定义发邮件方法
 */
@Slf4j
@Component
public class MailClient {

    @Resource
    private JavaMailSender mailSender;

    //发件人
    @Value("${spring.mail.username}")
    private String from;

    public void sendMessage(String to,String subject, String content){
        //定义邮件内容，初始为空
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        //使用MimeMessageHelper帮助构建
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        //设置发件人,内容，收件人
        try {
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content,true);

            //发邮件
            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("发送邮件失败："+e.getMessage());
        }
    }
}
