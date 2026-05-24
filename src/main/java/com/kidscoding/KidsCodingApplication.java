package com.kidscoding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * KidsCoding 后端服务入口
 *
 * 类比 Python Flask:
 *   Flask 里你写 app = Flask(__name__)，然后 app.run()
 *   Spring Boot 里你写一个 main 方法，调用 SpringApplication.run()
 *
 * @SpringBootApplication 是一个"组合注解"，它帮我们做了三件事：
 *   1. 告诉 Spring "这是一个 Spring Boot 应用"
 *   2. 自动配置（不用手动配一堆 XML）
 *   3. 自动扫描当前包下的所有组件（Controller、Service 等）
 */
@SpringBootApplication
public class KidsCodingApplication {

    public static void main(String[] args) {
        SpringApplication.run(KidsCodingApplication.class, args);
    }
}
