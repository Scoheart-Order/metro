package com.scoding.metro.service.impl;

import com.scoding.metro.dto.RouteDto;
import com.scoding.metro.dto.StationDto;
import com.scoding.metro.entity.Line;
import com.scoding.metro.entity.Route;
import com.scoding.metro.entity.Station;
import com.scoding.metro.entity.Stop;
import com.scoding.metro.exception.BusinessException;
import com.scoding.metro.mapper.LineMapper;
import com.scoding.metro.mapper.RouteMapper;
import com.scoding.metro.mapper.StationMapper;
import com.scoding.metro.mapper.StopMapper;
import com.scoding.metro.service.RouteService;
import com.scoding.metro.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteMapper routeMapper;
    private final LineMapper lineMapper;
    private final StationMapper stationMapper;
    private final StopMapper stopMapper;
    private final StationService stationService;

    @Override
    public List<RouteDto> getAllRoutes() {
        // 获取所有路线
        List<Route> routes = routeMapper.getAllRoutes();

        // 为每个路线填充关联数据并转换为DTO
        return routes.stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    @Override
    public RouteDto getRouteById(Long id) {
        Route route = routeMapper.getRouteById(id);
        if (route == null) {
            throw new BusinessException("路线不存在");
        }
        return convertToBasicDto(route);
    }

    @Override
    public List<RouteDto> getRoutesByLineId(Long lineId) {
        List<Route> routes = routeMapper.getRoutesByLineId(lineId);
        return routes.stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    @Override
    public RouteDto getRouteWithStations(Long id) {
        Route route = routeMapper.getRouteById(id);
        if (route == null) {
            throw new BusinessException("路线不存在");
        }

        // 创建DTO并填充基本信息
        RouteDto routeDto = convertToBasicDto(route);

        // 获取沿线站点信息
        List<Stop> stops = stopMapper.getStopsByRouteId(id);

        // 获取站点详情并添加到DTO
        routeDto.setStations(stops.stream()
                .map(stop -> {
                    Station station = stationMapper.getStationById(stop.getStationId());
                    StationDto stationDto = new StationDto();
                    stationDto.setId(station.getId());
                    stationDto.setName(station.getName());
                    stationDto.setCode(station.getCode());
                    stationDto.setIsTransfer(stop.getIsTransfer());
                    return stationDto;
                })
                .collect(Collectors.toList()));

        return routeDto;
    }

    @Override
    @Transactional
    public RouteDto createRoute(RouteDto routeDto) {
        // 验证线路是否存在
        Line line = lineMapper.getLineById(routeDto.getLineId());
        if (line == null) {
            throw new BusinessException("线路不存在");
        }

        // 验证起始站点是否存在
        if (routeDto.getStartStationId() != null) {
            Station startStation = stationMapper.getStationById(routeDto.getStartStationId());
            if (startStation == null) {
                throw new BusinessException("起始站点不存在");
            }
        }

        // 验证终点站点是否存在
        if (routeDto.getEndStationId() != null) {
            Station endStation = stationMapper.getStationById(routeDto.getEndStationId());
            if (endStation == null) {
                throw new BusinessException("终点站点不存在");
            }
        }

        // 检查同一线路下是否已存在同名路线
        Route existingRoute = routeMapper.getRouteByLineIdAndName(routeDto.getLineId(), routeDto.getName());
        if (existingRoute != null) {
            throw new BusinessException("该线路下已存在同名路线");
        }

        Route route = new Route();
        route.setLineId(routeDto.getLineId());
        route.setName(routeDto.getName());
        route.setStartStationId(routeDto.getStartStationId());
        route.setEndStationId(routeDto.getEndStationId());

        routeMapper.insertRoute(route);

        return convertToBasicDto(route);
    }

    @Override
    @Transactional
    public RouteDto updateRoute(Long id, RouteDto routeDto) {
        Route existingRoute = routeMapper.getRouteById(id);
        if (existingRoute == null) {
            throw new BusinessException("路线不存在");
        }

        // 验证线路是否存在
        Line line = lineMapper.getLineById(routeDto.getLineId());
        if (line == null) {
            throw new BusinessException("线路不存在");
        }

        // 验证起始站点是否存在
        if (routeDto.getStartStationId() != null) {
            Station startStation = stationMapper.getStationById(routeDto.getStartStationId());
            if (startStation == null) {
                throw new BusinessException("起始站点不存在");
            }
        }

        // 验证终点站点是否存在
        if (routeDto.getEndStationId() != null) {
            Station endStation = stationMapper.getStationById(routeDto.getEndStationId());
            if (endStation == null) {
                throw new BusinessException("终点站点不存在");
            }
        }

        // 如果路线名称已更改，检查是否与同一线路下的其他路线重名
        if (!existingRoute.getName().equals(routeDto.getName())) {
            Route routeWithSameName = routeMapper.getRouteByLineIdAndName(routeDto.getLineId(), routeDto.getName());
            if (routeWithSameName != null) {
                throw new BusinessException("该线路下已存在同名路线");
            }
        }

        Route route = new Route();
        route.setId(id);
        route.setLineId(routeDto.getLineId());
        route.setName(routeDto.getName());
        route.setStartStationId(routeDto.getStartStationId());
        route.setEndStationId(routeDto.getEndStationId());

        routeMapper.updateRoute(route);

        return convertToBasicDto(route);
    }

    @Override
    @Transactional
    public boolean deleteRoute(Long id) {
        Route route = routeMapper.getRouteById(id);
        if (route == null) {
            throw new BusinessException("路线不存在");
        }

        // 检查是否有关联的站点停靠信息
        List<Stop> stops = stopMapper.getStopsByRouteId(id);
        if (!stops.isEmpty()) {
            throw new BusinessException("该路线有关联的站点停靠信息，请先删除这些信息");
        }

        return routeMapper.deleteRoute(id) > 0;
    }

    @Override
    public List<RouteDto> findRoutesBetweenStations(Long stationId1, Long stationId2) {
        // 验证站点是否存在
        Station station1 = stationMapper.getStationById(stationId1);
        Station station2 = stationMapper.getStationById(stationId2);

        if (station1 == null || station2 == null) {
            throw new BusinessException("站点不存在");
        }

        List<Route> routes = routeMapper.findRoutesConnectingStations(stationId1, stationId2);
        return routes.stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    private RouteDto convertToBasicDto(Route route) {
        if (route == null) {
            return null;
        }

        RouteDto dto = new RouteDto();
        dto.setId(route.getId());
        dto.setName(route.getName());

        // 设置线路信息
        if (route.getLineId() != null) {
            dto.setLineId(route.getLineId());
            Line line = lineMapper.getLineById(route.getLineId());
            if (line != null) {
                dto.setLineName(line.getName());
            }
        }

        // 设置起始站点信息
        if (route.getStartStationId() != null) {
            dto.setStartStationId(route.getStartStationId());
            Station startStation = stationMapper.getStationById(route.getStartStationId());
            if (startStation != null) {
                dto.setStartStationName(startStation.getName());
            }
        }

        // 设置终点站信息
        if (route.getEndStationId() != null) {
            dto.setEndStationId(route.getEndStationId());
            Station endStation = stationMapper.getStationById(route.getEndStationId());
            if (endStation != null) {
                dto.setEndStationName(endStation.getName());
            }
        }

        return dto;
    }
}