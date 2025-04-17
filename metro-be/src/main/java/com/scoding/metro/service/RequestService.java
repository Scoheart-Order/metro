package com.scoding.metro.service;

import com.scoding.metro.dto.RequestDTO;
import com.scoding.metro.dto.ReplyDTO;
import com.scoding.metro.entity.RequestDO;
import com.scoding.metro.entity.ReplyDO;

import java.util.List;

public interface RequestService {
    /**
     * 获取所有需求
     *
     * @return 需求列表
     */
    List<RequestDO> listRequests();

    /**
     * 根据ID获取需求
     *
     * @param id 需求ID
     * @return 需求信息
     */
    RequestDO getRequestById(Long id);
    
    /**
     * 获取用户的所有需求
     *
     * @param userId 用户ID
     * @return 需求列表
     */
    List<RequestDO> listUserRequests(Long userId);

    /**
     * 创建需求
     *
     * @param requestDTO 需求信息
     * @param userId 用户ID
     * @return 创建的需求
     */
    RequestDO saveRequest(RequestDTO requestDTO, Long userId);
    
    /**
     * 更新需求状态
     *
     * @param id 需求ID
     * @param status 新状态
     * @return 更新后的需求
     */
    RequestDO updateRequestStatus(Long id, String status);

    /**
     * 回复需求
     *
     * @param id 需求ID
     * @param replyDTO 回复内容
     * @param userId 用户ID
     * @param isAdmin 是否管理员
     * @return 创建的回复
     */
    ReplyDO replyToRequest(Long id, ReplyDTO replyDTO, Long userId, boolean isAdmin);

    /**
     * 删除需求
     *
     * @param id 需求ID
     */
    void removeRequest(Long id);
} 