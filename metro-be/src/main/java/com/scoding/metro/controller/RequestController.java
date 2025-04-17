package com.scoding.metro.controller;

import com.scoding.metro.common.R;
import com.scoding.metro.dto.RequestDTO;
import com.scoding.metro.dto.ReplyDTO;
import com.scoding.metro.entity.RequestDO;
import com.scoding.metro.entity.ReplyDO;
import com.scoding.metro.security.SecurityUser;
import com.scoding.metro.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    /**
     * 获取所有需求
     */
    @GetMapping
    public R<List<RequestDO>> getAllRequests() {
        return R.ok(requestService.listRequests());
    }

    /**
     * 根据ID获取需求
     */
    @GetMapping("/{id}")
    public R<RequestDO> getRequestById(@PathVariable Long id) {
        return R.ok(requestService.getRequestById(id));
    }

    /**
     * 创建需求
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public R<RequestDO> createRequest(@RequestBody RequestDTO requestDTO, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return R.ok(requestService.saveRequest(requestDTO, securityUser.getUser().getId()));
    }
    
    /**
     * 更新需求状态（需要管理员权限）
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<RequestDO> updateRequestStatus(@PathVariable Long id, @RequestParam String status) {
        return R.ok(requestService.updateRequestStatus(id, status));
    }
    
    /**
     * 删除需求（需要管理员权限）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Void> deleteRequest(@PathVariable Long id) {
        requestService.removeRequest(id);
        return R.ok();
    }
    
    /**
     * 回复需求
     */
    @PostMapping("/{id}/replies")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public R<ReplyDO> replyToRequest(@PathVariable Long id, @RequestBody ReplyDTO replyDTO, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        boolean isAdmin = securityUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SUPER_ADMIN"));
        
        return R.ok(requestService.replyToRequest(id, replyDTO, securityUser.getUser().getId(), isAdmin));
    }
    
    /**
     * 获取用户的所有需求
     */
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public R<List<RequestDO>> getUserRequests(Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return R.ok(requestService.listUserRequests(securityUser.getUser().getId()));
    }
} 