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
@TableName("t_course_chapter")
public class CourseChapterEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("course_id")
    private Long courseId;

    @TableField("title")
    private String title;

    @TableField("chapter_order")
    private Integer chapterOrder;

    @TableField("chapter_type")
    private Integer chapterType;

    @TableField("xp_reward")
    @Builder.Default
    private Integer xpReward = 50;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    @Builder.Default
    private Integer deleted = 0;
}
