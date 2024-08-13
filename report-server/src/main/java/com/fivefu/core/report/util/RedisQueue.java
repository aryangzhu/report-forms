package com.example.core.report.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * redis实现消息队列
 */
@Component
public class RedisQueue {

    @Autowired
    RedisTemplate redisTemplate;


    public void push(String queueKey,String message) {
        redisTemplate.opsForList().leftPush(queueKey, message);
    }

    public String pop(String queueKey) {
        return (String) redisTemplate.opsForList().rightPop(queueKey);
    }

    public long size(String queueKey) {
        return redisTemplate.opsForList().size(queueKey);
    }

    public String peek(String queueKey) {
        return (String) redisTemplate.opsForList().index(queueKey, -1);
    }

    public String toJson(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public< T> T fromJson(String json, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
