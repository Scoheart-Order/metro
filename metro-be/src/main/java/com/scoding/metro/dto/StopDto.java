package com.scoding.metro.dto;

import lombok.Data;

@Data
public class StopDto {
    private Long id;
    private Long routeId;
    private String routeName;
    private Long stationId;
    private String stationName;
    private Integer seq;
    private Boolean isTransfer;
} 