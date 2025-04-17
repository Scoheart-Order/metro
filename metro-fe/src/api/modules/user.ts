import { request } from '../request'
import type { PasswordUpdateDto } from './auth'

// User profile DTO
export interface UserProfileDto {
  realName?: string
  email?: string
  phone?: string
  gender?: string
  birthDate?: string
  avatar?: string
  address?: string
  bio?: string
}

export interface RechargeDto {
  amount: number
}

// Role interface matching backend structure
export interface Role {
  id: number
  name: string
  description: string
}

export interface User {
  id: number
  username: string
  email: string | null
  phone: string | null
  avatar: string | null
  roles: Role[]
  money: number
  enabled: boolean
  accountNonExpired: boolean
  accountNonLocked: boolean
  credentialsNonExpired: boolean
  realName?: string | null
  gender?: string | null
  birthDate?: string | null
  address?: string | null
  bio?: string | null
  createTime: string
  updateTime: string
}

/**
 * User API methods
 */
export const userApi = {
  /**
   * Get the current user's profile
   */
  getProfile: () => {
    return request.get<User>('/user/profile')
  },

  /**
   * Update the current user's profile
   */
  updateProfile: (profileDto: UserProfileDto) => {
    return request.put<User>('/user/profile', profileDto)
  },

  /**
   * Update the current user's password
   */
  updatePassword: (passwordDto: PasswordUpdateDto) => {
    return request.put<void>('/user/profile/password', passwordDto)
  },

  /**
   * Recharge the current user's account
   */
  recharge: (rechargeDto: RechargeDto) => {
    return request.post<void>('/user/recharge', rechargeDto)
  },

  /**
   * Get the current user's balance
   */
  getBalance: () => {
    return request.get<number>('/user/balance')
  },

  /**
   * Upload avatar for the current user
   * @param file The avatar file to upload
   */
  uploadAvatar: (file: File) => {
    const formData = new FormData()
    formData.append('avatar', file)
    
    return request.post<{ avatarUrl: string }>('/user/upload-avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
} 