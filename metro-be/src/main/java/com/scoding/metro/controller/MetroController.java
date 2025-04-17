package com.scoding.metro.controller;

import com.scoding.metro.common.R;
import com.scoding.metro.dto.LineDto;
import com.scoding.metro.dto.RouteDto;
import com.scoding.metro.dto.StationDto;
import com.scoding.metro.service.LineService;
import com.scoding.metro.service.RouteService;
import com.scoding.metro.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metro")
@RequiredArgsConstructor
public class MetroController {
    
    private final LineService lineService;
    private final StationService stationService;
    private final RouteService routeService;
    
    // 线路相关接口
    @GetMapping("/lines")
    public R<List<LineDto>> getAllLines() {
        return R.ok(lineService.getAllLines());
    }
    
    @GetMapping("/lines/{id}")
    public R<LineDto> getLineById(@PathVariable Long id) {
        return R.ok(lineService.getLineById(id));
    }
    
    @GetMapping("/lines/code/{code}")
    public R<LineDto> getLineByCode(@PathVariable String code) {
        return R.ok(lineService.getLineByCode(code));
    }
    
    @GetMapping("/lines/{id}/routes")
    public R<LineDto> getLineWithRoutes(@PathVariable Long id) {
        return R.ok(lineService.getLineWithRoutes(id));
    }
    
    @PostMapping("/lines")
    public R<LineDto> createLine(@RequestBody LineDto lineDto) {
        return R.ok(lineService.createLine(lineDto));
    }
    
    @PutMapping("/lines/{id}")
    public R<LineDto> updateLine(@PathVariable Long id, @RequestBody LineDto lineDto) {
        return R.ok(lineService.updateLine(id, lineDto));
    }
    
    @DeleteMapping("/lines/{id}")
    public R<Boolean> deleteLine(@PathVariable Long id) {
        return R.ok(lineService.deleteLine(id));
    }
    
    // 站点相关接口
    @GetMapping("/stations")
    public R<List<StationDto>> getAllStations() {
        return R.ok(stationService.getAllStations());
    }
    
    @GetMapping("/stations/{id}")
    public R<StationDto> getStationById(@PathVariable Long id) {
        return R.ok(stationService.getStationById(id));
    }
    
    @GetMapping("/stations/code/{code}")
    public R<StationDto> getStationByCode(@PathVariable String code) {
        return R.ok(stationService.getStationByCode(code));
    }
    
    @GetMapping("/stations/line/{lineId}")
    public R<List<StationDto>> getStationsByLineId(@PathVariable Long lineId) {
        return R.ok(stationService.getStationsByLineId(lineId));
    }
    
    @GetMapping("/stations/route/{routeId}")
    public R<List<StationDto>> getStationsByRouteId(@PathVariable Long routeId) {
        return R.ok(stationService.getStationsByRouteId(routeId));
    }
    
    @GetMapping("/stations/transfer")
    public R<List<StationDto>> getAllTransferStations() {
        return R.ok(stationService.getAllTransferStations());
    }
    
    @PostMapping("/stations")
    public R<StationDto> createStation(@RequestBody StationDto stationDto) {
        return R.ok(stationService.createStation(stationDto));
    }
    
    @PutMapping("/stations/{id}")
    public R<StationDto> updateStation(@PathVariable Long id, @RequestBody StationDto stationDto) {
        return R.ok(stationService.updateStation(id, stationDto));
    }
    
    @DeleteMapping("/stations/{id}")
    public R<Boolean> deleteStation(@PathVariable Long id) {
        return R.ok(stationService.deleteStation(id));
    }
    
    // 路线相关接口
    @GetMapping("/routes")
    public R<List<RouteDto>> getAllRoutes() {
        return R.ok(routeService.getAllRoutes());
    }
    
    @GetMapping("/routes/{id}")
    public R<RouteDto> getRouteById(@PathVariable Long id) {
        return R.ok(routeService.getRouteById(id));
    }
    
    @GetMapping("/routes/line/{lineId}")
    public R<List<RouteDto>> getRoutesByLineId(@PathVariable Long lineId) {
        return R.ok(routeService.getRoutesByLineId(lineId));
    }
    
    @GetMapping("/routes/{id}/stations")
    public R<RouteDto> getRouteWithStations(@PathVariable Long id) {
        return R.ok(routeService.getRouteWithStations(id));
    }
    
    @GetMapping("/routes/path")
    public R<List<RouteDto>> findRoutesBetweenStations(
            @RequestParam Long fromStationId, @RequestParam Long toStationId) {
        return R.ok(routeService.findRoutesBetweenStations(fromStationId, toStationId));
    }
    
    @PostMapping("/routes")
    public R<RouteDto> createRoute(@RequestBody RouteDto routeDto) {
        return R.ok(routeService.createRoute(routeDto));
    }
    
    @PutMapping("/routes/{id}")
    public R<RouteDto> updateRoute(@PathVariable Long id, @RequestBody RouteDto routeDto) {
        return R.ok(routeService.updateRoute(id, routeDto));
    }
    
    @DeleteMapping("/routes/{id}")
    public R<Boolean> deleteRoute(@PathVariable Long id) {
        return R.ok(routeService.deleteRoute(id));
    }
} 