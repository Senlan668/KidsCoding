package com.kidscoding.controller;

import com.kidscoding.common.result.Result;
import com.kidscoding.dto.CreateUserRequest;
import com.kidscoding.entity.UserEntity;
import com.kidscoding.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户 Controller — REST API 入口
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Result<UserEntity> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserEntity created = userService.createUser(
                request.getUsername(),
                request.getNickname(),
                request.getRole()
        );
        return Result.ok(created);
    }

    @GetMapping
    public Result<List<UserEntity>> listUsers() {
        return Result.ok(userService.listUsers());
    }

    @GetMapping("/{id}")
    public Result<UserEntity> getUser(@PathVariable("id") Long id) {
        UserEntity user = userService.getUserById(id);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        return Result.ok(user);
    }

    @PutMapping("/{id}")
    public Result<UserEntity> updateUser(
            @PathVariable("id") Long id,
            @RequestBody UserEntity user
    ) {
        UserEntity updated = userService.updateUser(
                id, user.getUsername(), user.getNickname(),
                user.getUserType() != null ? String.valueOf(user.getUserType()) : null
        );
        if (updated == null) {
            return Result.fail(404, "用户不存在");
        }
        return Result.ok(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable("id") Long id) {
        boolean deleted = userService.deleteUser(id);
        if (!deleted) {
            return Result.fail(404, "用户不存在");
        }
        return Result.ok();
    }
}
