import { request } from './request'
import { authApi } from './modules/auth'
import { userApi } from './modules/user'
import { feedbackApi } from './modules/feedback'
import { metroApi } from './modules/metro'
import { announcementApi } from './modules/announcement'

// Re-export all API modules
export * from './modules/auth'
export * from './modules/user'
export * from './modules/feedback'
export * from './modules/metro'
export * from './modules/announcement'

// Export the request service for direct use if needed
export { request }

// Export a default object with all APIs for convenience
export default {
  request,
  authApi,
  userApi,
  feedbackApi,
  metroApi,
  announcementApi
} 