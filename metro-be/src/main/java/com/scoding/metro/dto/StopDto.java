package com.scoding.metro.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalTime;

@Data
public class StopDto {
    private Long id;
    private Long routeId;
    private String routeName;
    private Long stationId;
    private String stationName;
    private Integer seq;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime arrivalTime;    // 到站时间
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime departureTime;  // 发车时间
} 