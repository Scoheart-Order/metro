package com.scoding.metro.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FeedbackDO {
    private Long id;
    private Long userId;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
    
    // 关联字段，用于显示
    private String username;
    private List<ReplyDO> replies;
} 