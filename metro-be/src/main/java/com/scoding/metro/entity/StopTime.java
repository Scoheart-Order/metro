package com.scoding.metro.entity;

import lombok.Data;

import java.time.LocalTime;

@Data
public class StopTime {
    private Long id;
    private Long trainTripId;
    private Long stopId;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private Integer stopSeq;
    
    // Non-database fields
    private TrainTrip trainTrip;
    private Stop stop;
} 