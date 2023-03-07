package com.fct.nowcoder.controller;

import com.fct.nowcoder.entity.Message;
import com.fct.nowcoder.entity.Page;
import com.fct.nowcoder.entity.User;
import com.fct.nowcoder.service.MessageService;
import com.fct.nowcoder.service.UserService;
import com.fct.nowcoder.util.HostHolder;
import com.fct.nowcoder.util.NowcoderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

@Controller
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private UserService userService;

    /**
     私信列表
     * 1. 获取用户信息
     * 2. 设置分页信息(数据总数需要查询会话)
     * 3. 查询会话列表
     * 4. 根据会话列表遍历查询:
     *          (1). 当前会话的私信数量
     *          (2). 当前会话的未读消息数量
     *          (3). 当前会话对方的信息target
     * 5. 所有会话的未读消息数量总和
     */
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page){



        //1. 获取用户信息
        User user = HostHolder.getUser();

        //2. 设置分页信息
        page.setLimit(5);
        page.setRows(messageService.getConversationCount(user.getId()));
        page.setPath("/letter/list");

        //3. 查询会话列表
        List<Message> conversationList = messageService.getConversation(user.getId(), page.getOffset(), page.getLimit());

        //4. 建立map类型的List集合存放各种信息
        List<Map<String, Object>> conversations = new ArrayList<>();
        if(!conversationList.isEmpty()){
            for(Message message : conversationList){
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);

                //4.1 当前会话的私信数量
                Integer letterCount = messageService.getLetterCount(message.getConversationId());
                map.put("letterCount", letterCount);

                //4.2 当前会话的未读消息数量
                Integer unreadCount = messageService.getLetterUnreadCount(user.getId(), message.getConversationId());
                map.put("unreadCount", unreadCount);

                //4.3 当前会话对方的信息target
                Integer target = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target",userService.selectById(target));

                conversations.add(map);
            }
        }

        model.addAttribute("conversations",conversations);

        //5. 查询未读消息数量
        Integer unreadCountTotal = messageService.getLetterUnreadCount(user.getId(), null);
        model.addAttribute("unreadCountTotal",unreadCountTotal);

        return "/site/letter";
    }

    /**
     * 私信详情页面
     * @param conversationId 会话id
     * @param page 页面
     * @param model 模型
     * @return
     * 1. 设置分页信息
     * 2. 查询私信列表并进行遍历:
     *              (1). 私信信息
     *              (2). 某条私信发送人
     * 3. 查询当前私信,当前用户与谁进行会话
     */
    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){

        //1. 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.getLetterCount(conversationId));

        //2. 私信列表
        List<Message> letterLetters = messageService.getLetterLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if(!letterLetters.isEmpty()){
            for(Message message : letterLetters){
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.selectById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        //3. 当前会话的发送对象
        model.addAttribute("target", this.getLetterTarget(conversationId));

        //4. 修改未读消息数量
        List<Integer> letterIds = getLetterIds(letterLetters);
        if(!letterIds.isEmpty()){
            messageService.updateStatusMessage(letterIds,1);
        }

        return "/site/letter-detail";
    }

    // 获取当前会话的发送对象
    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        User user = HostHolder.getUser();
        if(id0 == user.getId()){
            return userService.selectById(id1);
        }
        return userService.selectById(id0);
    }

    // 获取当前会话未读消息的id
    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        User user = HostHolder.getUser();

        if(!letterList.isEmpty()){
            for(Message message : letterList){
                if((Objects.equals(user.getId(), message.getToId())) && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String senMessage(String toName, String content){
        User user = userService.selectByUsernameUser(toName);
        if(user == null){
            return NowcoderUtil.getJsonString(1, "您输入的用户不存在");
        }
//        Integer.valueOf("as");
        // 获取当前用户
        User fromUser = HostHolder.getUser();

        // 设置私信信息
        Message message = new Message();
        message.setFromId(fromUser.getId());
        message.setToId(user.getId());
        message.setStatus(0);
        message.setCreateTime(new Date());
        message.setContent(content);

        if(fromUser.getId() < user.getId()){
            message.setConversationId(message.getFromId()+"_"+ message.getToId());
        }else{
            message.setConversationId(message.getToId()+"_"+ message.getFromId());
        }

        messageService.insertMessage(message);

        return NowcoderUtil.getJsonString(0, "信息发送成功");
    }

}
