package com.scoding.metro.dto;

import lombok.Data;

import java.util.List;

@Data
public class RouteDto {
    private Long id;
    private Long lineId;
    private String lineName;
    private String name;
    private Long startStationId;
    private String startStationName;
    private Long endStationId;
    private String endStationName;
    private List<StationDto> stations;
} 