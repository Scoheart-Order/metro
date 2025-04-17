import { defineStore } from 'pinia'
import { 
  metroApi, 
  type Line, 
  type Station, 
  type Route, 
  type LineDto, 
  type StationDto, 
  type RouteDto 
} from '../api/modules/metro'

interface MetroState {
  lines: Line[]
  stations: Station[]
  routes: Route[]
  currentLine: Line | null
  currentStation: Station | null
  currentRoute: Route | null
  lineStations: Record<number, Station[]>
  lineRoutes: Record<number, Route[]>
  loading: boolean
  error: string | null
}

export const useMetroStore = defineStore('metro', {
  state: (): MetroState => ({
    lines: [],
    stations: [],
    routes: [],
    currentLine: null,
    currentStation: null,
    currentRoute: null,
    lineStations: {},
    lineRoutes: {},
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
    
    // Reset state
    resetState() {
      this.lines = []
      this.stations = []
      this.routes = []
      this.currentLine = null
      this.currentStation = null
      this.currentRoute = null
      this.lineStations = {}
      this.lineRoutes = {}
      this.error = null
    }
  }
}) 