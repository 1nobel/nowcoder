package com.fct.nowcoder.service;

public interface LikeService {

    // 点赞
    void like(int userId, int entityType, int entityId, int entityUserId);

    // 查询某实体的点赞数量
    long findEntityLikeCount(int entityType, int entityId);

    // 查询某实体的点赞状态
    int findEntityLikeStatus(int entityType, int entityId, int userId);

    int findUserLikeCount(int userId);
}
