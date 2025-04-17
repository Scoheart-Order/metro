package com.scoding.metro.entity;

import lombok.Data;

@Data
public class Stop {
    private Long id;
    private Long routeId;
    private Long stationId;
    private Integer seq;
    private Boolean isTransfer;
    
    // Non-database fields
    private Route route;
    private Station station;
} 