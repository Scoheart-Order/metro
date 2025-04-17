package com.scoding.metro.entity;

import lombok.Data;

import java.util.List;

@Data
public class Line {
    private Long id;
    private String name;
    private String code;
    private String color;
    private String operator;
    
    // Non-database fields
    private List<Route> routes;
} 