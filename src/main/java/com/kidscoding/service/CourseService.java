package com.kidscoding.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kidscoding.common.context.UserContext;
import com.kidscoding.common.exception.BizException;
import com.kidscoding.entity.*;
import com.kidscoding.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程服务 — 核心业务逻辑
 *
 * 新概念：@Transactional 事务
 * 事务 = "要么全部成功，要么全部失败"
 * 类比：银行转账 → 扣钱和加钱必须同时成功，不能扣了钱没加
 */
@Service
public class CourseService {

    private final CourseMapper courseMapper;
    private final CourseChapterMapper chapterMapper;
    private final CourseEnrollmentMapper enrollmentMapper;
    private final LearningProgressMapper progressMapper;

    public CourseService(CourseMapper courseMapper,
                         CourseChapterMapper chapterMapper,
                         CourseEnrollmentMapper enrollmentMapper,
                         LearningProgressMapper progressMapper) {
        this.courseMapper = courseMapper;
        this.chapterMapper = chapterMapper;
        this.enrollmentMapper = enrollmentMapper;
        this.progressMapper = progressMapper;
    }

    // ==================== 课程管理 ====================

    /**
     * 创建课程（管理员/教师用）
     */
    @Transactional
    public CourseEntity createCourse(String title, String subtitle, Integer courseType,
                                     Integer difficulty, String coverUrl) {
        CourseEntity course = CourseEntity.builder()
                .title(title)
                .subtitle(subtitle)
                .courseType(courseType)
                .difficulty(difficulty)
                .coverUrl(coverUrl)
                .status(2)  // 直接发布
                .build();
        courseMapper.insert(course);
        return course;
    }

    /**
     * 给课程添加章节
     * 同时更新课程的 total_chapters 计数
     */
    @Transactional
    public CourseChapterEntity addChapter(Long courseId, String title, Integer chapterType) {
        CourseEntity course = courseMapper.selectById(courseId);
        if (course == null) throw new BizException(404, "课程不存在");

        // 查当前课程最大排序号
        Long maxOrder = chapterMapper.selectCount(
                new LambdaQueryWrapper<CourseChapterEntity>()
                        .eq(CourseChapterEntity::getCourseId, courseId)
        );

        CourseChapterEntity chapter = CourseChapterEntity.builder()
                .courseId(courseId)
                .title(title)
                .chapterOrder(maxOrder.intValue() + 1)
                .chapterType(chapterType)
                .build();
        chapterMapper.insert(chapter);

        // 更新课程章节数
        course.setTotalChapters(maxOrder.intValue() + 1);
        courseMapper.updateById(course);

        // 为所有已报名的学生创建这条章节的学习进度
        List<CourseEnrollmentEntity> enrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<CourseEnrollmentEntity>()
                        .eq(CourseEnrollmentEntity::getCourseId, courseId)
        );
        for (CourseEnrollmentEntity enrollment : enrollments) {
            LearningProgressEntity progress = LearningProgressEntity.builder()
                    .userId(enrollment.getUserId())
                    .chapterId(chapter.getId())
                    .courseId(courseId)
                    .status(0)
                    .build();
            progressMapper.insert(progress);
        }

