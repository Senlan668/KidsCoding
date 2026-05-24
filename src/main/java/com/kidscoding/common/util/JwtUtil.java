package com.kidscoding.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类 — 生成和解析 Token
 *
 * JWT 是什么？（用食堂饭卡类比）
 *   Session 方案（传统）:
 *     你办了一张饭卡，食堂记本子上"卡号123 → 张三"
 *     每次刷卡，食堂翻本子查你是谁 → 食堂要维护这本子（服务器存 Session）
 *
 *   JWT 方案（现代）:
 *     你办了一张饭卡，卡面直接印着"张三，有效期到2026年底"
 *     每次刷卡，食堂看卡面就知道你是谁 → 食堂不用记本子（服务器无状态）
 *     卡不能伪造 → 上面有防伪标记（签名）
 *
 * JWT 结构（三段用 . 连接）:
 *   Header.Payload.Signature
 *   eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEyMyw... .SflKxwRJSMeKKF2QT4fw
 *
 *   Header:    {"alg": "HS256"}           → 算法
 *   Payload:   {"userId": 123, "exp": ...} → 数据（用户ID、过期时间）
 *   Signature: HMACSHA256(header.payload, secret) → 签名（防篡改）
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token
     * generateToken(userId, userType) → "eyJhbGciOiJI..."
     */
    public String generateToken(Long userId, Integer userType) {
        return Jwts.builder()
                .subject(String.valueOf(userId))       // 主题：用户ID
                .claim("userType", userType)            // 自定义字段：用户类型
                .issuedAt(new Date())                   // 签发时间
                .expiration(new Date(System.currentTimeMillis() + expiration))  // 过期时间
                .signWith(getSigningKey())              // 用密钥签名
                .compact();
    }

    /**
     * 解析 Token，返回里面的数据
     * parseToken("eyJhbGciOiJI...") → {userId: 123, userType: 1, ...}
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中提取用户 ID
     */
    public Long getUserId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    /**
     * 验证 Token 是否有效（过期或伪造会抛异常）
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
