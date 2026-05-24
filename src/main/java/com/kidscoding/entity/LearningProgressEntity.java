package com.kidscoding.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_learning_progress")
public class LearningProgressEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("chapter_id")
    private Long chapterId;

    @TableField("course_id")
    private Long courseId;

    /** 0-未开始 1-进行中 2-已完成 */
    @TableField("status")
    @Builder.Default
    private Integer status = 0;

    @TableField("score")
    @Builder.Default
    private Integer score = 0;

    @TableField("time_spent_seconds")
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    @TableField("completed_at")
    private LocalDateTime completedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    @Builder.Default
    private Integer deleted = 0;
}
