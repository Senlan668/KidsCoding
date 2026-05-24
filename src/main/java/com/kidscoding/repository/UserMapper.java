package com.kidscoding.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kidscoding.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper — MyBatis-Plus 的数据库操作接口
 *
 * 对比 JPA 的 Repository：
 *   JPA:    extends JpaRepository<UserEntity, Long>
 *   MP:     extends BaseMapper<UserEntity>
 *
 * BaseMapper 自带的方法（不用写实现）：
 *   insert(entity)           → INSERT INTO ...
 *   selectById(id)           → SELECT * WHERE id = ?
 *   selectList(wrapper)      → SELECT * WHERE ...（条件查询）
 *   updateById(entity)       → UPDATE ... WHERE id = ?
 *   deleteById(id)           → DELETE WHERE id = ?（逻辑删除时变成 UPDATE SET deleted=1）
 *   selectCount(wrapper)     → SELECT COUNT(*)
 *
 * @Mapper 告诉 Spring：这是一个 MyBatis Mapper 接口，请帮我管理
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    // 不用写任何方法！BaseMapper 已经提供了所有 CRUD
    // 如果需要自定义 SQL，可以在这里声明方法 + 写 XML
}
