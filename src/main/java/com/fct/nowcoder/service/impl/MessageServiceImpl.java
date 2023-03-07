package com.fct.nowcoder.service.impl;

import com.fct.nowcoder.dao.MessageMapper;
import com.fct.nowcoder.entity.Message;
import com.fct.nowcoder.service.MessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;

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
}
