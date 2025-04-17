package com.scoding.metro.service;

import com.scoding.metro.dto.AnnouncementDto;
import com.scoding.metro.entity.Announcement;

import java.util.List;

public interface AnnouncementService {
    /**
     * 获取所有公告
     *
     * @return 公告列表
     */
    List<Announcement> getAllAnnouncements();

    /**
     * 根据ID获取公告
     *
     * @param id 公告ID
     * @return 公告信息
     */
    Announcement getAnnouncementById(Long id);

    /**
     * 创建公告
     *
     * @param announcementDto 公告信息
     * @param adminId         管理员ID
     * @return 创建的公告
     */
    Announcement createAnnouncement(AnnouncementDto announcementDto, Long adminId);

    /**
     * 更新公告
     *
     * @param id              公告ID
     * @param announcementDto 公告信息
     * @return 更新后的公告
     */
    Announcement updateAnnouncement(Long id, AnnouncementDto announcementDto);

    /**
     * 删除公告
     *
     * @param id 公告ID
     */
    void deleteAnnouncement(Long id);
}