package com.fct.nowcoder.util;

import com.fct.nowcoder.entity.User;

/**
 * 创建ThreadLocal对象并重写其方法
 */
public  class HostHolder {

    private static ThreadLocal<User> users = new ThreadLocal<>();

    public static void setUser(User user){
        users.set(user);
    }

    public static User getUser(){
        return users.get();
    }

    public static void clean(){
        users.remove();
    }

}
