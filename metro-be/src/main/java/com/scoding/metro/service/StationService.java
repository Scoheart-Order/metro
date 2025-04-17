package com.scoding.metro.service;

import com.scoding.metro.dto.StationDto;

import java.util.List;

public interface StationService {
    
    List<StationDto> getAllStations();
    
    StationDto getStationById(Long id);
    
    StationDto getStationByCode(String code);
    
    List<StationDto> getStationsByLineId(Long lineId);
    
    List<StationDto> getStationsByRouteId(Long routeId);
    
    StationDto createStation(StationDto stationDto);
    
    StationDto updateStation(Long id, StationDto stationDto);
    
    boolean deleteStation(Long id);
    
    List<StationDto> getAllTransferStations();
} 