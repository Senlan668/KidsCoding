package com.kidscoding.controller;

import com.kidscoding.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 健康检查 Controller
 *
 * 这个接口的作用：告诉监控系统"我还活着"
 * 大厂生产环境必备，Kubernetes 会定时调这个接口，如果挂了就自动重启
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = Map.of(
                "status", "UP",
                "service", "kidscoding-server",
                "version", "0.0.1"
        );
        return Result.ok(data);
    }
}
