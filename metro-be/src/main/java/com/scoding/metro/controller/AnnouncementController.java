package com.scoding.metro.controller;

import com.scoding.metro.common.R;
import com.scoding.metro.dto.AnnouncementDto;
import com.scoding.metro.entity.Announcement;
import com.scoding.metro.service.AnnouncementService;
import com.scoding.metro.security.SecurityUser;
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
 * 公告管理控制器
 * 处理公告的查询、创建、更新和删除等操作
 */
@Tag(name = "公告管理", description = "公告相关接口")
@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * 获取所有公告
     *
     * @return 公告列表
     */
    @Operation(summary = "获取所有公告", description = "获取系统中所有的公告信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping
    @PreAuthorize("permitAll()")
    public R<List<Announcement>> getAllAnnouncements() {
        return R.ok(announcementService.getAllAnnouncements());
    }

    /**
     * 根据ID获取公告
     *
     * @param id 公告ID
     * @return 公告信息
     */
    @Operation(summary = "获取公告详情", description = "根据公告ID获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "公告不存在")
    })
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public R<Announcement> getAnnouncementById(@PathVariable Long id) {
        return R.ok(announcementService.getAnnouncementById(id));
    }

    /**
     * 创建公告
     *
     * @param announcementDto 公告信息
     * @param authentication 当前认证信息
     * @return 创建后的公告信息
     */
    @Operation(summary = "创建公告", description = "创建新的公告（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Announcement> createAnnouncement(@RequestBody AnnouncementDto announcementDto,
            Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return R.ok(announcementService.createAnnouncement(announcementDto, securityUser.getUser().getId()));
    }

    /**
     * 更新公告
     *
     * @param id 公告ID
     * @param announcementDto 公告信息
     * @return 更新后的公告信息
     */
    @Operation(summary = "更新公告", description = "更新现有公告信息（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "公告不存在")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Announcement> updateAnnouncement(@PathVariable Long id, @RequestBody AnnouncementDto announcementDto) {
        return R.ok(announcementService.updateAnnouncement(id, announcementDto));
    }

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 操作结果
     */
    @Operation(summary = "删除公告", description = "删除指定公告（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "公告不存在")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return R.ok();
    }
}