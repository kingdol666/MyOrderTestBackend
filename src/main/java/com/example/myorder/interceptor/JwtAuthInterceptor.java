package com.example.myorder.interceptor;

import com.example.myorder.util.JwtUtil;
import com.example.myorder.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行登录和退出登录接口
        if (request.getRequestURI().contains("/api/user/wx-login") ||
            request.getRequestURI().contains("/api/user/logout")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        token = token.substring(7);

        // 验证token是否在Redis中有效
        if (!tokenService.isTokenValid(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        // 从Redis获取openId
        String openId = tokenService.getOpenIdFromRedis(token);
        if (openId == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        request.setAttribute("openId", openId);
        return true;
    }
} 