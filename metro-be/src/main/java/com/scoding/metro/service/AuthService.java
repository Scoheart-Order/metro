package com.scoding.metro.service;

import com.scoding.metro.dto.LoginDto;
import com.scoding.metro.dto.PhoneLoginDto;
import com.scoding.metro.dto.RegisterDto;
import com.scoding.metro.dto.TokenDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import com.scoding.metro.entity.User;

public interface AuthService {

    /**
     * 注册新用户
     *
     * @param registerDto 注册信息
     */
    void register(RegisterDto registerDto);

    /**
     * 用户登录
     *
     * @param loginDto 登录信息
     * @return 令牌信息
     */
    TokenDto login(LoginDto loginDto);

    /**
     * 手机号登录
     *
     * @param phoneLoginDto 手机号登录信息
     * @return 令牌信息
     */
    TokenDto loginByPhone(PhoneLoginDto phoneLoginDto);

    /**
     * 刷新访问令牌
     *
     * @param request HTTP请求
     * @return 新的令牌信息
     */
    TokenDto refreshToken(HttpServletRequest request);

    /**
     * 用户登出
     *
     * @param request HTTP请求
     */
    void logout(HttpServletRequest request);

    /**
     * 获取当前用户信息
     *
     * @param authentication 认证信息
     * @return 用户信息
     */
    User getCurrentUser(Authentication authentication);
}