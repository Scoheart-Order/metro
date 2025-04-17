package com.scoding.metro.controller;

import com.scoding.metro.common.R;
import com.scoding.metro.dto.PasswordUpdateDto;
import com.scoding.metro.dto.RechargeDto;
import com.scoding.metro.dto.UserProfileDto;
import com.scoding.metro.dto.UserResponseDto;
import com.scoding.metro.entity.User;
import com.scoding.metro.service.UserService;
import com.scoding.metro.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 用户个人信息和账户管理控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户个人信息和账户管理相关接口")
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户个人信息
     */
    @Operation(summary = "获取个人信息", description = "获取当前登录用户的个人信息")
    @GetMapping("/profile")
    public R<UserResponseDto> getCurrentUserProfile(Authentication authentication) {
        // 从SecurityUser中获取用户信息
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        // 重新查询以获取最新数据
        User user = userService.getUserById(securityUser.getUser().getId());
        return R.ok(UserResponseDto.fromEntity(user));
    }

    /**
     * 更新当前用户个人信息
     */
    @Operation(summary = "更新个人信息", description = "更新当前登录用户的个人信息")
    @PutMapping("/profile")
    public R<UserResponseDto> updateUserProfile(@RequestBody UserProfileDto profileDto, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User user = userService.updateUserProfile(securityUser.getUser().getId(), profileDto);
        return R.ok(UserResponseDto.fromEntity(user));
    }

    /**
     * 更新当前用户密码
     */
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    @PutMapping("/profile/password")
    public R<Void> updatePassword(@Valid @RequestBody PasswordUpdateDto passwordDto, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        userService.updatePassword(securityUser.getUser().getId(), passwordDto);
        return R.ok();
    }

    /**
     * 用户充值
     */
    @Operation(summary = "用户充值", description = "用户充值，充值满100元送10元")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "充值成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未登录")
    })
    @PostMapping("/recharge")
    public R<Void> recharge(@Valid @RequestBody RechargeDto rechargeDto, Authentication authentication) {
        userService.recharge(rechargeDto, authentication);
        return R.ok();
    }

    /**
     * 获取用户余额
     */
    @Operation(summary = "获取余额", description = "获取当前登录用户的账户余额")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未登录")
    })
    @GetMapping("/balance")
    public R<Double> getBalance(Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return R.ok(userService.getUserBalance(securityUser.getUser().getId()));
    }
}