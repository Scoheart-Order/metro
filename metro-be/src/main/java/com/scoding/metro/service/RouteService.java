package com.scoding.metro.service;

import com.scoding.metro.dto.RouteDto;

import java.util.List;

public interface RouteService {
    
    List<RouteDto> getAllRoutes();
    
    RouteDto getRouteById(Long id);
    
    List<RouteDto> getRoutesByLineId(Long lineId);
    
    RouteDto getRouteWithStations(Long id);
    
    RouteDto createRoute(RouteDto routeDto);
    
    RouteDto updateRoute(Long id, RouteDto routeDto);
    
    boolean deleteRoute(Long id);
    
    List<RouteDto> findRoutesBetweenStations(Long stationId1, Long stationId2);
} 