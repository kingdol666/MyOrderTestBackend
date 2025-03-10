package com.example.myorder.dto;

import com.example.myorder.model.User;
import lombok.Data;

@Data
public class LoginResult {
    private String token;
    private User user;
    private String openId;
    private String sessionKey;
} 