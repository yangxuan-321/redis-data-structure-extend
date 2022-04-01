package org.moda.redis.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Redis工具类
 *
 * @author lwl
 */
@Slf4j
@Component
public class RedisUtil {
    public static final String TREE_BASE_KEY = "redis-data:tree:";

    public static RedisTemplate<String, String> redisTemplate;

    public RedisUtil(RedisTemplate<String, String> redisTemplate) {
        RedisUtil.redisTemplate = redisTemplate;
        // 提前作个redis预热。不然第一次会特别慢
        new Thread(() -> RedisUtil.redisTemplate.hasKey("1")).start();
    }



    public static <V> boolean hashPutAll(String key, Map<String, V> hash) {
        try {
            HashOperations<String, String, V> hashOperations = redisTemplate.<String, V>opsForHash();
            hashOperations.putAll(key, hash);
            return true;
        } catch (Exception e) {
            log.error("hashSet cache error", e);
            return false;
        }
    }

}
