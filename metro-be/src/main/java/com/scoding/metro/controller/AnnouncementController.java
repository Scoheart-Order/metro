package com.scoding.metro.controller;

import com.scoding.metro.common.R;
import com.scoding.metro.dto.AnnouncementDto;
import com.scoding.metro.entity.Announcement;
import com.scoding.metro.entity.User;
import com.scoding.metro.service.AnnouncementService;
import com.scoding.metro.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * 获取所有公告
     */
    @GetMapping
    public R<List<Announcement>> getAllAnnouncements() {
        return R.ok(announcementService.getAllAnnouncements());
    }

    /**
     * 根据ID获取公告
     */
    @GetMapping("/{id}")
    public R<Announcement> getAnnouncementById(@PathVariable Long id) {
        return R.ok(announcementService.getAnnouncementById(id));
    }

    /**
     * 创建公告（需要管理员权限）
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Announcement> createAnnouncement(@RequestBody AnnouncementDto announcementDto,
            Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return R.ok(announcementService.createAnnouncement(announcementDto, securityUser.getUser().getId()));
    }

    /**
     * 更新公告（需要管理员权限）
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Announcement> updateAnnouncement(@PathVariable Long id, @RequestBody AnnouncementDto announcementDto) {
        return R.ok(announcementService.updateAnnouncement(id, announcementDto));
    }

    /**
     * 删除公告（需要管理员权限）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return R.ok();
    }
}