package com.scoding.metro.entity;

import lombok.Data;
import java.time.LocalTime;

/**
 * 停靠点实体类
 * 表示路线上的一个停靠站点及其顺序
 * 
 * @author scoheart
 */
@Data
public class Stop {
    /**
     * 停靠点ID
     */
    private Long id;
    
    /**
     * 所属路线ID
     */
    private Long routeId;
    
    /**
     * 站点ID
     */
    private Long stationId;
    
    /**
     * 在路线中的顺序
     */
    private Integer seq;
    
    /**
     * 到达时间
     */
    private LocalTime arrivalTime;
    
    /**
     * 出发时间
     */
    private LocalTime departureTime;
    
    /**
     * 所属路线（非数据库字段）
     */
    private Route route;
    
    /**
     * 关联站点（非数据库字段）
     */
    private Station station;
} 