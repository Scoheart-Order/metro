package com.scoding.metro.dto;

import lombok.Data;

@Data
public class LineDto {
    private Long id;
    private String name;
    private String code;
    private String color;
    private String operator;
} 