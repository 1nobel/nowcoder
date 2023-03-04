package com.fct.nowcoder.controller;


import com.fct.nowcoder.annotation.LoginRequired;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.UserService;
import com.fct.nowcoder.util.HostHolder;
import com.fct.nowcoder.util.NowcoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Value("${nowcoder.upload}")
    private String upload;

    @Value("${nowcoder.path}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Resource
    private UserService userService;

    @LoginRequired
    @GetMapping("/setting")
    public String getSetting(){
        return "/site/setting";
    }

    //图片上传
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error","您还没有选择图片!");
            return "/site/setting";
        }

        //获取图片的名字
        String fileName = headerImage.getOriginalFilename();
        //截取图片后缀名
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","您上传的图片格式不正确");
            return "/site/setting";
        }

        //生成随机文件名
        fileName = NowcoderUtil.generateUUID() + suffix;
        //确定文件存放的路径
        File file = new File(upload + "/" + fileName);
        try {
            headerImage.transferTo(file);
        } catch (IOException e) {
            log.error("上传文件失败");
            throw new RuntimeException("上传图片失败,服务器异常",e);
        }

        //更新当前头像的访问路径
        //http://localhost:8010/nowcoder/user/header/xxx.png
        User user = HostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        user.setHeaderUrl(headerUrl);
        userService.updateById(user);

        return "redirect:/index";
    }

    //图片下载
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName = upload + "/" + fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+ suffix);
        OutputStream os = null;
        FileInputStream inputStream = null;
        try {
            os = response.getOutputStream();
            inputStream = new FileInputStream(fileName);
            byte[] bytes = new byte[1024];
            int b = 0;
            while((b = inputStream.read(bytes))!= -1){
                os.write(bytes,0,b);
            }
            os.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //修改密码
    @LoginRequired
    @PostMapping("/update")
    public String updatePwd(String newPassword,String oldPassword,String confirmPassword, Model model){
        User user = HostHolder.getUser();
        Map<String, String> map = userService.updatePassword(newPassword, user, oldPassword,confirmPassword);

        if(!map.isEmpty()){
            model.addAttribute("oldPasswordMsg",map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg",map.get("newPasswordMsg"));
            model.addAttribute("confirmPasswordMsg",map.get("confirmPasswordMsg"));
            return "/site/setting";
        }
        return "redirect:/index";
    }
}