        return chapter;
    }

    /**
     * 课程列表（只查已发布的）
     * 类比 Python: Course.query.filter_by(status=2).all()
     */
    public List<CourseEntity> listCourses(Integer courseType) {
        LambdaQueryWrapper<CourseEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseEntity::getStatus, 2);  // 只查已发布
        if (courseType != null) {
            wrapper.eq(CourseEntity::getCourseType, courseType);
        }
        wrapper.orderByAsc(CourseEntity::getSortOrder);
        return courseMapper.selectList(wrapper);
    }

    /**
     * 课程详情（包含章节列表）
     */
    public Map<String, Object> getCourseDetail(Long courseId) {
        CourseEntity course = courseMapper.selectById(courseId);
        if (course == null) throw new BizException(404, "课程不存在");

        // 查该课程所有章节，按排序号排列
        List<CourseChapterEntity> chapters = chapterMapper.selectList(
                new LambdaQueryWrapper<CourseChapterEntity>()
                        .eq(CourseChapterEntity::getCourseId, courseId)
                        .orderByAsc(CourseChapterEntity::getChapterOrder)
        );

        Map<String, Object> detail = new HashMap<>();
        detail.put("course", course);
        detail.put("chapters", chapters);
        return detail;
    }

    // ==================== 学习流程 ====================

    /**
     * 报名课程
     * 1. 检查是否已报名
     * 2. 创建报名记录
     * 3. 创建所有章节的学习进度（初始状态：未开始）
     * 4. 课程报名人数 +1
     *
     * @Transactional 保证：这4步要么全部成功，要么全部回滚
     */
    @Transactional
    public CourseEnrollmentEntity enrollCourse(Long courseId) {
        Long userId = UserContext.getUserId();
        if (userId == null) throw new BizException(401, "未登录");

        CourseEntity course = courseMapper.selectById(courseId);
        if (course == null) throw new BizException(404, "课程不存在");
        if (course.getStatus() != 2) throw new BizException(400, "课程未发布");

        // 检查是否已报名
        Long count = enrollmentMapper.selectCount(
                new LambdaQueryWrapper<CourseEnrollmentEntity>()
                        .eq(CourseEnrollmentEntity::getUserId, userId)
                        .eq(CourseEnrollmentEntity::getCourseId, courseId)
        );
        if (count > 0) throw new BizException(400, "已报名该课程");

        // 1. 创建报名记录
        CourseEnrollmentEntity enrollment = CourseEnrollmentEntity.builder()
                .userId(userId)
                .courseId(courseId)
                .build();
        enrollmentMapper.insert(enrollment);

        // 2. 查所有章节，为每个章节创建学习进度
        List<CourseChapterEntity> chapters = chapterMapper.selectList(
                new LambdaQueryWrapper<CourseChapterEntity>()
                        .eq(CourseChapterEntity::getCourseId, courseId)
        );
        for (CourseChapterEntity chapter : chapters) {
            LearningProgressEntity progress = LearningProgressEntity.builder()
                    .userId(userId)
                    .chapterId(chapter.getId())
                    .courseId(courseId)
                    .status(0)  // 未开始
                    .build();
            progressMapper.insert(progress);
        }

        // 3. 课程报名人数 +1
        course.setEnrolledCount(course.getEnrolledCount() + 1);
        courseMapper.updateById(course);

        return enrollment;
    }

    /**
     * 完成章节学习
     * 1. 更新章节进度为"已完成"
     * 2. 检查课程是否全部完成 → 更新报名状态
     */
    @Transactional
    public LearningProgressEntity completeChapter(Long chapterId) {
        Long userId = UserContext.getUserId();
        if (userId == null) throw new BizException(401, "未登录");

        // 查章节
        CourseChapterEntity chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) throw new BizException(404, "章节不存在");

        // 查学习进度
        LearningProgressEntity progress = progressMapper.selectOne(
                new LambdaQueryWrapper<LearningProgressEntity>()
                        .eq(LearningProgressEntity::getUserId, userId)
                        .eq(LearningProgressEntity::getChapterId, chapterId)
        );
        if (progress == null) throw new BizException(400, "未报名该课程");

        if (progress.getStatus() == 2) throw new BizException(400, "章节已完成");

        // 1. 更新进度为已完成
        progress.setStatus(2);
        progress.setScore(100);
        progress.setCompletedAt(LocalDateTime.now());
        progressMapper.updateById(progress);

        // 2. 检查课程是否全部完成
        Long totalChapters = progressMapper.selectCount(
                new LambdaQueryWrapper<LearningProgressEntity>()
                        .eq(LearningProgressEntity::getUserId, userId)
                        .eq(LearningProgressEntity::getCourseId, chapter.getCourseId())
        );
        Long completedChapters = progressMapper.selectCount(
                new LambdaQueryWrapper<LearningProgressEntity>()
                        .eq(LearningProgressEntity::getUserId, userId)
                        .eq(LearningProgressEntity::getCourseId, chapter.getCourseId())
                        .eq(LearningProgressEntity::getStatus, 2)
        );

        // 3. 更新报名进度百分比
        int pct = totalChapters > 0 ? (int) (completedChapters * 100 / totalChapters) : 0;
        enrollmentMapper.update(null,
                new LambdaUpdateWrapper<CourseEnrollmentEntity>()
                        .eq(CourseEnrollmentEntity::getUserId, userId)
                        .eq(CourseEnrollmentEntity::getCourseId, chapter.getCourseId())
                        .set(CourseEnrollmentEntity::getProgressPct, pct)
                        .set(pct == 100, CourseEnrollmentEntity::getStatus, 2)  // 全部完成
                        .set(pct == 100, CourseEnrollmentEntity::getCompletedAt, LocalDateTime.now())
        );

        return progress;
    }

    /**
     * 查询我的学习进度（某课程所有章节的状态）
     */
    public Map<String, Object> getMyProgress(Long courseId) {
        Long userId = UserContext.getUserId();
        if (userId == null) throw new BizException(401, "未登录");

        // 报名信息
        CourseEnrollmentEntity enrollment = enrollmentMapper.selectOne(
                new LambdaQueryWrapper<CourseEnrollmentEntity>()
                        .eq(CourseEnrollmentEntity::getUserId, userId)
                        .eq(CourseEnrollmentEntity::getCourseId, courseId)
        );
        if (enrollment == null) throw new BizException(400, "未报名该课程");

        // 所有章节进度
        List<LearningProgressEntity> progressList = progressMapper.selectList(
                new LambdaQueryWrapper<LearningProgressEntity>()
                        .eq(LearningProgressEntity::getUserId, userId)
                        .eq(LearningProgressEntity::getCourseId, courseId)
        );

        Map<String, Object> result = new HashMap<>();
        result.put("enrollment", enrollment);
        result.put("progress", progressList);
        return result;
    }
}
