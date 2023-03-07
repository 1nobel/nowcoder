package com.fct.nowcoder.service;

import com.fct.nowcoder.entity.Message;

import java.util.List;

public interface MessageService {

    List<Message> getConversation(int userId, int offset, int limit);

    // 查询当前用户的会话数量
    Integer getConversationCount(int userId);

    // 查询某个会话包含的私信列表
    List<Message> getLetterLetters(String conversationId, Integer offset, Integer limit);

    // 查询某个会话包含的私信数量
    Integer getLetterCount(String conversationId);

    // 查询未读私信的数量
    Integer getLetterUnreadCount(Integer userId, String conversationId);
}
