package com.kidscoding.common.context;

/**
 * 用户上下文 — 在当前请求线程中存储用户信息
 *
 * ThreadLocal 原理：
 *   每个线程有自己的"小盒子"，互不干扰
 *   请求A的userId存在请求A的盒子里，请求B的userId存在请求B的盒子里
 *
 * 类比 Python:
 *   Flask 的 g 对象（g.user_id = 123，在请求内全局可访问）
 *   或者 threading.local()
 *
 * 流程：
 *   请求进来 → 拦截器解析 Token → 存入 UserContext → Controller/Service 随时取
 *   请求结束 → 拦截器清理 UserContext（防止内存泄漏）
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Integer> USER_TYPE = new ThreadLocal<>();

    public static void set(Long userId, Integer userType) {
        USER_ID.set(userId);
        USER_TYPE.set(userType);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static Integer getUserType() {
        return USER_TYPE.get();
    }

    public static void clear() {
        USER_ID.remove();
        USER_TYPE.remove();
    }
}
