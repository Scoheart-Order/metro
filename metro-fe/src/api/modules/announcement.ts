import { request } from '../request'

// Announcement DTO
export interface AnnouncementDto {
  title: string
  content: string
}

export interface Announcement {
  id: number
  title: string
  content: string
  adminId: number
  adminName?: string
  createdAt: string
  updatedAt: string
}

/**
 * Announcement API methods
 */
export const announcementApi = {
  /**
   * Get all announcements
   */
  getAllAnnouncements: () => {
    return request.get<Announcement[]>('/announcements')
  },

  /**
   * Get announcement by ID
   */
  getAnnouncementById: (id: number) => {
    return request.get<Announcement>(`/announcements/${id}`)
  },

  /**
   * Create a new announcement (admin only)
   */
  createAnnouncement: (announcementDto: AnnouncementDto) => {
    return request.post<Announcement>('/announcements', announcementDto)
  },

  /**
   * Update an existing announcement (admin only)
   */
  updateAnnouncement: (id: number, announcementDto: AnnouncementDto) => {
    return request.put<Announcement>(`/announcements/${id}`, announcementDto)
  },

  /**
   * Delete an announcement (admin only)
   */
  deleteAnnouncement: (id: number) => {
    return request.delete<void>(`/announcements/${id}`)
  }
} 