package com.scoding.metro.service.impl;

import com.scoding.metro.dto.StopDto;
import com.scoding.metro.entity.Route;
import com.scoding.metro.entity.Station;
import com.scoding.metro.entity.Stop;
import com.scoding.metro.exception.BusinessException;
import com.scoding.metro.mapper.RouteMapper;
import com.scoding.metro.mapper.StationMapper;
import com.scoding.metro.mapper.StopMapper;
import com.scoding.metro.service.StopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StopServiceImpl implements StopService {
    
    private final StopMapper stopMapper;
    private final RouteMapper routeMapper;
    private final StationMapper stationMapper;
    
    @Override
    public List<StopDto> getAllStops() {
        return stopMapper.getAllStops().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public StopDto getStopById(Long id) {
        Stop stop = stopMapper.getStopById(id);
        if (stop == null) {
            throw new BusinessException("站点停靠信息不存在");
        }
        return convertToDto(stop);
    }
    
    @Override
    public List<StopDto> getStopsByRouteId(Long routeId) {
        List<Stop> stops = stopMapper.getStopsByRouteId(routeId);
        return stops.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<StopDto> getStopsByStationId(Long stationId) {
        List<Stop> stops = stopMapper.getStopsByStationId(stationId);
        return stops.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public StopDto getStopByRouteAndStation(Long routeId, Long stationId) {
        Stop stop = stopMapper.getStopByRouteAndStation(routeId, stationId);
        if (stop == null) {
            throw new BusinessException("站点停靠信息不存在");
        }
        return convertToDto(stop);
    }
    
    @Override
    @Transactional
    public StopDto createStop(StopDto stopDto) {
        // 验证路线是否存在
        Route route = routeMapper.getRouteById(stopDto.getRouteId());
        if (route == null) {
            throw new BusinessException("路线不存在");
        }
        
        // 验证站点是否存在
        Station station = stationMapper.getStationById(stopDto.getStationId());
        if (station == null) {
            throw new BusinessException("站点不存在");
        }
        
        // 检查该路线下是否已存在该站点
        Stop existingStop = stopMapper.getStopByRouteAndStation(stopDto.getRouteId(), stopDto.getStationId());
        if (existingStop != null) {
            throw new BusinessException("该路线下已存在此站点");
        }
        
        // 检查序号是否已被使用
        List<Stop> stops = stopMapper.getStopsByRouteId(stopDto.getRouteId());
        boolean seqExists = stops.stream()
                .anyMatch(s -> s.getSeq().equals(stopDto.getSeq()));
        if (seqExists) {
            throw new BusinessException("该路线下已存在相同序号的站点");
        }
        
        Stop stop = new Stop();
        stop.setRouteId(stopDto.getRouteId());
        stop.setStationId(stopDto.getStationId());
        stop.setSeq(stopDto.getSeq());
        stop.setArrivalTime(stopDto.getArrivalTime());
        stop.setDepartureTime(stopDto.getDepartureTime());
        
        stopMapper.insertStop(stop);
        
        return convertToDto(stop);
    }
    
    @Override
    @Transactional
    public StopDto updateStop(Long id, StopDto stopDto) {
        Stop existingStop = stopMapper.getStopById(id);
        if (existingStop == null) {
            throw new BusinessException("站点停靠信息不存在");
        }
        
        // 验证路线是否存在
        Route route = routeMapper.getRouteById(stopDto.getRouteId());
        if (route == null) {
            throw new BusinessException("路线不存在");
        }
        
        // 验证站点是否存在
        Station station = stationMapper.getStationById(stopDto.getStationId());
        if (station == null) {
            throw new BusinessException("站点不存在");
        }
        
        // 如果路线或站点已更改，检查新的组合是否已存在
        if (!existingStop.getRouteId().equals(stopDto.getRouteId()) || 
                !existingStop.getStationId().equals(stopDto.getStationId())) {
            Stop stopWithSameCombination = stopMapper.getStopByRouteAndStation(
                    stopDto.getRouteId(), stopDto.getStationId());
            if (stopWithSameCombination != null && !stopWithSameCombination.getId().equals(id)) {
                throw new BusinessException("该路线下已存在此站点");
            }
        }
        
        // 如果序号已更改，检查新序号是否已被使用
        if (!existingStop.getSeq().equals(stopDto.getSeq())) {
            List<Stop> stops = stopMapper.getStopsByRouteId(stopDto.getRouteId());
            boolean seqExists = stops.stream()
                    .filter(s -> !s.getId().equals(id))
                    .anyMatch(s -> s.getSeq().equals(stopDto.getSeq()));
            if (seqExists) {
                throw new BusinessException("该路线下已存在相同序号的站点");
            }
        }
        
        Stop stop = new Stop();
        stop.setId(id);
        stop.setRouteId(stopDto.getRouteId());
        stop.setStationId(stopDto.getStationId());
        stop.setSeq(stopDto.getSeq());
        stop.setArrivalTime(stopDto.getArrivalTime());
        stop.setDepartureTime(stopDto.getDepartureTime());
        
        stopMapper.updateStop(stop);
        
        return convertToDto(stop);
    }
    
    @Override
    @Transactional
    public boolean deleteStop(Long id) {
        Stop stop = stopMapper.getStopById(id);
        if (stop == null) {
            throw new BusinessException("站点停靠信息不存在");
        }
        
        return stopMapper.deleteStop(id) > 0;
    }
    
    private StopDto convertToDto(Stop stop) {
        StopDto dto = new StopDto();
        dto.setId(stop.getId());
        dto.setRouteId(stop.getRouteId());
        dto.setStationId(stop.getStationId());
        dto.setSeq(stop.getSeq());
        dto.setArrivalTime(stop.getArrivalTime());
        dto.setDepartureTime(stop.getDepartureTime());
        
        // 获取路线名称
        if (stop.getRoute() != null) {
            dto.setRouteName(stop.getRoute().getName());
        } else if (stop.getRouteId() != null) {
            Route route = routeMapper.getRouteById(stop.getRouteId());
            if (route != null) {
                dto.setRouteName(route.getName());
            }
        }
        
        // 获取站点名称
        if (stop.getStation() != null) {
            dto.setStationName(stop.getStation().getName());
        } else if (stop.getStationId() != null) {
            Station station = stationMapper.getStationById(stop.getStationId());
            if (station != null) {
                dto.setStationName(station.getName());
            }
        }
        
        return dto;
    }
} 