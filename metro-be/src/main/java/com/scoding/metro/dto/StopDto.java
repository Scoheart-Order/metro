package com.scoding.metro.dto;

import lombok.Data;

/**
 * 停靠点数据传输对象
 * 
 * @author scoheart
 */
@Data
public class StopDto {
    /**
     * 停靠点ID
     */
    private Long id;
    
    /**
     * 所属路线ID
     */
    private Long routeId;
    
    /**
     * 路线名称（非数据库字段，仅用于展示）
     */
    private String routeName;
    
    /**
     * 站点ID
     */
    private Long stationId;
    
    /**
     * 站点名称（非数据库字段，仅用于展示）
     */
    private String stationName;
    
    /**
     * 在路线中的顺序
     */
    private Integer seq;
} 