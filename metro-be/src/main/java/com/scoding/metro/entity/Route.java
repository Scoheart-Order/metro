package com.scoding.metro.entity;

import lombok.Data;

@Data
public class Route {
    private Long id;
    private Long lineId;
    private String name;
    private Long startStationId;
    private Long endStationId;
    
    // 非数据库字段
    private Line line;
}