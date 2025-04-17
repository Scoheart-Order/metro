package com.scoding.metro.mapper;

import com.scoding.metro.entity.FeedbackDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeedbackMapper {
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
    FeedbackDO getFeedbackById(@Param("id") Long id);
    
    /**
     * 根据用户ID获取反馈
     *
     * @param userId 用户ID
     * @return 反馈列表
     */
    List<FeedbackDO> listFeedbacksByUserId(@Param("userId") Long userId);

    /**
     * 保存反馈
     *
     * @param feedbackDO 反馈信息
     * @return 影响行数
     */
    int saveFeedback(FeedbackDO feedbackDO);

    /**
     * 更新反馈
     *
     * @param feedbackDO 反馈信息
     * @return 影响行数
     */
    int updateFeedback(FeedbackDO feedbackDO);

    /**
     * 删除反馈
     *
     * @param id 反馈ID
     * @return 影响行数
     */
    int removeFeedback(@Param("id") Long id);
} 