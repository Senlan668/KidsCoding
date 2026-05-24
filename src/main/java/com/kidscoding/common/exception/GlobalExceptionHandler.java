package com.kidscoding.common.exception;

import com.kidscoding.common.result.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * 类比 Python Flask:
 *   @app.errorhandler(Exception)
 *   def handle_error(e):
 *       return jsonify({"code": 500, "message": str(e)})
 *
 * 作用：所有 Controller 抛出的异常，都会被这里拦截
 * 统一转成 Result 格式返回给前端
 * 前端只需要判断 code == 0 就是成功，code != 0 就看 message
 *
 * @RestControllerAdvice = 给所有 @RestController 加上切面逻辑
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常（@Valid 触发的）
     * 把所有字段的错误信息拼成一句话
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.fail(400, message);
    }

    /**
     * 兜底：处理所有其他异常
     * 防止把 Java 堆栈信息暴露给前端（安全风险）
     */
    /**
     * 处理业务异常（代码里主动抛出的）
     * 如：用户名已存在、权限不足、余额不足等
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 兜底：处理所有其他异常
     * 防止把 Java 堆栈信息暴露给前端（安全风险）
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.fail(500, "服务器内部错误: " + e.getMessage());
    }
}
