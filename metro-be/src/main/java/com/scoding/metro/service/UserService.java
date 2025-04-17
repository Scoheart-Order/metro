package com.scoding.metro.service;

import com.scoding.metro.dto.PasswordUpdateDto;
import com.scoding.metro.dto.RechargeDto;
import com.scoding.metro.dto.UserProfileDto;
import com.scoding.metro.entity.User;
import org.springframework.security.core.Authentication;

public interface UserService {
    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 根据用户ID获取用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);

    /**
     * 更新用户个人信息
     * 
     * @param userId     用户ID
     * @param profileDto 用户个人信息
     * @return 更新后的用户信息
     */
    User updateUserProfile(Long userId, UserProfileDto profileDto);

    /**
     * 更新用户密码
     * 
     * @param userId      用户ID
     * @param passwordDto 密码更新信息
     */
    void updatePassword(Long userId, PasswordUpdateDto passwordDto);

    /**
     * 用户充值
     * 充值100元送10元
     *
     * @param rechargeDto    充值请求
     * @param authentication 当前用户认证信息
     */
    void recharge(RechargeDto rechargeDto, Authentication authentication);

    /**
     * 获取用户账户余额
     *
     * @param userId 用户ID
     * @return 账户余额
     */
    Double getUserBalance(Long userId);
}
