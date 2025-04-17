import { request } from '../request'

// Models/interfaces that match backend DTOs
export interface LoginDto {
  username: string
  password: string
}

export interface PhoneLoginDto {
  phone: string
  password: string
}

export interface RegisterDto {
  username: string
  password: string
  email: string
  phone: string
}

export interface TokenDto {
  accessToken: string
  refreshToken: string
  tokenType: string
}

export interface PasswordUpdateDto {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

/**
 * Auth API methods
 */
export const authApi = {
  /**
   * Login with username and password
   */
  login: (loginDto: LoginDto) => {
    return request.post<TokenDto>('/auth/login', loginDto)
  },

  /**
   * Login with phone and password
   */
  loginByPhone: (phoneLoginDto: PhoneLoginDto) => {
    return request.post<TokenDto>('/auth/login/phone', phoneLoginDto)
  },

  /**
   * Register a new user
   */
  register: (registerDto: RegisterDto) => {
    return request.post<void>('/auth/register', registerDto)
  },

  /**
   * Refresh the access token using a refresh token
   */
  refreshToken: () => {
    return request.post<TokenDto>('/auth/refresh-token')
  },

  /**
   * Logout the current user
   */
  logout: () => {
    return request.post<void>('/auth/logout')
  },

  /**
   * Get current user information
   */
  getCurrentUser: () => {
    return request.get('/auth/me')
  }
} 