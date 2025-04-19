package com.scoding.metro.service.impl;

import com.scoding.metro.dto.TrainTripDto;
import com.scoding.metro.entity.Line;
import com.scoding.metro.entity.Route;
import com.scoding.metro.entity.TrainTrip;
import com.scoding.metro.exception.BusinessException;
import com.scoding.metro.mapper.LineMapper;
import com.scoding.metro.mapper.RouteMapper;
import com.scoding.metro.mapper.TrainTripMapper;
import com.scoding.metro.service.StopTimeService;
import com.scoding.metro.service.TrainTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainTripServiceImpl implements TrainTripService {
    
    private final TrainTripMapper trainTripMapper;
    private final RouteMapper routeMapper;
    private final LineMapper lineMapper;
    private final StopTimeService stopTimeService;
    
    @Override
    public List<TrainTripDto> getAllTrainTrips() {
        List<TrainTrip> trainTrips = trainTripMapper.selectAll();
        
        // 预加载所有涉及到的路线信息，提高性能
        List<Long> routeIds = trainTrips.stream()
                .map(TrainTrip::getRouteId)
                .filter(routeId -> routeId != null) // 确保只处理非空的routeId
                .distinct()
                .collect(Collectors.toList());
        
        // 获取所有相关路线
        Map<Long, Route> routeMap = new java.util.HashMap<>();
        if (!routeIds.isEmpty()) {
            List<Route> routes = routeIds.stream()
                    .map(routeMapper::getRouteById)
                    .filter(route -> route != null)
                    .collect(Collectors.toList());
            
            // 预加载所有涉及到的线路信息
            List<Long> lineIds = routes.stream()
                    .map(Route::getLineId)
                    .filter(lineId -> lineId != null) // 确保只处理非空的lineId
                    .distinct()
                    .collect(Collectors.toList());
            
            // 获取所有相关线路并创建映射
            Map<Long, Line> lineMap = new java.util.HashMap<>();
            if (!lineIds.isEmpty()) {
                lineMap = lineIds.stream()
                        .map(lineMapper::getLineById)
                        .filter(line -> line != null)
                        .collect(Collectors.toMap(Line::getId, line -> line));
            }
            
            // 将线路信息加载到路线中
            for (Route route : routes) {
                if (route.getLineId() != null) {
                    route.setLine(lineMap.get(route.getLineId()));
                }
            }
            
            // 创建路线ID到路线的映射
            routeMap = routes.stream()
                    .collect(Collectors.toMap(Route::getId, route -> route));
        }
        
        // 将路线信息加载到列车行程实体中
        for (TrainTrip trainTrip : trainTrips) {
            if (trainTrip.getRouteId() != null) {
                trainTrip.setRoute(routeMap.get(trainTrip.getRouteId()));
            }
        }
        
        return trainTrips.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public TrainTripDto getTrainTripById(Long id) {
        TrainTrip trainTrip = trainTripMapper.selectById(id);
        if (trainTrip == null) {
            throw new BusinessException("列车行程不存在: " + id);
        }
        
        // 如果有路线ID，则加载路线信息
        if (trainTrip.getRouteId() != null) {
            Route route = routeMapper.getRouteById(trainTrip.getRouteId());
            if (route != null) {
                // 如果路线有线路ID，则加载线路信息
                if (route.getLineId() != null) {
                    Line line = lineMapper.getLineById(route.getLineId());
                    route.setLine(line);
                }
                trainTrip.setRoute(route);
            }
        }
        
        return convertToDto(trainTrip);
    }
    
    @Override
    public List<TrainTripDto> getTrainTripsByRouteId(Long routeId) {
        // 验证路线是否存在
        Route route = routeMapper.getRouteById(routeId);
        if (route == null) {
            throw new BusinessException("路线不存在: " + routeId);
        }
        
        // 如果路线有线路ID，则加载线路信息
        if (route.getLineId() != null) {
            Line line = lineMapper.getLineById(route.getLineId());
            route.setLine(line);
        }
        
        List<TrainTrip> trainTrips = trainTripMapper.selectByRouteId(routeId);
        
        // 将路线信息加载到每个列车行程中
        for (TrainTrip trainTrip : trainTrips) {
            trainTrip.setRoute(route);
        }
        
        return trainTrips.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public TrainTripDto getTrainTripWithStopTimes(Long id) {
        TrainTrip trainTrip = trainTripMapper.selectById(id);
        if (trainTrip == null) {
            throw new BusinessException("列车行程不存在: " + id);
        }
        
        // 加载路线信息
        Route route = routeMapper.getRouteById(trainTrip.getRouteId());
        if (route != null) {
            // 加载线路信息
            Line line = lineMapper.getLineById(route.getLineId());
            route.setLine(line);
            trainTrip.setRoute(route);
        }
        
        TrainTripDto trainTripDto = convertToDto(trainTrip);
        
        // 添加停靠时刻信息
        trainTripDto.setStopTimeIds(
                stopTimeService.getStopTimesByTrainTripId(id).stream()
                        .map(dto -> dto.getId())
                        .collect(Collectors.toList())
        );
        
        return trainTripDto;
    }
    
    @Override
    @Transactional
    public TrainTripDto createTrainTrip(TrainTripDto trainTripDto) {
        TrainTrip trainTrip = convertToEntity(trainTripDto);
        
        // 如果提供了routeId，验证路线是否存在
        if (trainTripDto.getRouteId() != null) {
            Route route = routeMapper.getRouteById(trainTripDto.getRouteId());
            if (route == null) {
                throw new BusinessException("路线不存在: " + trainTripDto.getRouteId());
            }
            
            // 加载线路信息
            if (route.getLineId() != null) {
                Line line = lineMapper.getLineById(route.getLineId());
                route.setLine(line);
            }
            
            // 设置路线信息
            trainTrip.setRoute(route);
        }
        
        trainTripMapper.insert(trainTrip);
        return convertToDto(trainTrip);
    }
    
    @Override
    @Transactional
    public TrainTripDto updateTrainTrip(Long id, TrainTripDto trainTripDto) {
        TrainTrip existing = trainTripMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("列车行程不存在: " + id);
        }
        
        TrainTrip trainTrip = convertToEntity(trainTripDto);
        trainTrip.setId(id);
        
        // 如果提供了routeId，验证路线是否存在
        if (trainTripDto.getRouteId() != null) {
            Route route = routeMapper.getRouteById(trainTripDto.getRouteId());
            if (route == null) {
                throw new BusinessException("路线不存在: " + trainTripDto.getRouteId());
            }
            
            // 加载线路信息
            if (route.getLineId() != null) {
                Line line = lineMapper.getLineById(route.getLineId());
                route.setLine(line);
            }
            
            // 设置路线信息
            trainTrip.setRoute(route);
        }
        
        trainTripMapper.update(trainTrip);
        return convertToDto(trainTrip);
    }
    
    @Override
    @Transactional
    public Boolean deleteTrainTrip(Long id) {
        TrainTrip existing = trainTripMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("列车行程不存在: " + id);
        }
        
        return trainTripMapper.delete(id) > 0;
    }
    
    // Helper methods
    private TrainTripDto convertToDto(TrainTrip trainTrip) {
        if (trainTrip == null) {
            return null;
        }
        
        TrainTripDto dto = new TrainTripDto();
        dto.setId(trainTrip.getId());
        dto.setRouteId(trainTrip.getRouteId());
        dto.setTrainNumber(trainTrip.getTrainNumber());
        dto.setRunDate(trainTrip.getRunDate());
        
        // 添加路线信息，如果存在的话
        if (trainTrip.getRoute() != null) {
            dto.setRouteName(trainTrip.getRoute().getName());
            if (trainTrip.getRoute().getLine() != null) {
                dto.setLineName(trainTrip.getRoute().getLine().getName());
                dto.setLineColor(trainTrip.getRoute().getLine().getColor());
            } else {
                // 为空字段设置默认值，而不是null
                dto.setLineName("");
                dto.setLineColor("");
            }
        } else {
            // 为空字段设置默认值，而不是null
            dto.setRouteName("");
            dto.setLineName("");
            dto.setLineColor("");
        }
        
        return dto;
    }
    
    private TrainTrip convertToEntity(TrainTripDto dto) {
        if (dto == null) {
            return null;
        }
        
        TrainTrip entity = new TrainTrip();
        entity.setId(dto.getId());
        entity.setRouteId(dto.getRouteId());
        entity.setTrainNumber(dto.getTrainNumber());
        entity.setRunDate(dto.getRunDate());
        
        return entity;
    }
} 