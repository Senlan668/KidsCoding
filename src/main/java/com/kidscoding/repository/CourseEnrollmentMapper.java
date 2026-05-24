package com.kidscoding.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kidscoding.entity.CourseEnrollmentEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseEnrollmentMapper extends BaseMapper<CourseEnrollmentEntity> {
}
