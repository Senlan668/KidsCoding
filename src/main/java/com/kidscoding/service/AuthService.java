package com.kidscoding.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kidscoding.common.exception.BizException;
import com.kidscoding.common.util.JwtUtil;
import com.kidscoding.entity.UserEntity;
import com.kidscoding.repository.UserMapper;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 认证服务 — 注册和登录
 *
 * 密码安全核心：BCrypt
 *   明文: "123456"
 *   BCrypt 加密后: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
 *
 *   BCrypt 的特点：
 *   1. 每次加密结果不同（同密码两次加密，结果不一样）→ 防彩虹表攻击
 *   2. 自带盐值（不需要额外存 salt 字段）
 *   3. 慢（故意设计得慢，暴力破解成本极高）
 *
 *   类比 Python:
 *     import bcrypt
 *     hashed = bcrypt.hashpw(password.encode(), bcrypt.gensalt())
 *     bcrypt.checkpw(password.encode(), hashed)  # True/False
 */
@Service
public class AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Random random = new Random();

    public AuthService(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 注册
     * 1. 检查用户名是否重复
     * 2. 密码 BCrypt 加密
     * 3. 生成 discriminator
     * 4. 存入数据库
     */
    public UserEntity register(String username, String password, String nickname, String role) {
        // 检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getUsername, username)
        );
        if (count > 0) {
            throw new BizException(400, "用户名已存在: " + username);
        }

        // 密码加密（永远不存明文！）
        String encodedPassword = passwordEncoder.encode(password);

        UserEntity user = UserEntity.builder()
                .username(username)
                .passwordHash(encodedPassword)
                .nickname(nickname)
                .discriminator(generateDiscriminator())
                .userType(mapUserType(role))
                .build();
        userMapper.insert(user);

        // 返回时清掉密码（不能把密码哈希返回给前端）
        user.setPasswordHash(null);
        return user;
    }

    /**
     * 登录
     * 1. 根据用户名查用户
     * 2. 验证密码
     * 3. 生成 JWT Token 返回
     */
    public Map<String, Object> login(String username, String password) {
        // 根据用户名查找用户
        UserEntity user = userMapper.selectOne(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getUsername, username)
        );
        if (user == null) {
            throw new BizException(401, "用户名或密码错误");
        }

        // 验证密码（BCrypt 自动对比明文和哈希）
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BizException(401, "用户名或密码错误");
        }

        // 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUserType());

        // 返回 Token + 用户信息（清掉密码）
        user.setPasswordHash(null);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    private String generateDiscriminator() {
        return String.format("%04d", random.nextInt(9999) + 1);
    }

    private Integer mapUserType(String role) {
        return switch (role.toUpperCase()) {
            case "CHILD" -> 1;
            case "PARENT" -> 2;
            case "TEACHER" -> 3;
            default -> 1;
        };
    }
}
