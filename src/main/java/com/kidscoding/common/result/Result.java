package com.kidscoding.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应封装 —— 所有 API 都返回这个格式
 *
 * 类比 Python: 相当于你定义了一个统一的字典结构
 *   {
 *       "code": 0,
 *       "message": "操作成功",
 *       "data": { ... },
 *       "timestamp": 1700000000
 *   }
 *
 * 为什么要统一格式？
 *   前端只需要判断 code == 0 就是成功，code != 0 就是失败
 *   不管调哪个接口，处理逻辑都一样，不用每个接口特殊处理
 *
 * 四个 Lombok 注解做了什么？（类比 Python dataclass）
 *   @Data         → 自动生成 getter/setter/toString/equals/hashCode
 *   @Builder      → 允许 Result.builder().code(0).message("ok").build()
 *   @NoArgsConstructor  → 自动生成无参构造函数
 *   @AllArgsConstructor → 自动生成全参构造函数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private int code;        // 业务码：0=成功，非0=各种错误
    private String message;  // 人类可读的消息
    private T data;          // 业务数据（泛型 T，什么类型都行）
    private long timestamp;  // 时间戳

    /**
     * 成功响应（带数据）
     * Result.ok(user) → { "code": 0, "message": "操作成功", "data": user, "timestamp": ... }
     */
    public static <T> Result<T> ok(T data) {
        return Result.<T>builder()
                .code(0)
                .message("操作成功")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 失败响应
     * Result.fail(400, "参数错误") → { "code": 400, "message": "参数错误", "data": null, ... }
     */
    public static <T> Result<T> fail(int code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
