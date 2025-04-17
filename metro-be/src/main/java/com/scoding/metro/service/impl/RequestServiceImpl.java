package com.scoding.metro.service.impl;

import com.scoding.metro.common.RCode;
import com.scoding.metro.dto.RequestDTO;
import com.scoding.metro.dto.ReplyDTO;
import com.scoding.metro.entity.RequestDO;
import com.scoding.metro.entity.ReplyDO;
import com.scoding.metro.exception.BusinessException;
import com.scoding.metro.mapper.RequestMapper;
import com.scoding.metro.mapper.ReplyMapper;
import com.scoding.metro.mapper.UserMapper;
import com.scoding.metro.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestMapper requestMapper;
    private final ReplyMapper replyMapper;
    private final UserMapper userMapper;

    @Override
    public List<RequestDO> listRequests() {
        List<RequestDO> requests = requestMapper.listRequests();
        
        // 为每个需求加载回复
        for (RequestDO request : requests) {
            List<ReplyDO> replies = replyMapper.listRepliesByParent(request.getId(), "REQUEST");
            request.setReplies(replies);
        }
        
        return requests;
    }

    @Override
    public RequestDO getRequestById(Long id) {
        RequestDO request = requestMapper.getRequestById(id);
        if (request == null) {
            throw new BusinessException(RCode.NOT_FOUND, "需求不存在");
        }
        
        // 加载回复
        List<ReplyDO> replies = replyMapper.listRepliesByParent(id, "REQUEST");
        request.setReplies(replies);
        
        return request;
    }
    
    @Override
    public List<RequestDO> listUserRequests(Long userId) {
        List<RequestDO> requests = requestMapper.listRequestsByUserId(userId);
        
        // 为每个需求加载回复
        for (RequestDO request : requests) {
            List<ReplyDO> replies = replyMapper.listRepliesByParent(request.getId(), "REQUEST");
            request.setReplies(replies);
        }
        
        return requests;
    }

    @Override
    @Transactional
    public RequestDO saveRequest(RequestDTO requestDTO, Long userId) {
        RequestDO request = new RequestDO();
        request.setUserId(userId);
        request.setTitle(requestDTO.getTitle());
        request.setContent(requestDTO.getContent());
        request.setStatus("pending"); // 初始状态
        request.setCreatedAt(LocalDateTime.now());
        
        // 获取用户名
        String username = userMapper.getUserById(userId).getUsername();
        request.setUsername(username);

        requestMapper.saveRequest(request);
        request.setReplies(List.of());
        
        return request;
    }
    
    @Override
    @Transactional
    public RequestDO updateRequestStatus(Long id, String status) {
        // 确保需求存在
        RequestDO request = getRequestById(id);
        
        // 验证状态值
        if (!List.of("pending", "processing", "resolved", "rejected").contains(status)) {
            throw new BusinessException(RCode.BAD_REQUEST, "无效的状态值");
        }
        
        requestMapper.updateRequestStatus(id, status);
        
        // 重新获取更新后的需求
        return getRequestById(id);
    }

    @Override
    @Transactional
    public ReplyDO replyToRequest(Long id, ReplyDTO replyDTO, Long userId, boolean isAdmin) {
        // 确保需求存在
        getRequestById(id);
        
        ReplyDO reply = new ReplyDO();
        reply.setUserId(userId);
        reply.setParentId(id);
        reply.setParentType("REQUEST");
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
    public void removeRequest(Long id) {
        // 删除相关回复
        replyMapper.removeRepliesByParent(id, "REQUEST");
        
        // 删除需求
        requestMapper.removeRequest(id);
    }
} 