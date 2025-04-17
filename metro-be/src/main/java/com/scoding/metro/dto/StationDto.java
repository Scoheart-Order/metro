package com.scoding.metro.dto;

import lombok.Data;

@Data
public class StationDto {
    private Long id;
    private String name;
    private String code;
    private Boolean isTransfer; // Indicates if this is a transfer station
} 