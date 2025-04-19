package com.scoding.metro.controller;

import com.scoding.metro.common.R;
import com.scoding.metro.dto.FeedbackDTO;
import com.scoding.metro.dto.ReplyDTO;
import com.scoding.metro.entity.FeedbackDO;
import com.scoding.metro.entity.ReplyDO;
import com.scoding.metro.security.SecurityUser;
import com.scoding.metro.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 反馈管理控制器
 * 处理用户反馈的查询、创建、回复和删除等操作
 */
@Tag(name = "反馈管理", description = "用户反馈相关接口")
@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * 获取所有反馈
     *
     * @return 反馈列表
     */
    @Operation(summary = "获取所有反馈", description = "获取系统中所有的用户反馈")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping
    @PreAuthorize("permitAll()")
    public R<List<FeedbackDO>> getAllFeedbacks() {
        return R.ok(feedbackService.listFeedbacks());
    }

    /**
     * 根据ID获取反馈
     *
     * @param id 反馈ID
     * @return 反馈信息
     */
    @Operation(summary = "获取反馈详情", description = "根据反馈ID获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "反馈不存在")
    })
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public R<FeedbackDO> getFeedbackById(@PathVariable Long id) {
        return R.ok(feedbackService.getFeedbackById(id));
    }

    /**
     * 获取用户的所有反馈
     *
     * @param authentication 当前认证信息
     * @return 用户反馈列表
     */
    @Operation(summary = "获取用户反馈", description = "获取当前登录用户的所有反馈")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未登录")
    })
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public R<List<FeedbackDO>> getUserFeedbacks(Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return R.ok(feedbackService.listUserFeedbacks(securityUser.getUser().getId()));
    }

    /**
     * 创建反馈
     *
     * @param feedbackDTO 反馈信息
     * @param authentication 当前认证信息
     * @return 创建后的反馈信息
     */
    @Operation(summary = "创建反馈", description = "创建新的用户反馈")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未登录")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public R<FeedbackDO> createFeedback(@RequestBody FeedbackDTO feedbackDTO, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return R.ok(feedbackService.saveFeedback(feedbackDTO, securityUser.getUser().getId()));
    }

    /**
     * 回复反馈
     *
     * @param id 反馈ID
     * @param replyDTO 回复信息
     * @param authentication 当前认证信息
     * @return 创建后的回复信息
     */
    @Operation(summary = "回复反馈", description = "对指定反馈进行回复")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "回复成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "404", description = "反馈不存在")
    })
    @PostMapping("/{id}/replies")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public R<ReplyDO> replyToFeedback(@PathVariable Long id, @RequestBody ReplyDTO replyDTO, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        boolean isAdmin = securityUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SUPER_ADMIN"));
        
        return R.ok(feedbackService.replyToFeedback(id, replyDTO, securityUser.getUser().getId(), isAdmin));
    }

    /**
     * 删除反馈
     *
     * @param id 反馈ID
     * @return 操作结果
     */
    @Operation(summary = "删除反馈", description = "删除指定反馈（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "反馈不存在")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.removeFeedback(id);
        return R.ok();
    }
} 