package com.scoding.metro.service;

import com.scoding.metro.dto.TrainTripDto;

import java.util.List;

public interface TrainTripService {
    List<TrainTripDto> getAllTrainTrips();
    
    TrainTripDto getTrainTripById(Long id);
    
    List<TrainTripDto> getTrainTripsByRouteId(Long routeId);
    
    TrainTripDto getTrainTripWithStopTimes(Long id);
    
    TrainTripDto createTrainTrip(TrainTripDto trainTripDto);
    
    TrainTripDto updateTrainTrip(Long id, TrainTripDto trainTripDto);
    
    Boolean deleteTrainTrip(Long id);
} 