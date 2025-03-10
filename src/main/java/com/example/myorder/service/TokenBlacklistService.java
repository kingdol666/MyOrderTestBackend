package com.example.myorder.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class TokenBlacklistService {
    private final ConcurrentMap<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    public void addToBlacklist(String token) {
        tokenBlacklist.put(token, System.currentTimeMillis());
    }

    public boolean isBlacklisted(String token) {
        return tokenBlacklist.containsKey(token);
    }

    // 清理过期的token（可以定时执行）
    public void cleanupExpiredTokens(long expirationMillis) {
        long now = System.currentTimeMillis();
        tokenBlacklist.entrySet().removeIf(entry -> 
            (now - entry.getValue()) > expirationMillis);
    }
} 