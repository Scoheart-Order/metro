package com.scoding.metro.controller;

import com.scoding.metro.common.R;
import com.scoding.metro.dto.UserCreateDto;
import com.scoding.metro.dto.UserResponseDto;
import com.scoding.metro.entity.User;
import com.scoding.metro.service.SuperAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 超级管理员控制器
 * 用于系统管理员对用户进行管理的接口
 */
@Tag(name = "超级管理员", description = "系统管理员用户管理相关接口")
@RestController
@RequestMapping("/superadmin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    /**
     * 获取所有用户
     */
    @Operation(summary = "获取所有用户", description = "获取系统中所有用户信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "403", description = "没有权限")
    })
    @GetMapping("/users")
    public R<List<UserResponseDto>> getAllUsers() {
        List<User> users = superAdminService.listAllUsers();
        List<UserResponseDto> userDtos = users.stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
        return R.ok(userDtos);
    }

    /**
     * 获取用户详情
     */
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "403", description = "没有权限")
    })
    @GetMapping("/users/{id}")
    public R<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = superAdminService.getUserById(id);
        return R.ok(UserResponseDto.fromEntity(user));
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息", description = "更新用户的基本信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "403", description = "没有权限")
    })
    @PutMapping("/users/{id}")
    public R<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        User updatedUser = superAdminService.updateUser(id, user);
        return R.ok(UserResponseDto.fromEntity(updatedUser));
    }

    /**
     * 创建新用户
     */
    @Operation(summary = "创建用户", description = "创建新用户，包括设置角色")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "没有权限"),
            @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PostMapping("/users")
    public R<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        User user = userCreateDto.toEntity();
        User createdUser = superAdminService.saveUser(user);
        return R.ok(UserResponseDto.fromEntity(createdUser));
    }

    /**
     * 重置用户密码
     */
    @Operation(summary = "重置密码", description = "重置指定用户的密码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "重置成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "403", description = "没有权限")
    })
    @PutMapping("/users/{id}/reset-password")
    public R<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        superAdminService.resetPassword(id, newPassword);
        return R.ok();
    }

    /**
     * 启用/禁用用户
     */
    @Operation(summary = "切换用户状态", description = "启用或禁用用户账户")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "操作成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "403", description = "没有权限")
    })
    @PutMapping("/users/{id}/toggle-status")
    public R<UserResponseDto> toggleUserStatus(@PathVariable Long id) {
        User user = superAdminService.toggleUserStatus(id);
        return R.ok(UserResponseDto.fromEntity(user));
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户", description = "删除指定用户")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "403", description = "没有权限")
    })
    @DeleteMapping("/users/{id}")
    public R<Void> deleteUser(@PathVariable Long id) {
        superAdminService.removeUser(id);
        return R.ok();
    }
} 