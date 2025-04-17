package com.scoding.metro.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class User {
    private Long id;
    private String username;
    
    @JsonIgnore
    private String password;
    
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private List<Role> roles;
    private Double money = 0.0; // User account balance
    
    // 个人信息字段
    private String realName;     // 真实姓名
    private String email;        // 电子邮箱
    private String phone;        // 手机号码
    private String gender;       // 性别
    private LocalDate birthDate; // 出生日期
    private String avatar;       // 头像URL
    private String address;      // 地址
    private String bio;          // 个人简介
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
