package com.kidscoding.common.exception;

import lombok.Getter;

/**
 * 业务异常 — 手动抛出，表示业务逻辑不允许的操作
 *
 * 用法：throw new BizException(400, "用户名已存在")
 * 全局异常处理器会捕获并返回 { "code": 400, "message": "用户名已存在" }
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
