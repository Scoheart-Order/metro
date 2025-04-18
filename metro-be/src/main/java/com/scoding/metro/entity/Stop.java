package com.scoding.metro.entity;

import lombok.Data;

import java.time.LocalTime;

@Data
public class Stop {
    private Long id;
    private Long routeId;
    private Long stationId;
    private Integer seq;
    private LocalTime arrivalTime;    // 到站时间
    private LocalTime departureTime;  // 发车时间
    
    // Non-database fields
    private Route route;
    private Station station;
} 