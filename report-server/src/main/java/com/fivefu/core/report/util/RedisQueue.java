package com.fivefu.core.report.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis实现消息队列
 */
public class RedisQueue {

    @Autowired
    RedisTemplate redisTemplate;


    private final String queueKey;


    public RedisQueue(String name){
        this.queueKey=name;
    }

    public void push(String message) {
        redisTemplate.opsForList().leftPush(queueKey, message);
    }

    public String pop() {
        return (String) redisTemplate.opsForList().rightPop(queueKey);
    }

    public long size() {
        return redisTemplate.opsForList().size(queueKey);
    }

    public String peek() {
        return (String) redisTemplate.opsForList().index(queueKey, -1);
    }



}
