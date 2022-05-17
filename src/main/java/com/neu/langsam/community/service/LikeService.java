package com.neu.langsam.community.service;

import com.neu.langsam.community.util.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    //点赞
    public void like(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType,entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if(isMember) {
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        } else {
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }
    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人对某实体类的点赞状态,1 -- 点赞了，0 -- 没点赞
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }
}
