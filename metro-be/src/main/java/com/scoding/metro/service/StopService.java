package com.scoding.metro.service;

import com.scoding.metro.dto.StopDto;
import com.scoding.metro.dto.UpdateStopSequencesRequest;

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
    
    /**
     * 批量更新经停站序号
     * 
     * @param request 包含路线ID和经停站序号的请求
     * @return 操作结果
     */
    boolean updateStopSequences(UpdateStopSequencesRequest request);
} 