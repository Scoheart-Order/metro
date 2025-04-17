package com.scoding.metro.mapper;

import com.scoding.metro.entity.Route;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RouteMapper {
    
    List<Route> getAllRoutes();
    
    Route getRouteById(Long id);
    
    List<Route> getRoutesByLineId(Long lineId);
    
    Route getRouteByLineIdAndName(Long lineId, String name);
    
    int insertRoute(Route route);
    
    int updateRoute(Route route);
    
    int deleteRoute(Long id);
    
    List<Route> findRoutesConnectingStations(Long stationId1, Long stationId2);
} 