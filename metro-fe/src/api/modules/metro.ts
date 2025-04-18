import { request } from '../request'

// Line interfaces
export interface LineDto {
  id?: number | null
  name: string
  code: string
  color: string
  operator: string
}

export interface Line extends LineDto {
  id: number
  routes?: Route[]
}

// Station interfaces
export interface StationDto {
  id?: number | null
  name: string
  code: string
  address: string
  isTransferStation: boolean
  lineIds?: number[]
}

export interface Station extends StationDto {
  id: number
  lines?: Line[]
}

// Route interfaces
export interface RouteDto {
  id?: number | null
  name: string
  lineId: number
  startStationId: number
  endStationId: number
  distance: number
  estimatedTime: number
  stationIds?: number[]
}

export interface Route extends RouteDto {
  id: number
  line?: Line
  stations?: Station[]
  startStation?: Station
  endStation?: Station
}

// Stop interfaces
export interface StopDto {
  id?: number | null
  routeId: number
  stationId: number
  seq: number
  arrivalTime?: string | null
  departureTime?: string | null
}

export interface Stop extends StopDto {
  id: number
  routeName?: string
  stationName?: string
}

/**
 * Metro API methods
 */
export const metroApi = {
  // Line methods
  getAllLines: () => {
    return request.get<Line[]>('/metro/lines')
  },

  getLineById: (id: number) => {
    return request.get<Line>(`/metro/lines/${id}`)
  },

  getLineByCode: (code: string) => {
    return request.get<Line>(`/metro/lines/code/${code}`)
  },

  getLineWithRoutes: (id: number) => {
    return request.get<Line>(`/metro/lines/${id}/routes`)
  },

  createLine: (lineDto: LineDto) => {
    return request.post<Line>('/metro/lines', lineDto)
  },

  updateLine: (id: number, lineDto: LineDto) => {
    return request.put<Line>(`/metro/lines/${id}`, lineDto)
  },

  deleteLine: (id: number | undefined) => {
    if (id === undefined) {
      return Promise.reject(new Error('Invalid line ID'))
    }
    return request.delete<boolean>(`/metro/lines/${id}`)
  },

  // Station methods
  getAllStations: () => {
    return request.get<Station[]>('/metro/stations')
  },

  getStationById: (id: number) => {
    return request.get<Station>(`/metro/stations/${id}`)
  },

  getStationByCode: (code: string) => {
    return request.get<Station>(`/metro/stations/code/${code}`)
  },

  getStationsByLineId: (lineId: number | undefined) => {
    if (lineId === undefined) {
      return Promise.reject(new Error('Invalid line ID'))
    }
    return request.get<Station[]>(`/metro/stations/line/${lineId}`)
  },

  getStationsByRouteId: (routeId: number) => {
    return request.get<Station[]>(`/metro/stations/route/${routeId}`)
  },

  getAllTransferStations: () => {
    return request.get<Station[]>('/metro/stations/transfer')
  },

  createStation: (stationDto: StationDto) => {
    return request.post<Station>('/metro/stations', stationDto)
  },

  updateStation: (id: number, stationDto: StationDto) => {
    return request.put<Station>(`/metro/stations/${id}`, stationDto)
  },

  deleteStation: (id: number | undefined) => {
    if (id === undefined) {
      return Promise.reject(new Error('Invalid station ID'))
    }
    return request.delete<boolean>(`/metro/stations/${id}`)
  },

  // Route methods
  getAllRoutes: () => {
    return request.get<Route[]>('/metro/routes')
  },

  getRouteById: (id: number) => {
    return request.get<Route>(`/metro/routes/${id}`)
  },

  getRoutesByLineId: (lineId: number) => {
    return request.get<Route[]>(`/metro/routes/line/${lineId}`)
  },

  getRouteWithStations: (id: number) => {
    return request.get<Route>(`/metro/routes/${id}/stations`)
  },

  findRoutesBetweenStations: (fromStationId: number, toStationId: number) => {
    return request.get<Route[]>(`/metro/routes/path?fromStationId=${fromStationId}&toStationId=${toStationId}`)
  },

  createRoute: (routeDto: RouteDto) => {
    return request.post<Route>('/metro/routes', routeDto)
  },

  updateRoute: (id: number, routeDto: RouteDto) => {
    return request.put<Route>(`/metro/routes/${id}`, routeDto)
  },

  deleteRoute: (id: number | undefined) => {
    if (id === undefined) {
      return Promise.reject(new Error('Invalid route ID'))
    }
    return request.delete<boolean>(`/metro/routes/${id}`)
  },

  // Stop methods
  getAllStops: () => {
    return request.get<Stop[]>('/metro/stops')
  },

  getStopById: (id: number) => {
    return request.get<Stop>(`/metro/stops/${id}`)
  },

  getStopsByRouteId: (routeId: number) => {
    return request.get<Stop[]>(`/metro/stops/route/${routeId}`)
  },

  createStop: (stopDto: StopDto) => {
    return request.post<Stop>('/metro/stops', stopDto)
  },

  updateStop: (id: number, stopDto: StopDto) => {
    return request.put<Stop>(`/metro/stops/${id}`, stopDto)
  },

  deleteStop: (id: number) => {
    return request.delete<boolean>(`/metro/stops/${id}`)
  }
} 