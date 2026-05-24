package com.kidscoding.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kidscoding.common.exception.BizException;
import com.kidscoding.entity.UserEntity;
import com.kidscoding.repository.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * 用户服务（MyBatis-Plus 版）
 *
 * 对比 JPA 版：
 *   JPA:  userRepository.save(user)            → MP: userMapper.insert(user)
 *   JPA:  userRepository.findById(id)          → MP: userMapper.selectById(id)
 *   JPA:  userRepository.findAll()             → MP: userMapper.selectList(null)
 *   JPA:  无需手动生成 ID                       → MP: @TableId(type=ASSIGN_ID) 自动雪花
 *
 * MyBatis-Plus 的雪花 ID 是自动的：
 *   insert 时如果 id 为 null，自动用雪花算法填充
 *   不需要像 JPA 版那样手动调用 SnowflakeIdUtil
 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final Random random = new Random();

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserEntity createUser(String username, String nickname, String userType) {
        // 先检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getUsername, username)
        );
        if (count > 0) {
            throw new BizException(400, "用户名已存在: " + username);
        }

        String discriminator = generateDiscriminator(nickname);

        UserEntity user = UserEntity.builder()
                // 不用设 id！@TableId(type=ASSIGN_ID) 自动生成雪花 ID
                .username(username)
                .nickname(nickname)
                .discriminator(discriminator)
                .userType(mapUserType(userType))
                .build();
        userMapper.insert(user);  // INSERT INTO t_user (id, username, ...) VALUES (?, ?, ...)
        return user;
    }

    public List<UserEntity> listUsers() {
        List<UserEntity> users = userMapper.selectList(null);
        users.forEach(u -> u.setPasswordHash(null));  // 清掉密码哈希
        return users;
    }

    public UserEntity getUserById(Long id) {
        UserEntity user = userMapper.selectById(id);
        if (user != null) user.setPasswordHash(null);
        return user;
    }

    public UserEntity updateUser(Long id, String username, String nickname, String userType) {
        UserEntity user = getUserById(id);
        if (user == null) {
            return null;
        }
        if (username != null) user.setUsername(username);
        if (nickname != null) user.setNickname(nickname);
        if (userType != null) user.setUserType(mapUserType(userType));
        userMapper.updateById(user);  // UPDATE t_user SET ... WHERE id = ? AND version = ?
        return user;
    }

    public boolean deleteUser(Long id) {
        // @TableLogic 让 deleteById 自动变成 UPDATE SET deleted = 1
        return userMapper.deleteById(id) > 0;
    }

    /**
     * 查找同一昵称下是否已有某个 discriminator
     * 演示 MyBatis-Plus 的 LambdaQueryWrapper（类型安全的条件构造器）
     */
    public UserEntity findByNicknameAndDiscriminator(String nickname, String discriminator) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getNickname, nickname)
                        .eq(UserEntity::getDiscriminator, discriminator)
        );
    }

    private String generateDiscriminator(String nickname) {
        return String.format("%04d", random.nextInt(9999) + 1);
    }

    private Integer mapUserType(String role) {
        return switch (role.toUpperCase()) {
            case "CHILD" -> 1;
            case "PARENT" -> 2;
            case "TEACHER" -> 3;
            default -> 1;
        };
    }
}
