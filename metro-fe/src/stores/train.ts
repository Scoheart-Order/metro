import { defineStore } from 'pinia'
import { metroApi, type RouteDto, type LineDto } from '../api'

interface Train {
  id: number
  trainNumber: string
  startStation: string
  endStation: string
  departureTime: string
  arrivalTime: string
  status: 'on-time' | 'delayed' | 'cancelled'
}

interface TrainState {
  trains: Train[]
  routes: RouteDto[]
  lines: LineDto[]
  loading: boolean
  error: string | null
}

export const useTrainStore = defineStore('train', {
  state: (): TrainState => ({
    trains: [],
    routes: [],
    lines: [],
    loading: false,
    error: null
  }),
  
  getters: {
    getTrainById: (state) => (id: number) => {
      return state.trains.find(train => train.id === id)
    },
    getRouteById: (state) => (id: number) => {
      return state.routes.find(route => route.id === id)
    },
    getLineById: (state) => (id: number) => {
      return state.lines.find(line => line.id === id)
    }
  },
  
  actions: {
    async fetchTrains() {
      this.loading = true
      this.error = null
      
      try {
        // TODO: Implement train API endpoints and calls
        // this.trains = await trainApi.getAllTrains()
        return true
      } catch (error) {
        console.error('Fetch trains error:', error)
        this.error = 'Failed to fetch train information'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async fetchRoutes() {
      this.loading = true
      this.error = null
      
      try {
        this.routes = await metroApi.getAllRoutes()
        return true
      } catch (error) {
        console.error('Fetch routes error:', error)
        this.error = 'Failed to fetch route information'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async fetchLines() {
      this.loading = true
      this.error = null
      
      try {
        this.lines = await metroApi.getAllLines()
        return true
      } catch (error) {
        console.error('Fetch lines error:', error)
        this.error = 'Failed to fetch line information'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async searchTrains(params: { 
      startStation?: string,
      endStation?: string,
      date?: string
    }) {
      this.loading = true
      this.error = null
      
      try {
        // TODO: Implement search trains API
        // this.trains = await trainApi.searchTrains(params)
        return true
      } catch (error) {
        console.error('Search trains error:', error)
        this.error = 'Failed to search trains'
        return false
      } finally {
        this.loading = false
      }
    },
    
    // Lines management
    async createLine(lineData: LineDto) {
      this.loading = true
      this.error = null
      
      try {
        const newLine = await metroApi.createLine(lineData)
        this.lines.push(newLine)
        return true
      } catch (error) {
        console.error('Create line error:', error)
        this.error = 'Failed to create line'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async updateLine(id: number, lineData: LineDto) {
      this.loading = true
      this.error = null
      
      try {
        const updatedLine = await metroApi.updateLine(id, lineData)
        
        const index = this.lines.findIndex(line => line.id === id)
        if (index !== -1) {
          this.lines[index] = updatedLine
        }
        
        return true
      } catch (error) {
        console.error('Update line error:', error)
        this.error = 'Failed to update line'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async deleteLine(id: number) {
      this.loading = true
      this.error = null
      
      try {
        const success = await metroApi.deleteLine(id)
        
        if (success) {
          this.lines = this.lines.filter(line => line.id !== id)
        }
        
        return success
      } catch (error) {
        console.error('Delete line error:', error)
        this.error = 'Failed to delete line'
        return false
      } finally {
        this.loading = false
      }
    },
    
    // Route management
    async createRoute(routeData: RouteDto) {
      this.loading = true
      this.error = null
      
      try {
        const newRoute = await metroApi.createRoute(routeData)
        this.routes.push(newRoute)
        return true
      } catch (error) {
        console.error('Create route error:', error)
        this.error = 'Failed to create route'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async updateRoute(id: number, routeData: RouteDto) {
      this.loading = true
      this.error = null
      
      try {
        const updatedRoute = await metroApi.updateRoute(id, routeData)
        
        const index = this.routes.findIndex(route => route.id === id)
        if (index !== -1) {
          this.routes[index] = updatedRoute
        }
        
        return true
      } catch (error) {
        console.error('Update route error:', error)
        this.error = 'Failed to update route'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async deleteRoute(id: number) {
      this.loading = true
      this.error = null
      
      try {
        const success = await metroApi.deleteRoute(id)
        
        if (success) {
          this.routes = this.routes.filter(route => route.id !== id)
        }
        
        return success
      } catch (error) {
        console.error('Delete route error:', error)
        this.error = 'Failed to delete route'
        return false
      } finally {
        this.loading = false
      }
    }
  }
}) 