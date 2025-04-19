package com.scoding.metro.mapper;

import com.scoding.metro.entity.TrainTrip;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 列车行程数据访问接口
 * SQL查询已移至XML映射文件
 */
@Mapper
public interface TrainTripMapper {
    
    /**
     * 查询所有列车行程
     * 
     * @return 列车行程列表
     */
    List<TrainTrip> selectAll();
    
    /**
     * 根据ID查询列车行程
     * 
     * @param id 列车行程ID
     * @return 列车行程信息
     */
    TrainTrip selectById(Long id);
    
    /**
     * 根据路线ID查询列车行程
     * 
     * @param routeId 路线ID
     * @return 列车行程列表
     */
    List<TrainTrip> selectByRouteId(Long routeId);
    
    /**
     * 插入列车行程
     * 
     * @param trainTrip 列车行程信息
     * @return 影响行数
     */
    int insert(TrainTrip trainTrip);
    
    /**
     * 更新列车行程
     * 
     * @param trainTrip 列车行程信息
     * @return 影响行数
     */
    int update(TrainTrip trainTrip);
    
    /**
     * 删除列车行程
     * 
     * @param id 列车行程ID
     * @return 影响行数
     */
    int delete(Long id);
} 