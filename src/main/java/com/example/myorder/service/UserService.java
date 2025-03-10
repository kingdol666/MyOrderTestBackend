package com.example.myorder.service;

import com.example.myorder.model.User;
import com.example.myorder.repository.UserRepository;
import com.example.myorder.config.WxConfig;
import com.example.myorder.dto.LoginResult;
import com.example.myorder.dto.WxLoginResponse;
import com.example.myorder.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WxConfig wxConfig;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Value("${wx.login-url}")
    private String wxLoginUrl;

    // 微信登录验证
    @Transactional
    public LoginResult wxLoginVerify(String code, String nickName, String avatarUrl, Integer gender) {
        try {
            // 构建请求URL
            String url = String.format(wxConfig.getLoginUrl(), 
                wxConfig.getAppId(), 
                wxConfig.getAppSecret(), 
                code);
            
            // 发送请求并处理响应
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();
            
            // 使用ObjectMapper手动解析JSON
            ObjectMapper mapper = new ObjectMapper();
            WxLoginResponse wxLoginResponse = mapper.readValue(responseBody, WxLoginResponse.class);
            
            if (wxLoginResponse.getErrcode() != null) {
                throw new RuntimeException("微信登录失败：" + wxLoginResponse.getErrmsg());
            }
            
            // 处理登录逻辑
            String openId = wxLoginResponse.getOpenid();
            Optional<User> userOptional = userRepository.findByOpenId(openId);
            User user = userOptional.orElseGet(() -> {
                User newUser = new User();
                newUser.setOpenId(openId);
                newUser.setCreateTime(LocalDateTime.now());
                return newUser;
            });
            
            // 更新用户信息
            if (nickName != null) user.setNickName(nickName);
            if (avatarUrl != null) user.setAvatarUrl(avatarUrl);
            if (gender != null) user.setGender(gender);
            
            user = userRepository.save(user);
            
            // 生成token
            String token = jwtUtil.generateToken(openId);
            
            // 保存token到Redis
            tokenService.saveToken(token, openId);
            
            LoginResult loginResult = new LoginResult();
            loginResult.setToken(token);
            loginResult.setUser(user);
            loginResult.setOpenId(openId);
            loginResult.setSessionKey(wxLoginResponse.getSession_key());
            
            return loginResult;
            
        } catch (Exception e) {
            log.error("微信登录验证失败", e);
            throw new RuntimeException("微信登录验证失败", e);
        }
    }

    // 查找或创建微信用户
    private User findOrCreateWxUser(WxLoginResponse wxResponse) {
        Optional<User> existingUser = userRepository.findByOpenId(wxResponse.getOpenid());
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setLastLoginTime(LocalDateTime.now());
            return userRepository.save(user);
        }
        
        // 创建新用户
        User newUser = new User();
        newUser.setOpenId(wxResponse.getOpenid());
        newUser.setUnionId(wxResponse.getUnionid());
        newUser.setLastLoginTime(LocalDateTime.now());
        newUser.setIsEnabled(true);
        
        return userRepository.save(newUser);
    }

    // 更新微信用户信息
    @Transactional
    public User updateWxUserInfo(String openId, User userInfo) {
        User user = userRepository.findByOpenId(openId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        user.setNickName(userInfo.getNickName());
        user.setAvatarUrl(userInfo.getAvatarUrl());
        user.setGender(userInfo.getGender());
        user.setCountry(userInfo.getCountry());
        user.setProvince(userInfo.getProvince());
        user.setCity(userInfo.getCity());
        user.setLanguage(userInfo.getLanguage());
        
        return userRepository.save(user);
    }

    // 根据openId查询用户信息
    public User getUserByOpenId(String openId) {
        return userRepository.findByOpenId(openId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    // 退出登录
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            // 从Redis中移除token
            tokenService.removeToken(token);
        }
    }
} 