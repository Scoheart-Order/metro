package com.scoding.metro.controller;

import com.scoding.metro.common.R;
import com.scoding.metro.dto.LineDto;
import com.scoding.metro.dto.RouteDto;
import com.scoding.metro.dto.StationDto;
import com.scoding.metro.dto.StopDto;
import com.scoding.metro.dto.TrainTripDto;
import com.scoding.metro.dto.StopTimeDto;
import com.scoding.metro.service.LineService;
import com.scoding.metro.service.RouteService;
import com.scoding.metro.service.StationService;
import com.scoding.metro.service.StopService;
import com.scoding.metro.service.TrainTripService;
import com.scoding.metro.service.StopTimeService;
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
    private final StopService stopService;
    private final TrainTripService trainTripService;
    private final StopTimeService stopTimeService;
    
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
    
    // 停靠点相关接口
    @GetMapping("/stops")
    public R<List<StopDto>> getAllStops() {
        return R.ok(stopService.getAllStops());
    }
    
    @GetMapping("/stops/{id}")
    public R<StopDto> getStopById(@PathVariable Long id) {
        return R.ok(stopService.getStopById(id));
    }
    
    @GetMapping("/stops/route/{routeId}")
    public R<List<StopDto>> getStopsByRouteId(@PathVariable Long routeId) {
        return R.ok(stopService.getStopsByRouteId(routeId));
    }
    
    @PostMapping("/stops")
    public R<StopDto> createStop(@RequestBody StopDto stopDto) {
        return R.ok(stopService.createStop(stopDto));
    }
    
    @PutMapping("/stops/{id}")
    public R<StopDto> updateStop(@PathVariable Long id, @RequestBody StopDto stopDto) {
        return R.ok(stopService.updateStop(id, stopDto));
    }
    
    @DeleteMapping("/stops/{id}")
    public R<Boolean> deleteStop(@PathVariable Long id) {
        return R.ok(stopService.deleteStop(id));
    }
    
    // 列车行程相关接口
    @GetMapping("/train-trips")
    public R<List<TrainTripDto>> getAllTrainTrips() {
        return R.ok(trainTripService.getAllTrainTrips());
    }
    
    @GetMapping("/train-trips/{id}")
    public R<TrainTripDto> getTrainTripById(@PathVariable Long id) {
        return R.ok(trainTripService.getTrainTripById(id));
    }
    
    @GetMapping("/train-trips/route/{routeId}")
    public R<List<TrainTripDto>> getTrainTripsByRouteId(@PathVariable Long routeId) {
        return R.ok(trainTripService.getTrainTripsByRouteId(routeId));
    }
    
    @GetMapping("/train-trips/{id}/stop-times")
    public R<TrainTripDto> getTrainTripWithStopTimes(@PathVariable Long id) {
        return R.ok(trainTripService.getTrainTripWithStopTimes(id));
    }
    
    @PostMapping("/train-trips")
    public R<TrainTripDto> createTrainTrip(@RequestBody TrainTripDto trainTripDto) {
        return R.ok(trainTripService.createTrainTrip(trainTripDto));
    }
    
    @PutMapping("/train-trips/{id}")
    public R<TrainTripDto> updateTrainTrip(@PathVariable Long id, @RequestBody TrainTripDto trainTripDto) {
        return R.ok(trainTripService.updateTrainTrip(id, trainTripDto));
    }
    
    @DeleteMapping("/train-trips/{id}")
    public R<Boolean> deleteTrainTrip(@PathVariable Long id) {
        return R.ok(trainTripService.deleteTrainTrip(id));
    }
    
    // 到站时刻相关接口
    @GetMapping("/stop-times")
    public R<List<StopTimeDto>> getAllStopTimes() {
        return R.ok(stopTimeService.getAllStopTimes());
    }
    
    @GetMapping("/stop-times/{id}")
    public R<StopTimeDto> getStopTimeById(@PathVariable Long id) {
        return R.ok(stopTimeService.getStopTimeById(id));
    }
    
    @GetMapping("/stop-times/train-trip/{trainTripId}")
    public R<List<StopTimeDto>> getStopTimesByTrainTripId(@PathVariable Long trainTripId) {
        return R.ok(stopTimeService.getStopTimesByTrainTripId(trainTripId));
    }
    
    @GetMapping("/stop-times/stop/{stopId}")
    public R<List<StopTimeDto>> getStopTimesByStopId(@PathVariable Long stopId) {
        return R.ok(stopTimeService.getStopTimesByStopId(stopId));
    }
    
    @PostMapping("/stop-times")
    public R<StopTimeDto> createStopTime(@RequestBody StopTimeDto stopTimeDto) {
        return R.ok(stopTimeService.createStopTime(stopTimeDto));
    }
    
    @PutMapping("/stop-times/{id}")
    public R<StopTimeDto> updateStopTime(@PathVariable Long id, @RequestBody StopTimeDto stopTimeDto) {
        return R.ok(stopTimeService.updateStopTime(id, stopTimeDto));
    }
    
    @DeleteMapping("/stop-times/{id}")
    public R<Boolean> deleteStopTime(@PathVariable Long id) {
        return R.ok(stopTimeService.deleteStopTime(id));
    }
} 