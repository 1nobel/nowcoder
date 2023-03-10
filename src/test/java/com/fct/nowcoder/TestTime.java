package com.fct.nowcoder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest
public class TestTime {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    public void test1(){
        long timeMillis = System.currentTimeMillis();

        redisTemplate.opsForZSet().add("test:time", 11, System.currentTimeMillis());


        System.out.println(System.currentTimeMillis());

        Double score = redisTemplate.opsForZSet().score("test:time", 11);
        System.out.println(new Date(score.longValue()));
    }
}
