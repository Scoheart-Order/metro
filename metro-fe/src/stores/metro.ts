import { defineStore } from 'pinia'
import { 
  metroApi, 
  type Line, 
  type Station, 
  type Route, 
  type LineDto, 
  type StationDto, 
  type RouteDto,
  type TrainTrip,
  type TrainTripDto,
  type StopTime,
  type StopTimeDto,
  type Stop,
  type StopDto
} from '../api/modules/metro'

interface MetroState {
  lines: Line[]
  stations: Station[]
  routes: Route[]
  stops: Stop[]
  trainTrips: TrainTrip[]
  stopTimes: StopTime[]
  currentLine: Line | null
  currentStation: Station | null
  currentRoute: Route | null
  currentTrainTrip: TrainTrip | null
  currentStopTime: StopTime | null
  lineStations: Record<number, Station[]>
  lineRoutes: Record<number, Route[]>
  routeStops: Record<number, Stop[]>
  trainTripStopTimes: Record<number, StopTime[]>
  loading: boolean
  error: string | null
}

export const useMetroStore = defineStore('metro', {
  state: (): MetroState => ({
    lines: [],
    stations: [],
    routes: [],
    stops: [],
    trainTrips: [],
    stopTimes: [],
    currentLine: null,
    currentStation: null,
    currentRoute: null,
    currentTrainTrip: null,
    currentStopTime: null,
    lineStations: {},
    lineRoutes: {},
    routeStops: {},
    trainTripStopTimes: {},
    loading: false,
    error: null
  }),
  
  getters: {
    getLineById: (state) => (id: number) => {
      return state.lines.find(line => line.id === id)
    },
    
    getStationById: (state) => (id: number) => {
      return state.stations.find(station => station.id === id)
    },
    
    getRouteById: (state) => (id: number) => {
      return state.routes.find(route => route.id === id)
    },
    
    getLineByCode: (state) => (code: string) => {
      return state.lines.find(line => line.code === code)
    },
    
    getStationByCode: (state) => (code: string) => {
      return state.stations.find(station => station.code === code)
    },
    
    getStationsForLine: (state) => (lineId: number) => {
      return state.lineStations[lineId] || []
    },
    
    getRoutesForLine: (state) => (lineId: number) => {
      return state.lineRoutes[lineId] || []
    },
    
    getTransferStations: (state) => {
      return state.stations.filter(station => station.isTransferStation)
    },
    
    getStopById: (state) => (id: number) => {
      return state.stops.find(stop => stop.id === id)
    },
    
    getTrainTripById: (state) => (id: number) => {
      return state.trainTrips.find(trainTrip => trainTrip.id === id)
    },
    
    getStopTimeById: (state) => (id: number) => {
      return state.stopTimes.find(stopTime => stopTime.id === id)
    },
    
    getStopsForRoute: (state) => (routeId: number) => {
      return state.routeStops[routeId] || []
    },
    
    getStopTimesForTrainTrip: (state) => (trainTripId: number) => {
      return state.trainTripStopTimes[trainTripId] || []
    },
    
    getTrainTripsByRouteId: (state) => (routeId: number) => {
      return state.trainTrips.filter(trip => trip.routeId === routeId)
    }
  },
  
  actions: {
    // Line actions
    async fetchLines() {
      this.loading = true
      this.error = null
      
      try {
        this.lines = await metroApi.getAllLines()
        return true
      } catch (error) {
        console.error('Fetch lines error:', error)
        this.error = 'Failed to fetch lines'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async fetchLineById(id: number) {
      this.loading = true
      this.error = null
      
      try {
        this.currentLine = await metroApi.getLineById(id)
        return this.currentLine
      } catch (error) {
        console.error(`Fetch line ${id} error:`, error)
        this.error = `Failed to fetch line ${id}`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async fetchLineWithRoutes(id: number) {
      this.loading = true
      this.error = null
      
      try {
        const line = await metroApi.getLineWithRoutes(id)
        this.currentLine = line
        
        if (line.routes) {
          this.lineRoutes[id] = line.routes
        }
        
        return line
      } catch (error) {
        console.error(`Fetch line ${id} with routes error:`, error)
        this.error = `Failed to fetch line ${id} with routes`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async createLine(lineData: LineDto) {
      this.loading = true
      this.error = null
      
      try {
        const newLine = await metroApi.createLine(lineData)
        this.lines.push(newLine)
        return newLine
      } catch (error) {
        console.error('Create line error:', error)
        this.error = 'Failed to create line'
        return null
      } finally {
        this.loading = false
      }
    },
    
    async updateLine(id: number, lineData: LineDto) {
      this.loading = true
      this.error = null
      
      try {
        const updatedLine = await metroApi.updateLine(id, lineData)
        
        // Update line in state
        const index = this.lines.findIndex(line => line.id === id)
        if (index !== -1) {
          this.lines[index] = updatedLine
        }
        
        // Update currentLine if it matches
        if (this.currentLine && this.currentLine.id === id) {
          this.currentLine = updatedLine
        }
        
        return updatedLine
      } catch (error) {
        console.error(`Update line ${id} error:`, error)
        this.error = `Failed to update line ${id}`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async deleteLine(id: number | undefined) {
      this.loading = true
      this.error = null
      
      try {
        if (id === undefined) {
          this.error = 'Invalid line ID'
          return false
        }
        
        const success = await metroApi.deleteLine(id)
        
        if (success) {
          // Remove line from state
          this.lines = this.lines.filter(line => line.id !== id)
          
          // Reset currentLine if it matches
          if (this.currentLine && this.currentLine.id === id) {
            this.currentLine = null
          }
          
          // Remove related data
          delete this.lineStations[id]
          delete this.lineRoutes[id]
        }
        
        return success
      } catch (error) {
        console.error(`Delete line ${id} error:`, error)
        this.error = `Failed to delete line ${id}`
        return false
      } finally {
        this.loading = false
      }
    },
    
    // Station actions
    async fetchStations() {
      this.loading = true
      this.error = null
      
      try {
        this.stations = await metroApi.getAllStations()
        return true
      } catch (error) {
        console.error('Fetch stations error:', error)
        this.error = 'Failed to fetch stations'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async fetchStationById(id: number) {
      this.loading = true
      this.error = null
      
      try {
        this.currentStation = await metroApi.getStationById(id)
        return this.currentStation
      } catch (error) {
        console.error(`Fetch station ${id} error:`, error)
        this.error = `Failed to fetch station ${id}`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async fetchStationsByLineId(lineId: number | undefined) {
      this.loading = true
      this.error = null
      
      try {
        if (lineId === undefined) {
          this.error = 'Invalid line ID'
          return []
        }
        
        const stations = await metroApi.getStationsByLineId(lineId)
        this.lineStations[lineId] = stations
        return stations
      } catch (error) {
        console.error(`Fetch stations for line ${lineId} error:`, error)
        this.error = `Failed to fetch stations for line ${lineId}`
        return []
      } finally {
        this.loading = false
      }
    },
    
    async createStation(stationData: StationDto) {
      this.loading = true
      this.error = null
      
      try {
        const newStation = await metroApi.createStation(stationData)
        this.stations.push(newStation)
        
        // Update lineStations if this station belongs to lines
        if (stationData.lineIds) {
          stationData.lineIds.forEach(lineId => {
            if (!this.lineStations[lineId]) {
              this.lineStations[lineId] = []
            }
            this.lineStations[lineId].push(newStation)
          })
        }
        
        return newStation
      } catch (error) {
        console.error('Create station error:', error)
        this.error = 'Failed to create station'
        return null
      } finally {
        this.loading = false
      }
    },
    
    async updateStation(id: number, stationData: StationDto) {
      this.loading = true
      this.error = null
      
      try {
        const updatedStation = await metroApi.updateStation(id, stationData)
        
        // Update station in state
        const index = this.stations.findIndex(station => station.id === id)
        if (index !== -1) {
          this.stations[index] = updatedStation
        }
        
        // Update currentStation if it matches
        if (this.currentStation && this.currentStation.id === id) {
          this.currentStation = updatedStation
        }
        
        // Update station in lineStations
        Object.keys(this.lineStations).forEach(lineIdStr => {
          const lineId = Number(lineIdStr)
          const stationIndex = this.lineStations[lineId]?.findIndex(s => s.id === id)
          
          if (stationIndex !== undefined && stationIndex !== -1) {
            this.lineStations[lineId][stationIndex] = updatedStation
          }
        })
        
        return updatedStation
      } catch (error) {
        console.error(`Update station ${id} error:`, error)
        this.error = `Failed to update station ${id}`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async deleteStation(id: number | undefined) {
      this.loading = true
      this.error = null
      
      try {
        if (id === undefined) {
          this.error = 'Invalid station ID'
          return false
        }
        
        const success = await metroApi.deleteStation(id)
        
        if (success) {
          // Remove station from state
          this.stations = this.stations.filter(station => station.id !== id)
          
          // Reset currentStation if it matches
          if (this.currentStation && this.currentStation.id === id) {
            this.currentStation = null
          }
          
          // Remove station from lineStations
          Object.keys(this.lineStations).forEach(lineIdStr => {
            const lineId = Number(lineIdStr)
            this.lineStations[lineId] = this.lineStations[lineId]?.filter(s => s.id !== id) || []
          })
        }
        
        return success
      } catch (error) {
        console.error(`Delete station ${id} error:`, error)
        this.error = `Failed to delete station ${id}`
        return false
      } finally {
        this.loading = false
      }
    },
    
    // Route actions
    async fetchRoutes() {
      this.loading = true
      this.error = null
      
      try {
        this.routes = await metroApi.getAllRoutes()
        return true
      } catch (error) {
        console.error('Fetch routes error:', error)
        this.error = 'Failed to fetch routes'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async fetchRouteById(id: number) {
      this.loading = true
      this.error = null
      
      try {
        this.currentRoute = await metroApi.getRouteById(id)
        return this.currentRoute
      } catch (error) {
        console.error(`Fetch route ${id} error:`, error)
        this.error = `Failed to fetch route ${id}`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async fetchRouteWithStations(id: number) {
      this.loading = true
      this.error = null
      
      try {
        const route = await metroApi.getRouteWithStations(id)
        this.currentRoute = route
        return route
      } catch (error) {
        console.error(`Fetch route ${id} with stations error:`, error)
        this.error = `Failed to fetch route ${id} with stations`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async fetchRoutesByLineId(lineId: number) {
      this.loading = true
      this.error = null
      
      try {
        const routes = await metroApi.getRoutesByLineId(lineId)
        this.lineRoutes[lineId] = routes
        return routes
      } catch (error) {
        console.error(`Fetch routes for line ${lineId} error:`, error)
        this.error = `Failed to fetch routes for line ${lineId}`
        return []
      } finally {
        this.loading = false
      }
    },
    
    async createRoute(routeData: RouteDto) {
      this.loading = true
      this.error = null
      
      try {
        const newRoute = await metroApi.createRoute(routeData)
        this.routes.push(newRoute)
        
        // Update lineRoutes
        if (!this.lineRoutes[routeData.lineId]) {
          this.lineRoutes[routeData.lineId] = []
        }
        this.lineRoutes[routeData.lineId].push(newRoute)
        
        return newRoute
      } catch (error) {
        console.error('Create route error:', error)
        this.error = 'Failed to create route'
        return null
      } finally {
        this.loading = false
      }
    },
    
    async updateRoute(id: number, routeData: RouteDto) {
      this.loading = true
      this.error = null
      
      try {
        const updatedRoute = await metroApi.updateRoute(id, routeData)
        
        // Update route in state
        const index = this.routes.findIndex(route => route.id === id)
        if (index !== -1) {
          this.routes[index] = updatedRoute
        }
        
        // Update currentRoute if it matches
        if (this.currentRoute && this.currentRoute.id === id) {
          this.currentRoute = updatedRoute
        }
        
        // Update route in lineRoutes
        const oldRoute = this.routes.find(r => r.id === id)
        
        if (oldRoute) {
          // If line has changed, update both old and new line's routes
          if (oldRoute.lineId !== routeData.lineId) {
            // Remove from old line
            if (this.lineRoutes[oldRoute.lineId]) {
              this.lineRoutes[oldRoute.lineId] = this.lineRoutes[oldRoute.lineId].filter(r => r.id !== id)
            }
            
            // Add to new line
            if (!this.lineRoutes[routeData.lineId]) {
              this.lineRoutes[routeData.lineId] = []
            }
            this.lineRoutes[routeData.lineId].push(updatedRoute)
          } else {
            // Just update the route in the same line
            const routeIndex = this.lineRoutes[oldRoute.lineId]?.findIndex(r => r.id === id)
            if (routeIndex !== undefined && routeIndex !== -1) {
              this.lineRoutes[oldRoute.lineId][routeIndex] = updatedRoute
            }
          }
        }
        
        return updatedRoute
      } catch (error) {
        console.error(`Update route ${id} error:`, error)
        this.error = `Failed to update route ${id}`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async deleteRoute(id: number | undefined) {
      this.loading = true
      this.error = null
      
      try {
        if (id === undefined) {
          this.error = 'Invalid route ID'
          return false
        }
        
        const success = await metroApi.deleteRoute(id)
        
        if (success) {
          // Find the route first to get the lineId
          const route = this.routes.find(r => r.id === id)
          
          // Remove route from state
          this.routes = this.routes.filter(route => route.id !== id)
          
          // Reset currentRoute if it matches
          if (this.currentRoute && this.currentRoute.id === id) {
            this.currentRoute = null
          }
          
          // Remove route from lineRoutes
          if (route) {
            this.lineRoutes[route.lineId] = this.lineRoutes[route.lineId]?.filter(r => r.id !== id) || []
          }
        }
        
        return success
      } catch (error) {
        console.error(`Delete route ${id} error:`, error)
        this.error = `Failed to delete route ${id}`
        return false
      } finally {
        this.loading = false
      }
    },
    
    // Path finding
    async findRoutesBetweenStations(fromStationId: number, toStationId: number) {
      this.loading = true
      this.error = null
      
      try {
        return await metroApi.findRoutesBetweenStations(fromStationId, toStationId)
      } catch (error) {
        console.error(`Find routes between stations ${fromStationId} and ${toStationId} error:`, error)
        this.error = 'Failed to find routes between stations'
        return []
      } finally {
        this.loading = false
      }
    },
    
    // Stop actions
    async fetchStops() {
      this.loading = true
      this.error = null
      
      try {
        this.stops = await metroApi.getAllStops()
        return true
      } catch (error) {
        console.error('Fetch stops error:', error)
        this.error = 'Failed to fetch stops'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async fetchStopById(id: number) {
      this.loading = true
      this.error = null
      
      try {
        const stop = await metroApi.getStopById(id)
        return stop
      } catch (error) {
        console.error(`Fetch stop ${id} error:`, error)
        this.error = `Failed to fetch stop ${id}`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async fetchStopsByRouteId(routeId: number) {
      this.loading = true
      this.error = null
      
      try {
        const stops = await metroApi.getStopsByRouteId(routeId)
        this.routeStops[routeId] = stops
        return stops
      } catch (error) {
        console.error(`Fetch stops for route ${routeId} error:`, error)
        this.error = `Failed to fetch stops for route ${routeId}`
        return []
      } finally {
        this.loading = false
      }
    },
    
    async createStop(stopData: StopDto) {
      this.loading = true
      this.error = null
      
      try {
        const newStop = await metroApi.createStop(stopData)
        this.stops.push(newStop)
        
        // Update route stops
        if (!this.routeStops[stopData.routeId]) {
          this.routeStops[stopData.routeId] = []
        }
        this.routeStops[stopData.routeId].push(newStop)
        
        return newStop
      } catch (error) {
        console.error('Create stop error:', error)
        this.error = 'Failed to create stop'
        return null
      } finally {
        this.loading = false
      }
    },
    
    async updateStop(id: number, stopData: StopDto) {
      this.loading = true
      this.error = null
      
      try {
        const updatedStop = await metroApi.updateStop(id, stopData)
        
        // Update stop in state
        const index = this.stops.findIndex(stop => stop.id === id)
        if (index !== -1) {
          this.stops[index] = updatedStop
        }
        
        // Update stop in routeStops
        Object.keys(this.routeStops).forEach(routeIdStr => {
          const routeId = Number(routeIdStr)
          const stopIndex = this.routeStops[routeId]?.findIndex(s => s.id === id)
          
          if (stopIndex !== undefined && stopIndex !== -1) {
            this.routeStops[routeId][stopIndex] = updatedStop
          }
        })
        
        return updatedStop
      } catch (error) {
        console.error(`Update stop ${id} error:`, error)
        this.error = `Failed to update stop ${id}`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async deleteStop(id: number) {
      this.loading = true
      this.error = null
      
      try {
        const success = await metroApi.deleteStop(id)
        
        if (success) {
          // Remove stop from state
          this.stops = this.stops.filter(stop => stop.id !== id)
          
          // Remove stop from routeStops
          Object.keys(this.routeStops).forEach(routeIdStr => {
            const routeId = Number(routeIdStr)
            this.routeStops[routeId] = this.routeStops[routeId]?.filter(s => s.id !== id) || []
          })
        }
        
        return success
      } catch (error) {
        console.error(`Delete stop ${id} error:`, error)
        this.error = `Failed to delete stop ${id}`
        return false
      } finally {
        this.loading = false
      }
    },
    
    // TrainTrip actions
    async fetchTrainTrips() {
      this.loading = true
      this.error = null
      
      try {
        this.trainTrips = await metroApi.getAllTrainTrips()
        return true
      } catch (error) {
        console.error('Fetch train trips error:', error)
        this.error = 'Failed to fetch train trips'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async fetchTrainTripById(id: number) {
      this.loading = true
      this.error = null
      
      try {
        this.currentTrainTrip = await metroApi.getTrainTripById(id)
        return this.currentTrainTrip
      } catch (error) {
        console.error(`Fetch train trip ${id} error:`, error)
        this.error = `Failed to fetch train trip ${id}`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async fetchTrainTripsByRouteId(routeId: number) {
      this.loading = true
      this.error = null
      
      try {
        const trainTrips = await metroApi.getTrainTripsByRouteId(routeId)
        // Filter and update trainTrips array
        this.trainTrips = this.trainTrips.filter(trip => trip.routeId !== routeId)
        this.trainTrips.push(...trainTrips)
        return trainTrips
      } catch (error) {
        console.error(`Fetch train trips for route ${routeId} error:`, error)
        this.error = `Failed to fetch train trips for route ${routeId}`
        return []
      } finally {
        this.loading = false
      }
    },
    
    async fetchTrainTripWithStopTimes(id: number) {
      this.loading = true
      this.error = null
      
      try {
        const trainTrip = await metroApi.getTrainTripWithStopTimes(id)
        this.currentTrainTrip = trainTrip
        
        // Load stop times if available
        if (trainTrip.stopTimeIds && trainTrip.stopTimeIds.length > 0) {
          const stopTimes = await metroApi.getStopTimesByTrainTripId(id)
          this.trainTripStopTimes[id] = stopTimes
        }
        
        return trainTrip
      } catch (error) {
        console.error(`Fetch train trip ${id} with stop times error:`, error)
        this.error = `Failed to fetch train trip ${id} with stop times`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async createTrainTrip(trainTripData: TrainTripDto) {
      this.loading = true
      this.error = null
      
      try {
        const newTrainTrip = await metroApi.createTrainTrip(trainTripData)
        this.trainTrips.push(newTrainTrip)
        return newTrainTrip
      } catch (error) {
        console.error('Create train trip error:', error)
        this.error = 'Failed to create train trip'
        return null
      } finally {
        this.loading = false
      }
    },
    
    async updateTrainTrip(id: number, trainTripData: TrainTripDto) {
      this.loading = true
      this.error = null
      
      try {
        const updatedTrainTrip = await metroApi.updateTrainTrip(id, trainTripData)
        
        // Update train trip in state
        const index = this.trainTrips.findIndex(trip => trip.id === id)
        if (index !== -1) {
          this.trainTrips[index] = updatedTrainTrip
        }
        
        // Update currentTrainTrip if it matches
        if (this.currentTrainTrip && this.currentTrainTrip.id === id) {
          this.currentTrainTrip = updatedTrainTrip
        }
        
        return updatedTrainTrip
      } catch (error) {
        console.error(`Update train trip ${id} error:`, error)
        this.error = `Failed to update train trip ${id}`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async deleteTrainTrip(id: number | undefined) {
      this.loading = true
      this.error = null
      
      try {
        if (id === undefined) {
          this.error = 'Invalid train trip ID'
          return false
        }
        
        const success = await metroApi.deleteTrainTrip(id)
        
        if (success) {
          // Remove train trip from state
          this.trainTrips = this.trainTrips.filter(trip => trip.id !== id)
          
          // Reset currentTrainTrip if it matches
          if (this.currentTrainTrip && this.currentTrainTrip.id === id) {
            this.currentTrainTrip = null
          }
          
          // Remove related data
          delete this.trainTripStopTimes[id]
        }
        
        return success
      } catch (error) {
        console.error(`Delete train trip ${id} error:`, error)
        this.error = `Failed to delete train trip ${id}`
        return false
      } finally {
        this.loading = false
      }
    },
    
    // StopTime actions
    async fetchStopTimes() {
      this.loading = true
      this.error = null
      
      try {
        this.stopTimes = await metroApi.getAllStopTimes()
        return true
      } catch (error) {
        console.error('Fetch stop times error:', error)
        this.error = 'Failed to fetch stop times'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async fetchStopTimeById(id: number) {
      this.loading = true
      this.error = null
      
      try {
        this.currentStopTime = await metroApi.getStopTimeById(id)
        return this.currentStopTime
      } catch (error) {
        console.error(`Fetch stop time ${id} error:`, error)
        this.error = `Failed to fetch stop time ${id}`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async fetchStopTimesByTrainTripId(trainTripId: number) {
      this.loading = true
      this.error = null
      
      try {
        const stopTimes = await metroApi.getStopTimesByTrainTripId(trainTripId)
        this.trainTripStopTimes[trainTripId] = stopTimes
        return stopTimes
      } catch (error) {
        console.error(`Fetch stop times for train trip ${trainTripId} error:`, error)
        this.error = `Failed to fetch stop times for train trip ${trainTripId}`
        return []
      } finally {
        this.loading = false
      }
    },
    
    async createStopTime(stopTimeData: StopTimeDto) {
      this.loading = true
      this.error = null
      
      try {
        const newStopTime = await metroApi.createStopTime(stopTimeData)
        this.stopTimes.push(newStopTime)
        
        // Update trainTripStopTimes
        if (!this.trainTripStopTimes[stopTimeData.trainTripId]) {
          this.trainTripStopTimes[stopTimeData.trainTripId] = []
        }
        this.trainTripStopTimes[stopTimeData.trainTripId].push(newStopTime)
        
        return newStopTime
      } catch (error) {
        console.error('Create stop time error:', error)
        this.error = 'Failed to create stop time'
        return null
      } finally {
        this.loading = false
      }
    },
    
    async updateStopTime(id: number, stopTimeData: StopTimeDto) {
      this.loading = true
      this.error = null
      
      try {
        const updatedStopTime = await metroApi.updateStopTime(id, stopTimeData)
        
        // Update stop time in state
        const index = this.stopTimes.findIndex(stopTime => stopTime.id === id)
        if (index !== -1) {
          this.stopTimes[index] = updatedStopTime
        }
        
        // Update currentStopTime if it matches
        if (this.currentStopTime && this.currentStopTime.id === id) {
          this.currentStopTime = updatedStopTime
        }
        
        // Update stop time in trainTripStopTimes
        Object.keys(this.trainTripStopTimes).forEach(trainTripIdStr => {
          const trainTripId = Number(trainTripIdStr)
          const stopTimeIndex = this.trainTripStopTimes[trainTripId]?.findIndex(st => st.id === id)
          
          if (stopTimeIndex !== undefined && stopTimeIndex !== -1) {
            this.trainTripStopTimes[trainTripId][stopTimeIndex] = updatedStopTime
          }
        })
        
        return updatedStopTime
      } catch (error) {
        console.error(`Update stop time ${id} error:`, error)
        this.error = `Failed to update stop time ${id}`
        return null
      } finally {
        this.loading = false
      }
    },
    
    async deleteStopTime(id: number | undefined) {
      this.loading = true
      this.error = null
      
      try {
        if (id === undefined) {
          this.error = 'Invalid stop time ID'
          return false
        }
        
        const success = await metroApi.deleteStopTime(id)
        
        if (success) {
          // Get the stop time before removing it
          const stopTime = this.stopTimes.find(st => st.id === id)
          
          // Remove stop time from state
          this.stopTimes = this.stopTimes.filter(stopTime => stopTime.id !== id)
          
          // Reset currentStopTime if it matches
          if (this.currentStopTime && this.currentStopTime.id === id) {
            this.currentStopTime = null
          }
          
          // Remove stop time from trainTripStopTimes
          if (stopTime && stopTime.trainTripId) {
            this.trainTripStopTimes[stopTime.trainTripId] = 
              this.trainTripStopTimes[stopTime.trainTripId]?.filter(st => st.id !== id) || []
          }
        }
        
        return success
      } catch (error) {
        console.error(`Delete stop time ${id} error:`, error)
        this.error = `Failed to delete stop time ${id}`
        return false
      } finally {
        this.loading = false
      }
    },
    
    // Reset state
    resetState() {
      this.lines = []
      this.stations = []
      this.routes = []
      this.stops = []
      this.trainTrips = []
      this.stopTimes = []
      this.currentLine = null
      this.currentStation = null
      this.currentRoute = null
      this.currentTrainTrip = null
      this.currentStopTime = null
      this.lineStations = {}
      this.lineRoutes = {}
      this.routeStops = {}
      this.trainTripStopTimes = {}
      this.loading = false
      this.error = null
    }
  }
}) 