package com.fivefu.core.report.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * redis实现消息队列,异步解藕
 */
public class RedisQueue {

    @Autowired
    RedisTemplate redisTemplate;




}
