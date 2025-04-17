package com.scoding.metro.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Announcement {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long adminId;
    
    // 额外字段，用于显示发布管理员的用户名
    private String adminUsername;
} 