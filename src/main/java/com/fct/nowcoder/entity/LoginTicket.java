package com.fct.nowcoder.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LoginTicket implements Serializable {

    public Integer id;

    public Integer userId;

    public String ticket;

    //状态,0有效,1无效
    public Integer status;

    //过期时间
    public LocalDateTime expired;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getExpired() {
        return expired;
    }

    public void setExpired(LocalDateTime expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "LoginTicket{" +
                "id=" + id +
                ", userId=" + userId +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                '}';
    }
}
