package com.scoding.metro.service.impl;

import com.scoding.metro.dto.StopDto;
import com.scoding.metro.dto.UpdateStopSequencesRequest;
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
import java.util.Map;
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
        
        // 验证不能将起始站或终点站作为停靠点
        if (station.getId().equals(route.getStartStationId())) {
            throw new BusinessException("不能将起始站作为停靠点，起始站已经是路线的第一站");
        }
        
        if (station.getId().equals(route.getEndStationId())) {
            throw new BusinessException("不能将终点站作为停靠点，终点站已经是路线的最后一站");
        }
        
        // 检查该路线下是否已存在该站点
        Stop existingStop = stopMapper.getStopByRouteAndStation(stopDto.getRouteId(), stopDto.getStationId());
        if (existingStop != null) {
            throw new BusinessException("该路线下已存在此站点");
        }
        
        // 获取当前路线上的所有停靠点
        List<Stop> stops = stopMapper.getStopsByRouteId(stopDto.getRouteId());
        
        // 初始化起始站和终点站的停靠点对象（如果不存在）
        Stop startStop = null;
        Stop endStop = null;
        
        // 查找现有的起始站和终点站停靠点
        for (Stop stop : stops) {
            if (stop.getStationId().equals(route.getStartStationId())) {
                startStop = stop;
            } else if (stop.getStationId().equals(route.getEndStationId())) {
                endStop = stop;
            }
        }
        
        // 如果起始站不存在，创建并插入起始站停靠点（seq=1）
        if (startStop == null) {
            startStop = new Stop();
            startStop.setRouteId(route.getId());
            startStop.setStationId(route.getStartStationId());
            startStop.setSeq(1);
            stopMapper.insertStop(startStop);
            
            // 更新stops集合
            stops = stopMapper.getStopsByRouteId(stopDto.getRouteId());
        }
        
        // 如果终点站不存在，创建并插入终点站停靠点（seq为最大值+1）
        if (endStop == null) {
            int maxSeq = stops.stream().mapToInt(Stop::getSeq).max().orElse(0);
            endStop = new Stop();
            endStop.setRouteId(route.getId());
            endStop.setStationId(route.getEndStationId());
            endStop.setSeq(maxSeq + 1);
            stopMapper.insertStop(endStop);
            
            // 更新stops集合
            stops = stopMapper.getStopsByRouteId(stopDto.getRouteId());
        }
        
        // 确保终点站始终是最后一个
        int maxSeq = stops.stream().mapToInt(Stop::getSeq).max().orElse(0);
        if (endStop.getSeq() != maxSeq) {
            // 如果终点站不是最后一个，调整其序号
            endStop.setSeq(maxSeq + 1);
            stopMapper.updateStop(endStop);
            
            // 更新stops集合
            stops = stopMapper.getStopsByRouteId(stopDto.getRouteId());
        }
        
        // 计算新停靠点的序号
        // 如果用户指定了序号，但超过了终点站的序号，则自动调整为终点站前一位
        int endStopSeq = endStop.getSeq();
        if (stopDto.getSeq() >= endStopSeq) {
            stopDto.setSeq(endStopSeq - 1);
        }
        
        // 如果用户指定的序号小于起始站的序号，则自动调整为起始站后一位
        int startStopSeq = startStop.getSeq();
        if (stopDto.getSeq() <= startStopSeq) {
            stopDto.setSeq(startStopSeq + 1);
        }
        
        // 如果已存在相同序号的停靠点，需要将该序号及之后的所有停靠点（除终点站外）的序号+1
        boolean seqExists = stops.stream()
                .anyMatch(s -> s.getSeq().equals(stopDto.getSeq()) && !s.getStationId().equals(route.getEndStationId()));
        
        if (seqExists) {
            // 找出需要调整序号的停靠点（序号大于等于新停靠点序号，并且不是终点站的）
            List<Stop> stopsToUpdate = stops.stream()
                    .filter(s -> s.getSeq() >= stopDto.getSeq() && !s.getStationId().equals(route.getEndStationId()))
                    .collect(Collectors.toList());
            
            // 从后向前调整序号，避免序号冲突
            stopsToUpdate.sort((s1, s2) -> Integer.compare(s2.getSeq(), s1.getSeq()));
            for (Stop s : stopsToUpdate) {
                s.setSeq(s.getSeq() + 1);
                stopMapper.updateStop(s);
            }
        }
        
        // 创建新的停靠点
        Stop stop = new Stop();
        stop.setRouteId(stopDto.getRouteId());
        stop.setStationId(stopDto.getStationId());
        stop.setSeq(stopDto.getSeq());
        
        stopMapper.insertStop(stop);
        
        // 确保终点站仍然是最后一个
        stops = stopMapper.getStopsByRouteId(stopDto.getRouteId());
        int newMaxSeq = stops.stream().mapToInt(Stop::getSeq).max().orElse(0);
        if (endStop.getSeq() != newMaxSeq) {
            endStop.setSeq(newMaxSeq + 1);
            stopMapper.updateStop(endStop);
        }
        
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
        
        // 验证不能将起始站或终点站作为停靠点
        if (station.getId().equals(route.getStartStationId())) {
            throw new BusinessException("不能将起始站作为停靠点，起始站已经是路线的第一站");
        }
        
        if (station.getId().equals(route.getEndStationId())) {
            throw new BusinessException("不能将终点站作为停靠点，终点站已经是路线的最后一站");
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
        
        // 获取当前路线上的所有停靠点
        List<Stop> stops = stopMapper.getStopsByRouteId(stopDto.getRouteId());
        
        // 查找起始站和终点站停靠点
        Stop startStop = null;
        Stop endStop = null;
        
        for (Stop stop : stops) {
            if (stop.getStationId().equals(route.getStartStationId())) {
                startStop = stop;
            } else if (stop.getStationId().equals(route.getEndStationId())) {
                endStop = stop;
            }
        }
        
        // 如果没有找到起始站和终点站停靠点，则抛出异常
        if (startStop == null) {
            throw new BusinessException("路线缺少起始站停靠点");
        }
        
        if (endStop == null) {
            throw new BusinessException("路线缺少终点站停靠点");
        }
        
        // 确保序号在起始站和终点站之间
        if (stopDto.getSeq() <= startStop.getSeq()) {
            stopDto.setSeq(startStop.getSeq() + 1);
        }
        
        if (stopDto.getSeq() >= endStop.getSeq()) {
            stopDto.setSeq(endStop.getSeq() - 1);
        }
        
        // 如果序号已更改，检查新序号是否已被使用
        if (!existingStop.getSeq().equals(stopDto.getSeq())) {
            // 检查是否存在使用相同序号的停靠点（不包括终点站和起始站）
            boolean seqExists = stops.stream()
                    .filter(s -> !s.getId().equals(id) && 
                                !s.getStationId().equals(route.getStartStationId()) && 
                                !s.getStationId().equals(route.getEndStationId()))
                    .anyMatch(s -> s.getSeq().equals(stopDto.getSeq()));
            
            if (seqExists) {
                // 找出需要调整序号的停靠点（序号大于等于新停靠点序号，并且不是终点站和起始站的）
                List<Stop> stopsToUpdate = stops.stream()
                        .filter(s -> !s.getId().equals(id) && 
                                    s.getSeq() >= stopDto.getSeq() && 
                                    !s.getStationId().equals(route.getEndStationId()) &&
                                    !s.getStationId().equals(route.getStartStationId()))
                        .collect(Collectors.toList());
                
                // 从后向前调整序号，避免序号冲突
                stopsToUpdate.sort((s1, s2) -> Integer.compare(s2.getSeq(), s1.getSeq()));
                for (Stop s : stopsToUpdate) {
                    s.setSeq(s.getSeq() + 1);
                    stopMapper.updateStop(s);
                }
            }
        }
        
        // 更新停靠点
        Stop stop = new Stop();
        stop.setId(id);
        stop.setRouteId(stopDto.getRouteId());
        stop.setStationId(stopDto.getStationId());
        stop.setSeq(stopDto.getSeq());
        
        stopMapper.updateStop(stop);
        
        // 确保终点站仍然是最后一个
        stops = stopMapper.getStopsByRouteId(stopDto.getRouteId());
        int maxSeq = stops.stream().mapToInt(Stop::getSeq).max().orElse(0);
        if (endStop.getSeq() != maxSeq) {
            endStop.setSeq(maxSeq + 1);
            stopMapper.updateStop(endStop);
        }
        
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
    
    @Override
    @Transactional
    public boolean updateStopSequences(UpdateStopSequencesRequest request) {
        // 验证路线是否存在
        Route route = routeMapper.getRouteById(request.getRouteId());
        if (route == null) {
            throw new BusinessException("路线不存在");
        }
        
        // 获取该路线下所有经停站
        List<Stop> stops = stopMapper.getStopsByRouteId(request.getRouteId());
        if (stops.isEmpty()) {
            throw new BusinessException("该路线下没有经停站");
        }
        
        // 创建经停站ID到对象的映射，方便后续查找
        Map<Long, Stop> stopMap = stops.stream()
                .collect(Collectors.toMap(Stop::getId, stop -> stop));
        
        // 查找起始站和终点站
        Stop startStop = stops.stream()
                .filter(s -> s.getStationId().equals(route.getStartStationId()))
                .findFirst()
                .orElse(null);
                
        Stop endStop = stops.stream()
                .filter(s -> s.getStationId().equals(route.getEndStationId()))
                .findFirst()
                .orElse(null);
        
        // 确保起始站和终点站存在
        if (startStop == null || endStop == null) {
            throw new BusinessException("路线缺少起始站或终点站");
        }
        
        // 更新经停站序号
        boolean success = true;
        for (UpdateStopSequencesRequest.StopSequence seq : request.getStopSequences()) {
            Stop stop = stopMap.get(seq.getId());
            if (stop == null) {
                // 跳过不存在的经停站
                continue;
            }
            
            // 起始站和终点站的序号不允许修改
            if (stop.getStationId().equals(route.getStartStationId()) || 
                stop.getStationId().equals(route.getEndStationId())) {
                continue;
            }
            
            // 设置新序号
            stop.setSeq(seq.getSeq());
            int result = stopMapper.updateStop(stop);
            if (result <= 0) {
                success = false;
            }
        }
        
        // 确保终点站仍然是最后一个
        stops = stopMapper.getStopsByRouteId(request.getRouteId());
        int maxSeq = stops.stream().mapToInt(Stop::getSeq).max().orElse(0);
        if (endStop.getSeq() != maxSeq) {
            endStop.setSeq(maxSeq + 1);
            stopMapper.updateStop(endStop);
        }
        
        return success;
    }
    
    private StopDto convertToDto(Stop stop) {
        StopDto dto = new StopDto();
        dto.setId(stop.getId());
        dto.setRouteId(stop.getRouteId());
        dto.setStationId(stop.getStationId());
        dto.setSeq(stop.getSeq());
        
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