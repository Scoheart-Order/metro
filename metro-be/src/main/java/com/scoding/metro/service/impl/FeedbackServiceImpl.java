package com.scoding.metro.service.impl;

import com.scoding.metro.common.RCode;
import com.scoding.metro.dto.FeedbackDTO;
import com.scoding.metro.dto.ReplyDTO;
import com.scoding.metro.entity.FeedbackDO;
import com.scoding.metro.entity.ReplyDO;
import com.scoding.metro.exception.BusinessException;
import com.scoding.metro.mapper.FeedbackMapper;
import com.scoding.metro.mapper.ReplyMapper;
import com.scoding.metro.mapper.UserMapper;
import com.scoding.metro.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackMapper feedbackMapper;
    private final ReplyMapper replyMapper;
    private final UserMapper userMapper;

    @Override
    public List<FeedbackDO> listFeedbacks() {
        List<FeedbackDO> feedbacks = feedbackMapper.listFeedbacks();
        
        // 为每个反馈加载回复
        for (FeedbackDO feedback : feedbacks) {
            List<ReplyDO> replies = replyMapper.listRepliesByParent(feedback.getId(), "FEEDBACK");
            feedback.setReplies(replies);
        }
        
        return feedbacks;
    }

    @Override
    public FeedbackDO getFeedbackById(Long id) {
        FeedbackDO feedback = feedbackMapper.getFeedbackById(id);
        if (feedback == null) {
            throw new BusinessException(RCode.NOT_FOUND, "反馈不存在");
        }
        
        // 加载回复
        List<ReplyDO> replies = replyMapper.listRepliesByParent(id, "FEEDBACK");
        feedback.setReplies(replies);
        
        return feedback;
    }
    
    @Override
    public List<FeedbackDO> listUserFeedbacks(Long userId) {
        List<FeedbackDO> feedbacks = feedbackMapper.listFeedbacksByUserId(userId);
        
        // 为每个反馈加载回复
        for (FeedbackDO feedback : feedbacks) {
            List<ReplyDO> replies = replyMapper.listRepliesByParent(feedback.getId(), "FEEDBACK");
            feedback.setReplies(replies);
        }
        
        return feedbacks;
    }

    @Override
    @Transactional
    public FeedbackDO saveFeedback(FeedbackDTO feedbackDTO, Long userId) {
        FeedbackDO feedback = new FeedbackDO();
        feedback.setUserId(userId);
        feedback.setContent(feedbackDTO.getContent());
        feedback.setRating(feedbackDTO.getRating());
        feedback.setCreatedAt(LocalDateTime.now());
        
        // 获取用户名
        String username = userMapper.getUserById(userId).getUsername();
        feedback.setUsername(username);

        feedbackMapper.saveFeedback(feedback);
        feedback.setReplies(List.of());
        
        return feedback;
    }

    @Override
    @Transactional
    public ReplyDO replyToFeedback(Long id, ReplyDTO replyDTO, Long userId, boolean isAdmin) {
        // 确保反馈存在
        getFeedbackById(id);
        
        ReplyDO reply = new ReplyDO();
        reply.setUserId(userId);
        reply.setParentId(id);
        reply.setParentType("FEEDBACK");
        reply.setContent(replyDTO.getContent());
        reply.setCreatedAt(LocalDateTime.now());
        reply.setIsAdmin(isAdmin);
        
        // 获取用户名
        String username = userMapper.getUserById(userId).getUsername();
        reply.setUsername(username);

        replyMapper.saveReply(reply);
        return reply;
    }

    @Override
    @Transactional
    public void removeFeedback(Long id) {
        // 删除相关回复
        replyMapper.removeRepliesByParent(id, "FEEDBACK");
        
        // 删除反馈
        feedbackMapper.removeFeedback(id);
    }
} 