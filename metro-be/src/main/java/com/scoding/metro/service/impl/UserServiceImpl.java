package com.scoding.metro.service.impl;

import com.scoding.metro.common.RCode;
import com.scoding.metro.dto.PasswordUpdateDto;
import com.scoding.metro.dto.RechargeDto;
import com.scoding.metro.dto.UserProfileDto;
import com.scoding.metro.entity.User;
import com.scoding.metro.exception.BusinessException;
import com.scoding.metro.mapper.UserMapper;
import com.scoding.metro.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.scoding.metro.security.SecurityUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // 充值满100元送10元
    private static final double BONUS_THRESHOLD = 100.0;
    private static final double BONUS_AMOUNT = 10.0;

    @Override
    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.getUserById(id);
        if (user == null) {
            throw new BusinessException(RCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    @Override
    @Transactional
    public User updateUserProfile(Long userId, UserProfileDto profileDto) {
        User user = getUserById(userId);

        // 更新用户个人信息
        user.setRealName(profileDto.getRealName());
        user.setEmail(profileDto.getEmail());
        user.setPhone(profileDto.getPhone());
        user.setGender(profileDto.getGender());
        user.setBirthDate(profileDto.getBirthDate());
        user.setAvatar(profileDto.getAvatar());
        user.setAddress(profileDto.getAddress());
        user.setBio(profileDto.getBio());

        userMapper.updateUserProfile(user);

        return getUserById(userId);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateDto passwordDto) {
        User user = getUserById(userId);

        // 验证旧密码
        if (!passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(RCode.BAD_REQUEST, "旧密码不正确");
        }

        // 验证新密码与确认密码是否一致
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
            throw new BusinessException(RCode.BAD_REQUEST, "新密码与确认密码不一致");
        }

        // 更新密码
        String encodedPassword = passwordEncoder.encode(passwordDto.getNewPassword());
        userMapper.updatePassword(userId, encodedPassword);
    }

    @Override
    @Transactional
    public void recharge(RechargeDto rechargeDto, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User user = securityUser.getUser();
        Double amount = rechargeDto.getAmount();

        // 计算赠送金额
        Double bonus = calculateBonus(amount);
        Double totalAmount = amount + bonus;

        // 更新用户余额
        Double newBalance = user.getMoney() + totalAmount;
        userMapper.updateUserMoney(user.getId(), newBalance);
    }

    @Override
    public Double getUserBalance(Long userId) {
        User user = getUserById(userId);
        return user.getMoney();
    }

    /**
     * 计算赠送金额
     * 充值满100元送10元
     */
    private Double calculateBonus(Double amount) {
        // 计算应赠送的次数（每满100元送10元）
        int bonusCount = (int) (amount / BONUS_THRESHOLD);
        return bonusCount * BONUS_AMOUNT;
    }
}