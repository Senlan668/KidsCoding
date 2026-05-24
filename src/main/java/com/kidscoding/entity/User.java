package com.kidscoding.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体
 *
 * 类比 Python:
 *   @dataclass
 *   class User:
 *       id: int
 *       username: str
 *       nickname: str
 *       role: str
 *
 * 四个 Lombok 注解自动生成：
 *   getter/setter、toString、equals、hashCode
 *   Builder 模式、无参构造、全参构造
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;           // 用户ID
    private String username;   // 用户名（登录用）
    private String nickname;   // 昵称（显示用）
    private String role;       // 角色：PARENT / CHILD / TEACHER
}
