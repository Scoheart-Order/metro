package com.scoding.metro.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class StopTimeDto {
    private Long id;
    private Long trainTripId;
    private Long stopId;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private Integer stopSeq;
} 