package com.scoding.metro.service;

import com.scoding.metro.dto.LineDto;
import com.scoding.metro.entity.Line;

import java.util.List;

public interface LineService {
    
    List<LineDto> getAllLines();
    
    LineDto getLineById(Long id);
    
    LineDto getLineByCode(String code);
    
    LineDto createLine(LineDto lineDto);
    
    LineDto updateLine(Long id, LineDto lineDto);
    
    boolean deleteLine(Long id);
    
    LineDto getLineWithRoutes(Long id);
} 