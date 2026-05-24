package com.kidscoding.config;

import com.kidscoding.common.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置 — 注册拦截器 + 跨域配置
 *
 * 白名单：这些路径不需要 Token 就能访问
 *   /api/v1/auth/**  → 注册和登录
 *   /api/hello      → 测试接口
 *   /api/health     → 健康检查
 *
 * 其他所有路径都必须带 Token
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")           // 拦截所有 /api/** 请求
                .excludePathPatterns(                  // 白名单（不需要 Token）
                        "/api/v1/auth/**",             // 注册、登录
                        "/api/hello",                  // 测试
                        "/api/health"                  // 健康检查
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 跨域配置：允许前端（React）访问后端 API
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
