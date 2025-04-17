package com.scoding.metro.controller;

import com.scoding.metro.common.R;
import com.scoding.metro.dto.FeedbackDTO;
import com.scoding.metro.dto.ReplyDTO;
import com.scoding.metro.entity.FeedbackDO;
import com.scoding.metro.entity.ReplyDO;
import com.scoding.metro.security.SecurityUser;
import com.scoding.metro.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * 获取所有反馈
     */
    @GetMapping
    public R<List<FeedbackDO>> getAllFeedbacks() {
        return R.ok(feedbackService.listFeedbacks());
    }

    /**
     * 根据ID获取反馈
     */
    @GetMapping("/{id}")
    public R<FeedbackDO> getFeedbackById(@PathVariable Long id) {
        return R.ok(feedbackService.getFeedbackById(id));
    }

    /**
     * 创建反馈
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public R<FeedbackDO> createFeedback(@RequestBody FeedbackDTO feedbackDTO, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return R.ok(feedbackService.saveFeedback(feedbackDTO, securityUser.getUser().getId()));
    }

    /**
     * 删除反馈（需要管理员权限）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.removeFeedback(id);
        return R.ok();
    }
    
    /**
     * 回复反馈
     */
    @PostMapping("/{id}/replies")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public R<ReplyDO> replyToFeedback(@PathVariable Long id, @RequestBody ReplyDTO replyDTO, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        boolean isAdmin = securityUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SUPER_ADMIN"));
        
        return R.ok(feedbackService.replyToFeedback(id, replyDTO, securityUser.getUser().getId(), isAdmin));
    }
    
    /**
     * 获取用户的所有反馈
     */
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public R<List<FeedbackDO>> getUserFeedbacks(Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return R.ok(feedbackService.listUserFeedbacks(securityUser.getUser().getId()));
    }
} 