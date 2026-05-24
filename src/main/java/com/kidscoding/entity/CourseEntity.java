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
@TableName("t_course")
public class CourseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("subtitle")
    private String subtitle;

    @TableField("course_type")
    private Integer courseType;

    @TableField("difficulty")
    private Integer difficulty;

    @TableField("total_chapters")
    @Builder.Default
    private Integer totalChapters = 0;

    @TableField("cover_url")
    private String coverUrl;

    @TableField("status")
    @Builder.Default
    private Integer status = 1;

    @TableField("enrolled_count")
    @Builder.Default
    private Integer enrolledCount = 0;

    @TableField("sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    @Builder.Default
    private Integer deleted = 0;
}
