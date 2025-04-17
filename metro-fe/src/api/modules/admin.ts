import { request } from '../request'
import type { User } from './user'

export interface AdminUserUpdateDto {
  username?: string
  realName?: string
  email?: string
  phone?: string
  role?: string
  status?: string
}

export interface AdminUserCreateDto {
  username: string
  password: string
  realName?: string
  email?: string
  phone?: string
  role: string
  status?: string
}

export interface PasswordResetDto {
  password: string
  confirmPassword: string
}

/**
 * SuperAdmin API methods for user management
 */
export const adminApi = {
  /**
   * Get all users
   */
  getAllUsers: () => {
    return request.get<User[]>('/superadmin/users')
  },

  /**
   * Get user by ID
   */
  getUserById: (id: number) => {
    return request.get<User>(`/superadmin/users/${id}`)
  },

  /**
   * Create a new user
   */
  createUser: (userData: AdminUserCreateDto) => {
    return request.post<User>('/superadmin/users', userData)
  },

  /**
   * Update a user
   */
  updateUser: (id: number, userData: AdminUserUpdateDto) => {
    return request.put<User>(`/superadmin/users/${id}`, userData)
  },

  /**
   * Toggle user status (enable/disable)
   */
  toggleUserStatus: (id: number) => {
    return request.put<User>(`/superadmin/users/${id}/toggle-status`)
  },

  /**
   * Reset user password
   */
  resetPassword: (id: number, newPassword: string) => {
    return request.put<void>(`/superadmin/users/${id}/reset-password?newPassword=${newPassword}`)
  },

  /**
   * Delete a user
   */
  deleteUser: (id: number) => {
    return request.delete<void>(`/superadmin/users/${id}`)
  }
} 