package com.scoding.metro.mapper;

import com.scoding.metro.entity.Station;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StationMapper {
    
    List<Station> getAllStations();
    
    Station getStationById(Long id);
    
    Station getStationByCode(String code);
    
    List<Station> getStationsByLineId(Long lineId);
    
    List<Station> getStationsByRouteId(Long routeId);
    
    int insertStation(Station station);
    
    int updateStation(Station station);
    
    int deleteStation(Long id);
} 