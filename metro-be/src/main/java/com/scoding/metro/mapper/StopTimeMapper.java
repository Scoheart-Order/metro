package com.scoding.metro.mapper;

import com.scoding.metro.entity.StopTime;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 到站时刻数据访问接口
 * SQL查询已移至XML映射文件
 */
@Mapper
public interface StopTimeMapper {
    
    /**
     * 查询所有到站时刻
     * 
     * @return 到站时刻列表
     */
    List<StopTime> selectAll();
    
    /**
     * 根据ID查询到站时刻
     * 
     * @param id 到站时刻ID
     * @return 到站时刻信息
     */
    StopTime selectById(Long id);
    
    /**
     * 根据列车行程ID查询到站时刻
     * 
     * @param trainTripId 列车行程ID
     * @return 到站时刻列表
     */
    List<StopTime> selectByTrainTripId(Long trainTripId);
    
    /**
     * 根据停靠点ID查询到站时刻
     * 
     * @param stopId 停靠点ID
     * @return 到站时刻列表
     */
    List<StopTime> selectByStopId(Long stopId);
    
    /**
     * 插入到站时刻
     * 
     * @param stopTime 到站时刻信息
     * @return 影响行数
     */
    int insert(StopTime stopTime);
    
    /**
     * 更新到站时刻
     * 
     * @param stopTime 到站时刻信息
     * @return 影响行数
     */
    int update(StopTime stopTime);
    
    /**
     * 删除到站时刻
     * 
     * @param id 到站时刻ID
     * @return 影响行数
     */
    int delete(Long id);
} 