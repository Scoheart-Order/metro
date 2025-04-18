package com.scoding.metro.entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TrainTrip {
    private Long id;
    private Long routeId;
    private String trainNumber;
    private LocalDate runDate;
    
    // Non-database fields
    private Route route;
    private List<StopTime> stopTimes;
} 