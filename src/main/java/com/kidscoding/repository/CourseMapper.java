package com.kidscoding.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kidscoding.entity.CourseEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<CourseEntity> {
}
