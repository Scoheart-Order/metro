package com.scoding.metro.service.impl;

import com.scoding.metro.dto.StationDto;
import com.scoding.metro.entity.Station;
import com.scoding.metro.entity.Stop;
import com.scoding.metro.exception.BusinessException;
import com.scoding.metro.mapper.StationMapper;
import com.scoding.metro.mapper.StopMapper;
import com.scoding.metro.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {
    
    private final StationMapper stationMapper;
    private final StopMapper stopMapper;
    
    @Override
    public List<StationDto> getAllStations() {
        return stationMapper.getAllStations().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public StationDto getStationById(Long id) {
        Station station = stationMapper.getStationById(id);
        if (station == null) {
            throw new BusinessException("站点不存在");
        }
        return convertToDto(station);
    }
    
    @Override
    public StationDto getStationByCode(String code) {
        Station station = stationMapper.getStationByCode(code);
        if (station == null) {
            throw new BusinessException("站点不存在");
        }
        return convertToDto(station);
    }
    
    @Override
    public List<StationDto> getStationsByLineId(Long lineId) {
        List<Station> stations = stationMapper.getStationsByLineId(lineId);
        return stations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<StationDto> getStationsByRouteId(Long routeId) {
        List<Station> stations = stationMapper.getStationsByRouteId(routeId);
        return stations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public StationDto createStation(StationDto stationDto) {
        Station station = convertToEntity(stationDto);
        
        // 检查站点编码是否已存在
        if (station.getCode() != null) {
            Station existingStation = stationMapper.getStationByCode(station.getCode());
            if (existingStation != null) {
                throw new BusinessException("站点编码已存在");
            }
        }
        
        stationMapper.insertStation(station);
        return convertToDto(station);
    }
    
    @Override
    @Transactional
    public StationDto updateStation(Long id, StationDto stationDto) {
        Station existingStation = stationMapper.getStationById(id);
        if (existingStation == null) {
            throw new BusinessException("站点不存在");
        }
        
        // 如果编码已更改，检查新编码是否已被使用
        if (stationDto.getCode() != null && (existingStation.getCode() == null || 
                !existingStation.getCode().equals(stationDto.getCode()))) {
            Station stationWithCode = stationMapper.getStationByCode(stationDto.getCode());
            if (stationWithCode != null) {
                throw new BusinessException("站点编码已存在");
            }
        }
        
        Station station = convertToEntity(stationDto);
        station.setId(id);
        stationMapper.updateStation(station);
        
        return convertToDto(station);
    }
    
    @Override
    @Transactional
    public boolean deleteStation(Long id) {
        Station station = stationMapper.getStationById(id);
        if (station == null) {
            throw new BusinessException("站点不存在");
        }
        
        // 检查是否有关联的站点停靠信息
        List<Stop> stops = stopMapper.getStopsByStationId(id);
        if (!stops.isEmpty()) {
            throw new BusinessException("该站点有关联的线路停靠信息，无法删除");
        }
        
        return stationMapper.deleteStation(id) > 0;
    }
    
    @Override
    public List<StationDto> getAllTransferStations() {
        List<Stop> transferStops = stopMapper.getAllTransferStops();
        return transferStops.stream()
                .map(stop -> stationMapper.getStationById(stop.getStationId()))
                .distinct()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private StationDto convertToDto(Station station) {
        StationDto dto = new StationDto();
        dto.setId(station.getId());
        dto.setName(station.getName());
        dto.setCode(station.getCode());
        
        // 确定是否为换乘站
        List<Stop> stops = stopMapper.getStopsByStationId(station.getId());
        dto.setIsTransfer(stops.stream().anyMatch(Stop::getIsTransfer));
        
        return dto;
    }
    
    private Station convertToEntity(StationDto dto) {
        Station station = new Station();
        station.setId(dto.getId());
        station.setName(dto.getName());
        station.setCode(dto.getCode());
        return station;
    }
} 