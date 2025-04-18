package com.scoding.metro.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TrainTripDto {
    private Long id;
    private Long routeId;
    private String trainNumber;
    private LocalDate runDate;
    private List<Long> stopTimeIds;
    
    // 非数据库字段，仅用于前端展示
    private String routeName;
    private String lineName;
    private String lineColor;
} 