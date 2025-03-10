package com.example.myorder.controller;

import com.example.myorder.model.User;
import com.example.myorder.service.UserService;
import com.example.myorder.dto.LoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    // 微信登录验证
    @PostMapping("/wx-login")
    public ResponseEntity<LoginResult> wxLoginVerify(
            @RequestParam String code,
            @RequestParam(required = false) String nickName,
            @RequestParam(required = false) String avatarUrl,
            @RequestParam(required = false) Integer gender) {
        return ResponseEntity.ok(userService.wxLoginVerify(code, nickName, avatarUrl, gender));
    }

    // 更新微信用户信息
    @PostMapping("/wx-update-info")
    public ResponseEntity<User> updateWxUserInfo(
            @RequestParam String openId,
            @RequestBody User userInfo) {
        return ResponseEntity.ok(userService.updateWxUserInfo(openId, userInfo));
    }

    // 根据openId获取用户信息
    @GetMapping("/wx/{openId}")
    public ResponseEntity<User> getUserByOpenId(@PathVariable String openId) {
        return ResponseEntity.ok(userService.getUserByOpenId(openId));
    }

    // 退出登录
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token.replace("Bearer ", ""));
        System.out.println("退出成功： token：" + token);
        return ResponseEntity.ok("退出成功");
    }
} 