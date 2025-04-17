package com.scoding.metro.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplyDO {
    private Long id;
    private Long userId;
    private Long parentId;  // 关联的Feedback或Request的ID
    private String parentType; // "FEEDBACK" or "REQUEST"
    private String content;
    private LocalDateTime createdAt;
    private Boolean isAdmin;
    
    // 关联字段，用于显示
    private String username;
} 