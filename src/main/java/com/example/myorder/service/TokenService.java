package com.example.myorder.service;

import com.example.myorder.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_PREFIX = "token:";
    private static final long TOKEN_EXPIRE_HOURS = 24;
    private final JwtUtil jwtUtil;

    public void saveToken(String token, String openId) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, openId, TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    public String getOpenIdFromRedis(String token) {
        String key = TOKEN_PREFIX + token;
        return redisTemplate.opsForValue().get(key);
    }

    public void removeToken(String token) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }

    public boolean isTokenValid(String token) {
        String key = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key))&&jwtUtil.validateToken(token);
    }
} 