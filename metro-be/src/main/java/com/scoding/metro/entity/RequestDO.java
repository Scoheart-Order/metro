package com.scoding.metro.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestDO {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String status; // "pending", "processing", "resolved", "rejected"
    private LocalDateTime createdAt;
    
    // 关联字段，用于显示
    private String username;
    private List<ReplyDO> replies;
} 