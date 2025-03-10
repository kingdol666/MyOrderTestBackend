package com.example.myorder.utils;

import com.example.myorder.model.User;
import com.example.myorder.service.TokenService;
import com.example.myorder.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerifyUtil {
    private final TokenService tokenService;
    private final UserService userService;

    public boolean validateToken(HttpServletRequest request,
                                        HttpServletResponse response,
                                        TokenService tokenService) {
        String token = getToken(request, response);
        if (token == null) return false;

        // 3. 验证Redis有效性
        if (!tokenService.isTokenValid(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        // 4. 获取并验证openId
        String openId = tokenService.getOpenIdFromRedis(token);
        if (openId == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        request.setAttribute("openId", openId);
        return true;
    }

    public String getToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization");

        // 1. 验证Header格式
        if (!isValidHeader(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return null;
        }

        // 2. 提取有效token
        token = extractToken(token);
        return token;
    }

    private static boolean isValidHeader(String token) {
        return token != null && token.startsWith("Bearer ");
    }

    private static String extractToken(String header) {
        return header.substring(7);
    }

    public Long getUserIdByToken(String token) {
        String openIdFromRedis = tokenService.getOpenIdFromRedis(token);
        User ThisUser = userService.getUserByOpenId(openIdFromRedis);
        return ThisUser.getId();
    }
}
