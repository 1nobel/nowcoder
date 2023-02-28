package com.fct.nowcoder.dao;

import com.fct.nowcoder.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginTicketMapper {
    Integer insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectTicket(String ticket);

    Boolean updateTicket(String ticket,Integer status);
}
