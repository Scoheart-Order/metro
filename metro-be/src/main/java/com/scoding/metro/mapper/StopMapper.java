package com.scoding.metro.mapper;

import com.scoding.metro.entity.Stop;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StopMapper {
    
    List<Stop> getAllStops();
    
    Stop getStopById(Long id);
    
    List<Stop> getStopsByRouteId(Long routeId);
    
    List<Stop> getStopsByStationId(Long stationId);
    
    Stop getStopByRouteAndStation(Long routeId, Long stationId);
    
    List<Stop> getAllTransferStops();
    
    int insertStop(Stop stop);
    
    int updateStop(Stop stop);
    
    int deleteStop(Long id);
} 