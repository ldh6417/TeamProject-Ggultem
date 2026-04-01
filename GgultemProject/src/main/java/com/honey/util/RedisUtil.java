package com.honey.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate; // Redis와 통신하는 도구

    // 데이터 저장 (이메일, 인증번호, 유효시간 5분)
    public void setDataExpire(String key, String value, long duration) {
        redisTemplate.opsForValue().set(key, value, duration, TimeUnit.MINUTES);
    }

    // 데이터 가져오기
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 데이터 삭제 (인증 완료 시)
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}