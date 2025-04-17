package com.scoding.metro.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 用户个人信息DTO
 */
@Data
public class UserProfileDto {
    private String realName;
    private String email;
    private String phone;
    private String gender;
    private LocalDate birthDate;
    private String avatar;
    private String address;
    private String bio;
} 