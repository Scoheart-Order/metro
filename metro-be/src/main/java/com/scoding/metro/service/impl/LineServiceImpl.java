package com.scoding.metro.service.impl;

import com.scoding.metro.dto.LineDto;
import com.scoding.metro.entity.Line;
import com.scoding.metro.entity.Route;
import com.scoding.metro.exception.BusinessException;
import com.scoding.metro.mapper.LineMapper;
import com.scoding.metro.mapper.RouteMapper;
import com.scoding.metro.service.LineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {
    
    private final LineMapper lineMapper;
    private final RouteMapper routeMapper;
    
    @Override
    public List<LineDto> getAllLines() {
        return lineMapper.getAllLines().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public LineDto getLineById(Long id) {
        Line line = lineMapper.getLineById(id);
        if (line == null) {
            throw new BusinessException("线路不存在");
        }
        return convertToDto(line);
    }
    
    @Override
    public LineDto getLineByCode(String code) {
        Line line = lineMapper.getLineByCode(code);
        if (line == null) {
            throw new BusinessException("线路不存在");
        }
        return convertToDto(line);
    }
    
    @Override
    @Transactional
    public LineDto createLine(LineDto lineDto) {
        Line line = convertToEntity(lineDto);
        
        // 检查线路编码是否已存在
        Line existingLine = lineMapper.getLineByCode(line.getCode());
        if (existingLine != null) {
            throw new BusinessException("线路编码已存在");
        }
        
        lineMapper.insertLine(line);
        return convertToDto(line);
    }
    
    @Override
    @Transactional
    public LineDto updateLine(Long id, LineDto lineDto) {
        Line existingLine = lineMapper.getLineById(id);
        if (existingLine == null) {
            throw new BusinessException("线路不存在");
        }
        
        // 如果编码已更改，检查新编码是否已被使用
        if (!existingLine.getCode().equals(lineDto.getCode())) {
            Line lineWithCode = lineMapper.getLineByCode(lineDto.getCode());
            if (lineWithCode != null) {
                throw new BusinessException("线路编码已存在");
            }
        }
        
        Line line = convertToEntity(lineDto);
        line.setId(id);
        lineMapper.updateLine(line);
        
        return convertToDto(line);
    }
    
    @Override
    @Transactional
    public boolean deleteLine(Long id) {
        Line line = lineMapper.getLineById(id);
        if (line == null) {
            throw new BusinessException("线路不存在");
        }
        
        // 检查是否有关联的路线
        List<Route> routes = routeMapper.getRoutesByLineId(id);
        if (!routes.isEmpty()) {
            throw new BusinessException("该线路有关联的路线，无法删除");
        }
        
        return lineMapper.deleteLine(id) > 0;
    }
    
    @Override
    public LineDto getLineWithRoutes(Long id) {
        Line line = lineMapper.getLineById(id);
        if (line == null) {
            throw new BusinessException("线路不存在");
        }
        
        // 获取线路的所有路线
        List<Route> routes = routeMapper.getRoutesByLineId(id);
        line.setRoutes(routes);
        
        return convertToDto(line);
    }
    
    private LineDto convertToDto(Line line) {
        LineDto dto = new LineDto();
        dto.setId(line.getId());
        dto.setName(line.getName());
        dto.setCode(line.getCode());
        dto.setColor(line.getColor());
        dto.setOperator(line.getOperator());
        return dto;
    }
    
    private Line convertToEntity(LineDto dto) {
        Line line = new Line();
        line.setId(dto.getId());
        line.setName(dto.getName());
        line.setCode(dto.getCode());
        line.setColor(dto.getColor());
        line.setOperator(dto.getOperator());
        return line;
    }
} 