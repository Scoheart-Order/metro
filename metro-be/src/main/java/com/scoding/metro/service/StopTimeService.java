package com.scoding.metro.service;

import com.scoding.metro.dto.StopTimeDto;

import java.util.List;

public interface StopTimeService {
    List<StopTimeDto> getAllStopTimes();
    
    StopTimeDto getStopTimeById(Long id);
    
    List<StopTimeDto> getStopTimesByTrainTripId(Long trainTripId);
    
    List<StopTimeDto> getStopTimesByStopId(Long stopId);
    
    StopTimeDto createStopTime(StopTimeDto stopTimeDto);
    
    StopTimeDto updateStopTime(Long id, StopTimeDto stopTimeDto);
    
    Boolean deleteStopTime(Long id);
} 