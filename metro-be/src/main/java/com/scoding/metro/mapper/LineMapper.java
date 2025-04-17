package com.scoding.metro.mapper;

import com.scoding.metro.entity.Line;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LineMapper {
    
    List<Line> getAllLines();
    
    Line getLineById(Long id);
    
    Line getLineByCode(String code);
    
    int insertLine(Line line);
    
    int updateLine(Line line);
    
    int deleteLine(Long id);
} 