package com.scoding.metro.service.impl;

import com.scoding.metro.dto.TrainTripDto;
import com.scoding.metro.entity.TrainTrip;
import com.scoding.metro.mapper.TrainTripMapper;
import com.scoding.metro.service.StopTimeService;
import com.scoding.metro.service.TrainTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainTripServiceImpl implements TrainTripService {
    
    private final TrainTripMapper trainTripMapper;
    private final StopTimeService stopTimeService;
    
    @Override
    public List<TrainTripDto> getAllTrainTrips() {
        return trainTripMapper.selectAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public TrainTripDto getTrainTripById(Long id) {
        TrainTrip trainTrip = trainTripMapper.selectById(id);
        return convertToDto(trainTrip);
    }
    
    @Override
    public List<TrainTripDto> getTrainTripsByRouteId(Long routeId) {
        return trainTripMapper.selectByRouteId(routeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public TrainTripDto getTrainTripWithStopTimes(Long id) {
        TrainTrip trainTrip = trainTripMapper.selectById(id);
        TrainTripDto trainTripDto = convertToDto(trainTrip);
        
        // Add stop times
        trainTripDto.setStopTimeIds(
                stopTimeService.getStopTimesByTrainTripId(id).stream()
                        .map(dto -> dto.getId())
                        .collect(Collectors.toList())
        );
        
        return trainTripDto;
    }
    
    @Override
    @Transactional
    public TrainTripDto createTrainTrip(TrainTripDto trainTripDto) {
        TrainTrip trainTrip = convertToEntity(trainTripDto);
        trainTripMapper.insert(trainTrip);
        
        return convertToDto(trainTrip);
    }
    
    @Override
    @Transactional
    public TrainTripDto updateTrainTrip(Long id, TrainTripDto trainTripDto) {
        TrainTrip existing = trainTripMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("Train trip not found: " + id);
        }
        
        TrainTrip trainTrip = convertToEntity(trainTripDto);
        trainTrip.setId(id);
        trainTripMapper.update(trainTrip);
        
        return convertToDto(trainTrip);
    }
    
    @Override
    @Transactional
    public Boolean deleteTrainTrip(Long id) {
        return trainTripMapper.delete(id) > 0;
    }
    
    // Helper methods
    private TrainTripDto convertToDto(TrainTrip trainTrip) {
        if (trainTrip == null) {
            return null;
        }
        
        TrainTripDto dto = new TrainTripDto();
        dto.setId(trainTrip.getId());
        dto.setRouteId(trainTrip.getRouteId());
        dto.setTrainNumber(trainTrip.getTrainNumber());
        dto.setRunDate(trainTrip.getRunDate());
        
        return dto;
    }
    
    private TrainTrip convertToEntity(TrainTripDto dto) {
        if (dto == null) {
            return null;
        }
        
        TrainTrip entity = new TrainTrip();
        entity.setId(dto.getId());
        entity.setRouteId(dto.getRouteId());
        entity.setTrainNumber(dto.getTrainNumber());
        entity.setRunDate(dto.getRunDate());
        
        return entity;
    }
} 