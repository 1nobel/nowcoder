package com.fct.nowcoder.service.impl;

import com.fct.nowcoder.service.LikeService;
import com.fct.nowcoder.util.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void like(int userId, int entityType, int entityId, int entityUserId) {
//        String redisKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//
//        // 查询当前用户是否在redis中存在(即是否点赞)
//        boolean isMember = redisTemplate.opsForSet().isMember(redisKey,userId);
//        if(isMember){
//            redisTemplate.opsForSet().remove(redisKey, userId);
//        }else{
//            redisTemplate.opsForSet().add(redisKey,userId);
//        }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);

                //开启事务
                operations.multi();

                if(isMember){
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);
                    redisTemplate.opsForValue().decrement(userLikeKey);
                }else{
                    redisTemplate.opsForSet().add(entityLikeKey, userId);
                    redisTemplate.opsForValue().increment(userLikeKey);
                }

                //关闭事务
                return operations.exec();
            }
        });
    }

    // 查询作品点赞的数量
    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        String redisKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);

        return redisTemplate.opsForSet().size(redisKey);
    }

    // 查询某人的点赞状态
    @Override
    public int findEntityLikeStatus(int entityType, int entityId, int userId) {
        String redisKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);

        int status = redisTemplate.opsForSet().isMember(redisKey,userId) ? 1 : 0;

        return status;
    }

    @Override
    // 查询某个用户获得的赞
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer)redisTemplate.opsForValue().get(userLikeKey);

        return count == null? 0 :count.intValue();
    }


}
