package com.fct.nowcoder.service;

import java.util.List;
import java.util.Map;

public interface FollowService {

    void follow(int userId, int entityType, int entityId);

    void unfollow(int userId, int entityType, int entityId);

    long findFolloweeCount(int userId, int entityType);

    long findFollowerCount(int entityType, int entityId);

    boolean hasFollowed(int userId, int entityType, int entityId);

    List<Map<String, Object>> findFollowee(int userId, int offset, int limit);

    List<Map<String, Object>> findFollowers(int userId, int offset, int limit);
}
