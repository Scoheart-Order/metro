package com.scoding.metro.controller;

import com.scoding.metro.common.R;
import com.scoding.metro.dto.LoginDto;
import com.scoding.metro.dto.PhoneLoginDto;
import com.scoding.metro.dto.RegisterDto;
import com.scoding.metro.dto.TokenDto;
import com.scoding.metro.dto.UserResponseDto;
import com.scoding.metro.entity.User;
import com.scoding.metro.security.SecurityUser;
import com.scoding.metro.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户认证相关的请求，包括登录、注册、刷新令牌等
 */
@Tag(name = "认证管理", description = "用户认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     *
     * @param registerDto 注册请求参数
     * @return 注册结果
     */
    @Operation(summary = "用户注册", description = "注册新用户")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterDto registerDto) {
        authService.register(registerDto);
        return R.ok();
    }

    /**
     * 用户登录
     *
     * @param loginDto 登录请求参数
     * @return 包含JWT令牌的响应
     */
    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录认证")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public R<TokenDto> login(@Valid @RequestBody LoginDto loginDto) {
        return R.ok(authService.login(loginDto));
    }

    /**
     * 手机号登录
     *
     * @param phoneLoginDto 手机号登录请求参数
     * @return 包含JWT令牌的响应
     */
    @Operation(summary = "手机号登录", description = "使用手机号和密码进行登录认证")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "手机号或密码错误")
    })
    @PostMapping("/login/phone")
    public R<TokenDto> loginByPhone(@Valid @RequestBody PhoneLoginDto phoneLoginDto) {
        return R.ok(authService.loginByPhone(phoneLoginDto));
    }

    /**
     * 刷新访问令牌
     *
     * @param request HTTP请求
     * @return 新的访问令牌
     */
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "刷新成功"),
            @ApiResponse(responseCode = "401", description = "无效的刷新令牌")
    })
    @PostMapping("/refresh-token")
    public R<TokenDto> refreshToken(HttpServletRequest request) {
        return R.ok(authService.refreshToken(request));
    }

    /**
     * 用户登出
     *
     * @param request HTTP请求
     * @return 登出结果
     */
    @Operation(summary = "用户登出", description = "使当前用户的令牌失效")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登出成功")
    })
    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return R.ok();
    }

    /**
     * 获取当前用户信息
     *
     * @param authentication 当前认证信息
     * @return 用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未登录")
    })
    @GetMapping("/me")
    public R<UserResponseDto> getCurrentUser(Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return R.ok(UserResponseDto.fromEntity(user));
    }
}