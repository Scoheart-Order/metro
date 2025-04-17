package com.scoding.metro.service;

import com.scoding.metro.dto.StopDto;

import java.util.List;

public interface StopService {
    
    List<StopDto> getAllStops();
    
    StopDto getStopById(Long id);
    
    List<StopDto> getStopsByRouteId(Long routeId);
    
    List<StopDto> getStopsByStationId(Long stationId);
    
    StopDto createStop(StopDto stopDto);
    
    StopDto updateStop(Long id, StopDto stopDto);
    
    boolean deleteStop(Long id);
    
    StopDto getStopByRouteAndStation(Long routeId, Long stationId);
} 