package com.kidscoding.controller;

import com.kidscoding.common.result.Result;
import com.kidscoding.entity.CourseChapterEntity;
import com.kidscoding.entity.CourseEntity;
import com.kidscoding.entity.CourseEnrollmentEntity;
import com.kidscoding.entity.LearningProgressEntity;
import com.kidscoding.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /** 创建课程 — POST /api/v1/courses */
    @PostMapping
    public Result<CourseEntity> createCourse(@RequestBody Map<String, Object> body) {
        CourseEntity course = courseService.createCourse(
                (String) body.get("title"),
                (String) body.get("subtitle"),
                ((Number) body.get("courseType")).intValue(),
                ((Number) body.get("difficulty")).intValue(),
                (String) body.get("coverUrl")
        );
        return Result.ok(course);
    }

    /** 添加章节 — POST /api/v1/courses/{courseId}/chapters */
    @PostMapping("/{courseId}/chapters")
    public Result<CourseChapterEntity> addChapter(@PathVariable Long courseId,
                                                   @RequestBody Map<String, Object> body) {
        CourseChapterEntity chapter = courseService.addChapter(
                courseId,
                (String) body.get("title"),
                ((Number) body.get("chapterType")).intValue()
        );
        return Result.ok(chapter);
    }

    /** 课程列表 — GET /api/v1/courses?courseType=1 */
    @GetMapping
    public Result<List<CourseEntity>> listCourses(
            @RequestParam(required = false) Integer courseType) {
        return Result.ok(courseService.listCourses(courseType));
    }

    /** 课程详情 — GET /api/v1/courses/{id} */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getCourseDetail(@PathVariable Long id) {
        return Result.ok(courseService.getCourseDetail(id));
    }

    /** 报名课程 — POST /api/v1/courses/{id}/enroll */
    @PostMapping("/{id}/enroll")
    public Result<CourseEnrollmentEntity> enrollCourse(@PathVariable Long id) {
        return Result.ok(courseService.enrollCourse(id));
    }

    /** 完成章节 — POST /api/v1/chapters/{id}/complete */
    @PostMapping("/chapters/{id}/complete")
    public Result<LearningProgressEntity> completeChapter(@PathVariable Long id) {
        return Result.ok(courseService.completeChapter(id));
    }

    /** 我的学习进度 — GET /api/v1/courses/{id}/progress */
    @GetMapping("/{id}/progress")
    public Result<Map<String, Object>> getMyProgress(@PathVariable Long id) {
        return Result.ok(courseService.getMyProgress(id));
    }
}
