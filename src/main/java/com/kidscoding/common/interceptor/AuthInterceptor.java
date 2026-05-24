package com.kidscoding.common.interceptor;

import com.kidscoding.common.context.UserContext;
import com.kidscoding.common.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * 认证拦截器 — 每个请求进来先验证 Token
 *
 * 类比 Python Flask:
 *   @app.before_request
 *   def check_token():
 *       token = request.headers.get("Authorization")
 *       if not token:
 *           return jsonify({"code": 401, "message": "未登录"}), 401
 *
 * 工作流程：
 *   1. 从请求头取 Authorization: Bearer eyJhbGciOiJI...
 *   2. 提取 Token，用 JwtUtil 解析
 *   3. 把 userId 和 userType 存入 UserContext
 *   4. 放行请求
 *   5. 请求结束后清理 UserContext
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 请求直接放行（浏览器跨域预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头取 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, 401, "未登录，请先登录");
            return false;
        }

        String token = authHeader.substring(7); // 去掉 "Bearer " 前缀

        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            sendError(response, 401, "Token 无效或已过期");
            return false;
        }

        // 解析 Token，存入 UserContext
        Long userId = jwtUtil.getUserId(token);
        Integer userType = jwtUtil.parseToken(token).get("userType", Integer.class);
        UserContext.set(userId, userType);

        return true;  // 放行
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清理，防止内存泄漏
        UserContext.clear();
    }

    private void sendError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = Map.of(
                "code", code,
                "message", message,
                "data", "",
                "timestamp", System.currentTimeMillis()
        );
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
