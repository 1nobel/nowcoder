package com.fct.nowcoder.dao;

import com.fct.nowcoder.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表,针对每个会话只返回一条最新的私信
    List<Message> selectConversation(int userId, int offset, int limit);

    // 查询当前用户的会话数量
    Integer selectConversationCount(int userId);

    // 查询某个会话包含的私信列表
    List<Message> selectLetterLetters(String conversationId, Integer offset, Integer limit);

    // 查询某个会话包含的私信数量
    Integer selectLetterCount(String conversationId);

    // 查询未读私信的数量
    Integer selectLetterUnreadCount(Integer userId, String conversationId);

    // 添加私信
    Integer addMessage(Message message);

    // 修改私信状态
    Integer updateMessageStatus(List<Integer> ids, Integer status);
}
