package com.kidscoding.controller;

import com.kidscoding.common.result.Result;
import com.kidscoding.dto.LoginRequest;
import com.kidscoding.dto.RegisterRequest;
import com.kidscoding.entity.UserEntity;
import com.kidscoding.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证 Controller — 注册和登录
 *
 * 这两个接口不需要 Token 就能访问（白名单）
 * 其他所有接口后续都要带 Token 才能访问
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 注册 — POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public Result<UserEntity> register(@Valid @RequestBody RegisterRequest request) {
        UserEntity user = authService.register(
                request.getUsername(),
                request.getPassword(),
                request.getNickname(),
                request.getRole()
        );
        return Result.ok(user);
    }

    /**
     * 登录 — POST /api/v1/auth/login
     * 返回 { "token": "eyJ...", "user": {...} }
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> data = authService.login(
                request.getUsername(),
                request.getPassword()
        );
        return Result.ok(data);
    }
}
