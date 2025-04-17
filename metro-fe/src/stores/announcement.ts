import { defineStore } from 'pinia'
import { announcementApi, type Announcement, type AnnouncementDto } from '../api'

interface AnnouncementState {
  announcements: Announcement[]
  loading: boolean
  error: string | null
}

export const useAnnouncementStore = defineStore('announcement', {
  state: (): AnnouncementState => ({
    announcements: [],
    loading: false,
    error: null
  }),
  
  getters: {
    getAnnouncementById: (state) => (id: number) => {
      return state.announcements.find(announcement => announcement.id === id)
    }
  },
  
  actions: {
    async fetchAnnouncements() {
      this.loading = true
      this.error = null
      
      try {
        this.announcements = await announcementApi.getAllAnnouncements()
        return true
      } catch (error) {
        console.error('Fetch announcements error:', error)
        this.error = 'Failed to fetch announcements'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async getAnnouncementDetails(id: number) {
      this.loading = true
      this.error = null
      
      try {
        return await announcementApi.getAnnouncementById(id)
      } catch (error) {
        console.error('Get announcement details error:', error)
        this.error = 'Failed to fetch announcement details'
        return null
      } finally {
        this.loading = false
      }
    },
    
    async createAnnouncement(announcementData: AnnouncementDto) {
      this.loading = true
      this.error = null
      
      try {
        const newAnnouncement = await announcementApi.createAnnouncement(announcementData)
        this.announcements.unshift(newAnnouncement)
        return true
      } catch (error) {
        console.error('Create announcement error:', error)
        this.error = 'Failed to create announcement'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async updateAnnouncement(id: number, announcementData: AnnouncementDto) {
      this.loading = true
      this.error = null
      
      try {
        const updatedAnnouncement = await announcementApi.updateAnnouncement(id, announcementData)
        
        const index = this.announcements.findIndex(announcement => announcement.id === id)
        if (index !== -1) {
          this.announcements[index] = updatedAnnouncement
        }
        
        return true
      } catch (error) {
        console.error('Update announcement error:', error)
        this.error = 'Failed to update announcement'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async deleteAnnouncement(id: number) {
      this.loading = true
      this.error = null
      
      try {
        await announcementApi.deleteAnnouncement(id)
        
        this.announcements = this.announcements.filter(announcement => announcement.id !== id)
        
        return true
      } catch (error) {
        console.error('Delete announcement error:', error)
        this.error = 'Failed to delete announcement'
        return false
      } finally {
        this.loading = false
      }
    }
  }
}) 