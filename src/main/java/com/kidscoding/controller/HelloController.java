package com.kidscoding.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第一个 Controller
 *
 * 类比 Python Flask:
 *
 *   # Flask 写法
 *   @app.route("/api/hello")
 *   def hello():
 *       return "Hello KidsCoding!"
 *
 *   # Spring Boot 写法
 *   @RestController + @GetMapping("/api/hello")
 *   public String hello() { return "Hello KidsCoding!"; }
 *
 * 区别只是语法不同，做的事情完全一样：注册一个路由，返回一个字符串
 */
@RestController                     // 告诉 Spring：这个类是处理 HTTP 请求的
@RequestMapping("/api")             // 这个类里所有接口的 URL 前缀都是 /api
public class HelloController {

    @GetMapping("/hello")           // 处理 GET 请求，路径是 /api/hello
    public String hello() {
        return "Hello KidsCoding!";
    }
}
