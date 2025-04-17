package com.scoding.metro.service.impl;

import com.scoding.metro.common.RCode;
import com.scoding.metro.dto.AnnouncementDto;
import com.scoding.metro.entity.Announcement;
import com.scoding.metro.exception.BusinessException;
import com.scoding.metro.mapper.AnnouncementMapper;
import com.scoding.metro.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public List<Announcement> getAllAnnouncements() {
        return announcementMapper.getAllAnnouncements();
    }

    @Override
    public Announcement getAnnouncementById(Long id) {
        Announcement announcement = announcementMapper.getAnnouncementById(id);
        if (announcement == null) {
            throw new BusinessException(RCode.NOT_FOUND, "公告不存在");
        }
        return announcement;
    }

    @Override
    @Transactional
    public Announcement createAnnouncement(AnnouncementDto announcementDto, Long adminId) {
        Announcement announcement = new Announcement();
        announcement.setTitle(announcementDto.getTitle());
        announcement.setContent(announcementDto.getContent());
        announcement.setAdminId(adminId);

        announcementMapper.saveAnnouncement(announcement);
        return getAnnouncementById(announcement.getId());
    }

    @Override
    @Transactional
    public Announcement updateAnnouncement(Long id, AnnouncementDto announcementDto) {
        Announcement announcement = getAnnouncementById(id);

        announcement.setTitle(announcementDto.getTitle());
        announcement.setContent(announcementDto.getContent());

        announcementMapper.updateAnnouncement(announcement);
        return getAnnouncementById(id);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteAnnouncement(id);
    }
}