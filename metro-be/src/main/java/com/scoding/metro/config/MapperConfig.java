package com.scoding.metro.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.scoding.metro.mapper")
public class MapperConfig {
} 