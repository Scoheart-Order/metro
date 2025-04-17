package com.scoding.metro.mapper;

import com.scoding.metro.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnnouncementMapper {
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
    Announcement getAnnouncementById(@Param("id") Long id);

    /**
     * 保存公告
     *
     * @param announcement 公告信息
     * @return 影响行数
     */
    int saveAnnouncement(Announcement announcement);

    /**
     * 更新公告
     *
     * @param announcement 公告信息
     * @return 影响行数
     */
    int updateAnnouncement(Announcement announcement);

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 影响行数
     */
    int deleteAnnouncement(@Param("id") Long id);
} 