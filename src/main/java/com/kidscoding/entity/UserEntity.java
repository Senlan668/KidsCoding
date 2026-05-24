package com.kidscoding.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体 — 对应数据库 t_user 表
 *
 * JPA 注解 → MyBatis-Plus 注解 对照：
 *   @Entity / @Table(name)    →  @TableName("t_user")
 *   @Id                       →  @TableId(type = IdType.ASSIGN_ID)
 *   @Column(name, nullable)   →  @TableField("column_name")
 *   无                        →  @TableLogic（逻辑删除）
 *   @Version (JPA)            →  @Version（一样）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user")
public class UserEntity {

    @TableId(type = IdType.ASSIGN_ID)   // 雪花算法自动生成 ID
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password_hash")
    @Builder.Default
    private String passwordHash = "";

    @TableField("nickname")
    private String nickname;

    @TableField("discriminator")
    private String discriminator;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("user_type")
    private Integer userType;

    @TableField("status")
    @Builder.Default
    private Integer status = 1;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableLogic  // 逻辑删除：查询时自动加 WHERE deleted = 0
    @TableField("deleted")
    @Builder.Default
    private Integer deleted = 0;

    @Version  // 乐观锁：UPDATE 时自动加 WHERE version = #{version}
    @TableField("version")
    @Builder.Default
    private Integer version = 0;
}
