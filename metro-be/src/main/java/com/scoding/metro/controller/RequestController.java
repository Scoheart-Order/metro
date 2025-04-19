package com.scoding.metro.controller;

import com.scoding.metro.common.R;
import com.scoding.metro.dto.RequestDTO;
import com.scoding.metro.dto.ReplyDTO;
import com.scoding.metro.entity.RequestDO;
import com.scoding.metro.entity.ReplyDO;
import com.scoding.metro.security.SecurityUser;
import com.scoding.metro.service.RequestService;
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
 * 需求管理控制器
 * 处理用户需求的查询、创建、回复、更新状态和删除等操作
 */
@Tag(name = "需求管理", description = "用户需求相关接口")
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    /**
     * 获取所有需求
     *
     * @return 需求列表
     */
    @Operation(summary = "获取所有需求", description = "获取系统中所有的用户需求")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping
    @PreAuthorize("permitAll()")
    public R<List<RequestDO>> getAllRequests() {
        return R.ok(requestService.listRequests());
    }

    /**
     * 根据ID获取需求
     *
     * @param id 需求ID
     * @return 需求信息
     */
    @Operation(summary = "获取需求详情", description = "根据需求ID获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "需求不存在")
    })
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public R<RequestDO> getRequestById(@PathVariable Long id) {
        return R.ok(requestService.getRequestById(id));
    }

    /**
     * 获取用户的所有需求
     *
     * @param authentication 当前认证信息
     * @return 用户需求列表
     */
    @Operation(summary = "获取用户需求", description = "获取当前登录用户的所有需求")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未登录")
    })
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public R<List<RequestDO>> getUserRequests(Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return R.ok(requestService.listUserRequests(securityUser.getUser().getId()));
    }

    /**
     * 创建需求
     *
     * @param requestDTO 需求信息
     * @param authentication 当前认证信息
     * @return 创建后的需求信息
     */
    @Operation(summary = "创建需求", description = "创建新的用户需求")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未登录")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public R<RequestDO> createRequest(@RequestBody RequestDTO requestDTO, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return R.ok(requestService.saveRequest(requestDTO, securityUser.getUser().getId()));
    }
    
    /**
     * 回复需求
     *
     * @param id 需求ID
     * @param replyDTO 回复信息
     * @param authentication 当前认证信息
     * @return 创建后的回复信息
     */
    @Operation(summary = "回复需求", description = "对指定需求进行回复")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "回复成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "404", description = "需求不存在")
    })
    @PostMapping("/{id}/replies")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public R<ReplyDO> replyToRequest(@PathVariable Long id, @RequestBody ReplyDTO replyDTO, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        boolean isAdmin = securityUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SUPER_ADMIN"));
        
        return R.ok(requestService.replyToRequest(id, replyDTO, securityUser.getUser().getId(), isAdmin));
    }
    
    /**
     * 更新需求状态
     *
     * @param id 需求ID
     * @param status 新状态
     * @return 更新后的需求信息
     */
    @Operation(summary = "更新需求状态", description = "更新指定需求的状态（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "需求不存在")
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<RequestDO> updateRequestStatus(@PathVariable Long id, @RequestParam String status) {
        return R.ok(requestService.updateRequestStatus(id, status));
    }
    
    /**
     * 删除需求
     *
     * @param id 需求ID
     * @return 操作结果
     */
    @Operation(summary = "删除需求", description = "删除指定需求（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "需求不存在")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Void> deleteRequest(@PathVariable Long id) {
        requestService.removeRequest(id);
        return R.ok();
    }
} 