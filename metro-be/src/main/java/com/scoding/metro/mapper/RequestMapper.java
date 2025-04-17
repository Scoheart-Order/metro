package com.scoding.metro.mapper;

import com.scoding.metro.entity.RequestDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RequestMapper {
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
    RequestDO getRequestById(@Param("id") Long id);
    
    /**
     * 根据用户ID获取需求
     *
     * @param userId 用户ID
     * @return 需求列表
     */
    List<RequestDO> listRequestsByUserId(@Param("userId") Long userId);

    /**
     * 保存需求
     *
     * @param requestDO 需求信息
     * @return 影响行数
     */
    int saveRequest(RequestDO requestDO);

    /**
     * 更新需求
     *
     * @param requestDO 需求信息
     * @return 影响行数
     */
    int updateRequest(RequestDO requestDO);
    
    /**
     * 更新需求状态
     *
     * @param id 需求ID
     * @param status 新状态
     * @return 影响行数
     */
    int updateRequestStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 删除需求
     *
     * @param id 需求ID
     * @return 影响行数
     */
    int removeRequest(@Param("id") Long id);
} 