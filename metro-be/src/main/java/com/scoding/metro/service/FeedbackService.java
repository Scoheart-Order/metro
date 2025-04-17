package com.scoding.metro.service;

import com.scoding.metro.dto.FeedbackDTO;
import com.scoding.metro.dto.ReplyDTO;
import com.scoding.metro.entity.FeedbackDO;
import com.scoding.metro.entity.ReplyDO;

import java.util.List;

public interface FeedbackService {
    /**
     * 获取所有反馈
     *
     * @return 反馈列表
     */
    List<FeedbackDO> listFeedbacks();

    /**
     * 根据ID获取反馈
     *
     * @param id 反馈ID
     * @return 反馈信息
     */
    FeedbackDO getFeedbackById(Long id);
    
    /**
     * 获取用户的所有反馈
     *
     * @param userId 用户ID
     * @return 反馈列表
     */
    List<FeedbackDO> listUserFeedbacks(Long userId);

    /**
     * 创建反馈
     *
     * @param feedbackDTO 反馈信息
     * @param userId 用户ID
     * @return 创建的反馈
     */
    FeedbackDO saveFeedback(FeedbackDTO feedbackDTO, Long userId);

    /**
     * 回复反馈
     *
     * @param id 反馈ID
     * @param replyDTO 回复内容
     * @param userId 用户ID
     * @param isAdmin 是否管理员
     * @return 创建的回复
     */
    ReplyDO replyToFeedback(Long id, ReplyDTO replyDTO, Long userId, boolean isAdmin);

    /**
     * 删除反馈
     *
     * @param id 反馈ID
     */
    void removeFeedback(Long id);
} 