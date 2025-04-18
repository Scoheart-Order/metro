package com.scoding.metro.service.impl;

import com.scoding.metro.dto.StopTimeDto;
import com.scoding.metro.entity.StopTime;
import com.scoding.metro.mapper.StopTimeMapper;
import com.scoding.metro.service.StopTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StopTimeServiceImpl implements StopTimeService {
    
    private final StopTimeMapper stopTimeMapper;
    
    @Override
    public List<StopTimeDto> getAllStopTimes() {
        return stopTimeMapper.selectAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public StopTimeDto getStopTimeById(Long id) {
        StopTime stopTime = stopTimeMapper.selectById(id);
        return convertToDto(stopTime);
    }
    
    @Override
    public List<StopTimeDto> getStopTimesByTrainTripId(Long trainTripId) {
        return stopTimeMapper.selectByTrainTripId(trainTripId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<StopTimeDto> getStopTimesByStopId(Long stopId) {
        return stopTimeMapper.selectByStopId(stopId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public StopTimeDto createStopTime(StopTimeDto stopTimeDto) {
        StopTime stopTime = convertToEntity(stopTimeDto);
        stopTimeMapper.insert(stopTime);
        
        return convertToDto(stopTime);
    }
    
    @Override
    @Transactional
    public StopTimeDto updateStopTime(Long id, StopTimeDto stopTimeDto) {
        StopTime existing = stopTimeMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("Stop time not found: " + id);
        }
        
        StopTime stopTime = convertToEntity(stopTimeDto);
        stopTime.setId(id);
        stopTimeMapper.update(stopTime);
        
        return convertToDto(stopTime);
    }
    
    @Override
    @Transactional
    public Boolean deleteStopTime(Long id) {
        return stopTimeMapper.delete(id) > 0;
    }
    
    // Helper methods
    private StopTimeDto convertToDto(StopTime stopTime) {
        if (stopTime == null) {
            return null;
        }
        
        StopTimeDto dto = new StopTimeDto();
        dto.setId(stopTime.getId());
        dto.setTrainTripId(stopTime.getTrainTripId());
        dto.setStopId(stopTime.getStopId());
        dto.setArrivalTime(stopTime.getArrivalTime());
        dto.setDepartureTime(stopTime.getDepartureTime());
        dto.setStopSeq(stopTime.getStopSeq());
        
        return dto;
    }
    
    private StopTime convertToEntity(StopTimeDto dto) {
        if (dto == null) {
            return null;
        }
        
        StopTime entity = new StopTime();
        entity.setId(dto.getId());
        entity.setTrainTripId(dto.getTrainTripId());
        entity.setStopId(dto.getStopId());
        entity.setArrivalTime(dto.getArrivalTime());
        entity.setDepartureTime(dto.getDepartureTime());
        entity.setStopSeq(dto.getStopSeq());
        
        return entity;
    }
} 