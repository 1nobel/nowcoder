package com.fct.nowcoder.service.impl;

import com.fct.nowcoder.dao.MessageMapper;
import com.fct.nowcoder.entity.Message;
import com.fct.nowcoder.service.MessageService;
import com.fct.nowcoder.util.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Message> getConversation(int userId, int offset, int limit) {
        return messageMapper.selectConversation(userId, offset, limit);
    }

    @Override
    public Integer getConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<Message> getLetterLetters(String conversationId, Integer offset, Integer limit) {
        return messageMapper.selectLetterLetters(conversationId, offset, limit);
    }

    @Override
    public Integer getLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public Integer getLetterUnreadCount(Integer userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    @Override
    public Integer insertMessage(Message message) {

        if(message.getFromId() == 1){
            return messageMapper.addMessage(message);
        }
        //敏感词过滤
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));

        return messageMapper.addMessage(message);
    }

    @Override
    public Integer updateStatusMessage(List<Integer> ids, Integer status) {
        return messageMapper.updateMessageStatus(ids, status);
    }

    @Override
    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectMessage(userId, topic);
    }

    @Override
    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    @Override
    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    @Override
    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}
