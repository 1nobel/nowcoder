package com.fct.nowcoder.util;

/**
 * 常量类
 */
public interface CommunityConstant {

    /**
     * 激活成功
     */
    Integer ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    Integer ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    Integer ACTIVATION_FAILURE = 2;

    /**
     *  默认状态的登录凭证的超时时间
     */
    Long DEFAULT_EXPIRED_MINUTES = 60 * 12L;

    /**
     * 记住状态的登录凭证超时时间
     */
    Long REMEMBER_EXPIRED_MINUTES = 60 * 24 * 30L;

    /**
     * 实体类型:帖子
     */
    Integer ENTITY_TYPE_POST = 1;

    /**
     * 实体类型:评论
     */
    Integer ENTITY_TYPE_COMMENT = 2;

    /**
     * 回复的赞
     */
    Integer ENTITY_TYPE_REPLY = 3;
}
