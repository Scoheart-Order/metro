package com.scoding.metro.controller;

import com.scoding.metro.common.R;
import com.scoding.metro.dto.LineDto;
import com.scoding.metro.dto.RouteDto;
import com.scoding.metro.dto.StationDto;
import com.scoding.metro.dto.StopDto;
import com.scoding.metro.dto.TrainTripDto;
import com.scoding.metro.dto.StopTimeDto;
import com.scoding.metro.dto.UpdateStopSequencesRequest;
import com.scoding.metro.service.LineService;
import com.scoding.metro.service.RouteService;
import com.scoding.metro.service.StationService;
import com.scoding.metro.service.StopService;
import com.scoding.metro.service.TrainTripService;
import com.scoding.metro.service.StopTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地铁运营管理控制器
 * 处理线路、站点、路线、停靠点、列车行程和到站时刻等地铁运营相关信息
 */
@Tag(name = "地铁运营管理", description = "地铁运营相关接口")
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
    
    //********************* 线路相关接口 *********************//
    
    /**
     * 获取所有线路
     *
     * @return 线路列表
     */
    @Operation(summary = "获取所有线路", description = "获取系统中所有的地铁线路信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/lines")
    @PreAuthorize("permitAll()")
    public R<List<LineDto>> getAllLines() {
        return R.ok(lineService.getAllLines());
    }
    
    /**
     * 根据ID获取线路
     *
     * @param id 线路ID
     * @return 线路信息
     */
    @Operation(summary = "获取线路详情", description = "根据线路ID获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "线路不存在")
    })
    @GetMapping("/lines/{id}")
    @PreAuthorize("permitAll()")
    public R<LineDto> getLineById(@PathVariable Long id) {
        return R.ok(lineService.getLineById(id));
    }
    
    /**
     * 根据代码获取线路
     *
     * @param code 线路代码
     * @return 线路信息
     */
    @Operation(summary = "根据代码获取线路", description = "根据线路代码获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "线路不存在")
    })
    @GetMapping("/lines/code/{code}")
    @PreAuthorize("permitAll()")
    public R<LineDto> getLineByCode(@PathVariable String code) {
        return R.ok(lineService.getLineByCode(code));
    }
    
    /**
     * 获取线路及其路线信息
     *
     * @param id 线路ID
     * @return 包含路线信息的线路详情
     */
    @Operation(summary = "获取线路及路线", description = "获取线路及其包含的所有路线信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "线路不存在")
    })
    @GetMapping("/lines/{id}/routes")
    @PreAuthorize("permitAll()")
    public R<LineDto> getLineWithRoutes(@PathVariable Long id) {
        return R.ok(lineService.getLineWithRoutes(id));
    }
    
    /**
     * 创建线路
     *
     * @param lineDto 线路信息
     * @return 创建后的线路信息
     */
    @Operation(summary = "创建线路", description = "创建新的地铁线路（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/lines")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<LineDto> createLine(@RequestBody LineDto lineDto) {
        return R.ok(lineService.createLine(lineDto));
    }
    
    /**
     * 更新线路
     *
     * @param id 线路ID
     * @param lineDto 线路信息
     * @return 更新后的线路信息
     */
    @Operation(summary = "更新线路", description = "更新现有线路信息（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "线路不存在")
    })
    @PutMapping("/lines/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<LineDto> updateLine(@PathVariable Long id, @RequestBody LineDto lineDto) {
        return R.ok(lineService.updateLine(id, lineDto));
    }
    
    /**
     * 删除线路
     *
     * @param id 线路ID
     * @return 操作结果
     */
    @Operation(summary = "删除线路", description = "删除指定线路（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "线路不存在")
    })
    @DeleteMapping("/lines/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Boolean> deleteLine(@PathVariable Long id) {
        return R.ok(lineService.deleteLine(id));
    }
    
    //********************* 站点相关接口 *********************//
    
    /**
     * 获取所有站点
     *
     * @return 站点列表
     */
    @Operation(summary = "获取所有站点", description = "获取系统中所有的地铁站点信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/stations")
    @PreAuthorize("permitAll()")
    public R<List<StationDto>> getAllStations() {
        return R.ok(stationService.getAllStations());
    }
    
    /**
     * 根据ID获取站点
     *
     * @param id 站点ID
     * @return 站点信息
     */
    @Operation(summary = "获取站点详情", description = "根据站点ID获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "站点不存在")
    })
    @GetMapping("/stations/{id}")
    @PreAuthorize("permitAll()")
    public R<StationDto> getStationById(@PathVariable Long id) {
        return R.ok(stationService.getStationById(id));
    }
    
    /**
     * 根据代码获取站点
     *
     * @param code 站点代码
     * @return 站点信息
     */
    @Operation(summary = "根据代码获取站点", description = "根据站点代码获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "站点不存在")
    })
    @GetMapping("/stations/code/{code}")
    @PreAuthorize("permitAll()")
    public R<StationDto> getStationByCode(@PathVariable String code) {
        return R.ok(stationService.getStationByCode(code));
    }
    
    /**
     * 获取线路上的所有站点
     *
     * @param lineId 线路ID
     * @return 站点列表
     */
    @Operation(summary = "获取线路站点", description = "获取指定线路上的所有站点")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "线路不存在")
    })
    @GetMapping("/stations/line/{lineId}")
    @PreAuthorize("permitAll()")
    public R<List<StationDto>> getStationsByLineId(@PathVariable Long lineId) {
        return R.ok(stationService.getStationsByLineId(lineId));
    }
    
    /**
     * 获取路线上的所有站点
     *
     * @param routeId 路线ID
     * @return 站点列表
     */
    @Operation(summary = "获取路线站点", description = "获取指定路线上的所有站点")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "路线不存在")
    })
    @GetMapping("/stations/route/{routeId}")
    @PreAuthorize("permitAll()")
    public R<List<StationDto>> getStationsByRouteId(@PathVariable Long routeId) {
        return R.ok(stationService.getStationsByRouteId(routeId));
    }
    
    /**
     * 创建站点
     *
     * @param stationDto 站点信息
     * @return 创建后的站点信息
     */
    @Operation(summary = "创建站点", description = "创建新的地铁站点（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/stations")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<StationDto> createStation(@RequestBody StationDto stationDto) {
        return R.ok(stationService.createStation(stationDto));
    }
    
    /**
     * 更新站点
     *
     * @param id 站点ID
     * @param stationDto 站点信息
     * @return 更新后的站点信息
     */
    @Operation(summary = "更新站点", description = "更新现有站点信息（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "站点不存在")
    })
    @PutMapping("/stations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<StationDto> updateStation(@PathVariable Long id, @RequestBody StationDto stationDto) {
        return R.ok(stationService.updateStation(id, stationDto));
    }
    
    /**
     * 删除站点
     *
     * @param id 站点ID
     * @return 操作结果
     */
    @Operation(summary = "删除站点", description = "删除指定站点（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "站点不存在")
    })
    @DeleteMapping("/stations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Boolean> deleteStation(@PathVariable Long id) {
        return R.ok(stationService.deleteStation(id));
    }
    
    //********************* 路线相关接口 *********************//

    /**
     * 获取所有路线
     *
     * @return 路线列表
     */
    @Operation(summary = "获取所有路线", description = "获取系统中所有的地铁路线信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/routes")
    @PreAuthorize("permitAll()")
    public R<List<RouteDto>> getAllRoutes() {
        return R.ok(routeService.getAllRoutes());
    }
    
    /**
     * 根据ID获取路线
     *
     * @param id 路线ID
     * @return 路线信息
     */
    @Operation(summary = "获取路线详情", description = "根据路线ID获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "路线不存在")
    })
    @GetMapping("/routes/{id}")
    @PreAuthorize("permitAll()")
    public R<RouteDto> getRouteById(@PathVariable Long id) {
        return R.ok(routeService.getRouteById(id));
    }
    
    /**
     * 获取线路上的所有路线
     *
     * @param lineId 线路ID
     * @return 路线列表
     */
    @Operation(summary = "获取线路路线", description = "获取指定线路上的所有路线")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "线路不存在")
    })
    @GetMapping("/routes/line/{lineId}")
    @PreAuthorize("permitAll()")
    public R<List<RouteDto>> getRoutesByLineId(@PathVariable Long lineId) {
        return R.ok(routeService.getRoutesByLineId(lineId));
    }
    
    /**
     * 获取路线及其站点信息
     *
     * @param id 路线ID
     * @return 包含站点信息的路线详情
     */
    @Operation(summary = "获取路线及站点", description = "获取路线及其包含的所有站点信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "路线不存在")
    })
    @GetMapping("/routes/{id}/stations")
    @PreAuthorize("permitAll()")
    public R<RouteDto> getRouteWithStations(@PathVariable Long id) {
        return R.ok(routeService.getRouteWithStations(id));
    }
    
    /**
     * 查找两个站点之间的路线
     *
     * @param fromStationId 起始站点ID
     * @param toStationId 终点站点ID
     * @return 路线列表
     */
    @Operation(summary = "查找站点间路线", description = "查找两个站点之间的可行路线")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "站点不存在")
    })
    @GetMapping("/routes/path")
    @PreAuthorize("permitAll()")
    public R<List<RouteDto>> findRoutesBetweenStations(
            @RequestParam Long fromStationId, @RequestParam Long toStationId) {
        return R.ok(routeService.findRoutesBetweenStations(fromStationId, toStationId));
    }
    
    /**
     * 创建路线
     *
     * @param routeDto 路线信息
     * @return 创建后的路线信息
     */
    @Operation(summary = "创建路线", description = "创建新的地铁路线（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/routes")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<RouteDto> createRoute(@RequestBody RouteDto routeDto) {
        return R.ok(routeService.createRoute(routeDto));
    }
    
    /**
     * 更新路线
     *
     * @param id 路线ID
     * @param routeDto 路线信息
     * @return 更新后的路线信息
     */
    @Operation(summary = "更新路线", description = "更新现有路线信息（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "路线不存在")
    })
    @PutMapping("/routes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<RouteDto> updateRoute(@PathVariable Long id, @RequestBody RouteDto routeDto) {
        return R.ok(routeService.updateRoute(id, routeDto));
    }
    
    /**
     * 删除路线
     *
     * @param id 路线ID
     * @return 操作结果
     */
    @Operation(summary = "删除路线", description = "删除指定路线（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "路线不存在")
    })
    @DeleteMapping("/routes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Boolean> deleteRoute(@PathVariable Long id) {
        return R.ok(routeService.deleteRoute(id));
    }
    
    //********************* 列车行程相关接口 *********************//

    /**
     * 获取所有列车行程
     *
     * @return 列车行程列表
     */
    @Operation(summary = "获取所有列车行程", description = "获取系统中所有的列车行程信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/train-trips")
    @PreAuthorize("permitAll()")
    public R<List<TrainTripDto>> getAllTrainTrips() {
        return R.ok(trainTripService.getAllTrainTrips());
    }
    
    /**
     * 根据ID获取列车行程
     *
     * @param id 列车行程ID
     * @return 列车行程信息
     */
    @Operation(summary = "获取列车行程详情", description = "根据列车行程ID获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "列车行程不存在")
    })
    @GetMapping("/train-trips/{id}")
    @PreAuthorize("permitAll()")
    public R<TrainTripDto> getTrainTripById(@PathVariable Long id) {
        return R.ok(trainTripService.getTrainTripById(id));
    }
    
    /**
     * 获取路线上的所有列车行程
     *
     * @param routeId 路线ID
     * @return 列车行程列表
     */
    @Operation(summary = "获取路线列车行程", description = "获取指定路线上的所有列车行程")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "路线不存在")
    })
    @GetMapping("/train-trips/route/{routeId}")
    @PreAuthorize("permitAll()")
    public R<List<TrainTripDto>> getTrainTripsByRouteId(@PathVariable Long routeId) {
        return R.ok(trainTripService.getTrainTripsByRouteId(routeId));
    }
    
    /**
     * 获取列车行程及其到站时刻信息
     *
     * @param id 列车行程ID
     * @return 包含到站时刻信息的列车行程详情
     */
    @Operation(summary = "获取列车行程及到站时刻", description = "获取列车行程及其包含的所有到站时刻信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "列车行程不存在")
    })
    @GetMapping("/train-trips/{id}/stop-times")
    @PreAuthorize("permitAll()")
    public R<TrainTripDto> getTrainTripWithStopTimes(@PathVariable Long id) {
        return R.ok(trainTripService.getTrainTripWithStopTimes(id));
    }
    
    /**
     * 创建列车行程
     *
     * @param trainTripDto 列车行程信息
     * @return 创建后的列车行程信息
     */
    @Operation(summary = "创建列车行程", description = "创建新的列车行程（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/train-trips")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<TrainTripDto> createTrainTrip(@RequestBody TrainTripDto trainTripDto) {
        return R.ok(trainTripService.createTrainTrip(trainTripDto));
    }
    
    /**
     * 更新列车行程
     *
     * @param id 列车行程ID
     * @param trainTripDto 列车行程信息
     * @return 更新后的列车行程信息
     */
    @Operation(summary = "更新列车行程", description = "更新现有列车行程信息（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "列车行程不存在")
    })
    @PutMapping("/train-trips/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<TrainTripDto> updateTrainTrip(@PathVariable Long id, @RequestBody TrainTripDto trainTripDto) {
        return R.ok(trainTripService.updateTrainTrip(id, trainTripDto));
    }
    
    /**
     * 删除列车行程
     *
     * @param id 列车行程ID
     * @return 操作结果
     */
    @Operation(summary = "删除列车行程", description = "删除指定列车行程（需要管理员权限）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "列车行程不存在")
    })
    @DeleteMapping("/train-trips/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Boolean> deleteTrainTrip(@PathVariable Long id) {
        return R.ok(trainTripService.deleteTrainTrip(id));
    }
    
    //********************* 其他接口 *********************//
    
    // 停靠点接口
    @GetMapping("/stops")
    @PreAuthorize("permitAll()")
    public R<List<StopDto>> getAllStops() {
        return R.ok(stopService.getAllStops());
    }
    
    @GetMapping("/stops/{id}")
    @PreAuthorize("permitAll()")
    public R<StopDto> getStopById(@PathVariable Long id) {
        return R.ok(stopService.getStopById(id));
    }
    
    @GetMapping("/stops/route/{routeId}")
    @PreAuthorize("permitAll()")
    public R<List<StopDto>> getStopsByRouteId(@PathVariable Long routeId) {
        return R.ok(stopService.getStopsByRouteId(routeId));
    }
    
    @PostMapping("/stops")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<StopDto> createStop(@RequestBody StopDto stopDto) {
        return R.ok(stopService.createStop(stopDto));
    }
    
    @PutMapping("/stops/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<StopDto> updateStop(@PathVariable Long id, @RequestBody StopDto stopDto) {
        return R.ok(stopService.updateStop(id, stopDto));
    }
    
    @DeleteMapping("/stops/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Boolean> deleteStop(@PathVariable Long id) {
        return R.ok(stopService.deleteStop(id));
    }
    
    /**
     * 批量更新经停站序号
     *
     * @param request 包含路线ID和经停站序号的请求
     * @return 操作结果
     */
    @PutMapping("/stops/sequences")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Boolean> updateStopSequences(@RequestBody UpdateStopSequencesRequest request) {
        return R.ok(stopService.updateStopSequences(request));
    }
    
    // 到站时刻接口
    @GetMapping("/stop-times")
    @PreAuthorize("permitAll()")
    public R<List<StopTimeDto>> getAllStopTimes() {
        return R.ok(stopTimeService.getAllStopTimes());
    }
    
    @GetMapping("/stop-times/{id}")
    @PreAuthorize("permitAll()")
    public R<StopTimeDto> getStopTimeById(@PathVariable Long id) {
        return R.ok(stopTimeService.getStopTimeById(id));
    }
    
    @GetMapping("/stop-times/train-trip/{trainTripId}")
    @PreAuthorize("permitAll()")
    public R<List<StopTimeDto>> getStopTimesByTrainTripId(@PathVariable Long trainTripId) {
        return R.ok(stopTimeService.getStopTimesByTrainTripId(trainTripId));
    }
    
    @GetMapping("/stop-times/stop/{stopId}")
    @PreAuthorize("permitAll()")
    public R<List<StopTimeDto>> getStopTimesByStopId(@PathVariable Long stopId) {
        return R.ok(stopTimeService.getStopTimesByStopId(stopId));
    }
    
    @PostMapping("/stop-times")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<StopTimeDto> createStopTime(@RequestBody StopTimeDto stopTimeDto) {
        return R.ok(stopTimeService.createStopTime(stopTimeDto));
    }
    
    @PutMapping("/stop-times/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<StopTimeDto> updateStopTime(@PathVariable Long id, @RequestBody StopTimeDto stopTimeDto) {
        return R.ok(stopTimeService.updateStopTime(id, stopTimeDto));
    }
    
    @DeleteMapping("/stop-times/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public R<Boolean> deleteStopTime(@PathVariable Long id) {
        return R.ok(stopTimeService.deleteStopTime(id));
    }
} 