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
@TableName("t_course_enrollment")
public class CourseEnrollmentEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("course_id")
    private Long courseId;

    @TableField("status")
    @Builder.Default
    private Integer status = 1;

    @TableField("progress_pct")
    @Builder.Default
    private Integer progressPct = 0;

    @TableField("enrolled_at")
    private LocalDateTime enrolledAt;

    @TableField("completed_at")
    private LocalDateTime completedAt;

    @TableLogic
    @TableField("deleted")
    @Builder.Default
    private Integer deleted = 0;
}
