import { defineStore } from 'pinia'
import { authApi, userApi, type LoginDto, type PhoneLoginDto, type RegisterDto, type User, type UserProfileDto } from '../api'
import { ROLE_NAMES, hasRole, isAdmin, isSuperAdmin } from '../constants/roles'

interface UserState {
  token: string | null
  user: User | null
  isLoggedIn: boolean
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('token'),
    user: null,
    isLoggedIn: !!localStorage.getItem('token')
  }),
  
  getters: {
    // Helper to check if user has a specific role
    hasRole: (state) => (roleName: string) => {
      return hasRole(state.user?.roles, roleName);
    },
    
    isAdmin: (state) => {
      return isAdmin(state.user?.roles);
    },
    
    isSuperAdmin: (state) => {
      return isSuperAdmin(state.user?.roles);
    },
    
    // Returns the appropriate home route for the current user
    homeRoute: (state) => {
      if (!state.user) return '/login';
      
      // Use the imported helper functions to check roles
      if (isSuperAdmin(state.user.roles)) {
        return '/superadmin/user-management';
      } else if (isAdmin(state.user.roles)) {
        return '/admin';
      } else {
        return '/';
      }
    },
    
    // Get user balance/money
    balance: (state) => state.user?.money || 0
  },
  
  actions: {
    async login(loginDto: LoginDto) {
      try {
        const tokenData = await authApi.login(loginDto)
        
        this.token = tokenData.accessToken
        this.isLoggedIn = true
        
        localStorage.setItem('token', tokenData.accessToken)
        
        // No longer fetching profile automatically
        
        return true
      } catch (error) {
        console.error('Login error:', error)
        // 不返回false，而是抛出错误，让调用者处理
        throw error
      }
    },
    
    async loginByPhone(phoneLoginDto: PhoneLoginDto) {
      try {
        const tokenData = await authApi.loginByPhone(phoneLoginDto)
        
        this.token = tokenData.accessToken
        this.isLoggedIn = true
        
        localStorage.setItem('token', tokenData.accessToken)
        
        // No longer fetching profile automatically
        
        return true
      } catch (error) {
        console.error('Login by phone error:', error)
        // 不返回false，而是抛出错误，让调用者处理
        throw error
      }
    },
    
    async register(userData: RegisterDto) {
      try {
        await authApi.register(userData)
        return { success: true }
      } catch (error) {
        console.error('Register error:', error)
        return { success: false, error }
      }
    },
    
    async fetchProfile() {
      try {
        if (!this.token) {
          console.warn('No token available for fetchProfile')
          return false
        }
        
        console.log('Fetching user profile...')
        const userData = await userApi.getProfile()
        
        if (!userData) {
          console.error('Received empty user data from API')
          return false
        }
        
        // Log successful profile fetch with role information
        const roleNames = userData.roles.map(role => role.name).join(', ');
        console.log(`User profile loaded successfully. Username: ${userData.username}, Roles: ${roleNames}`)
        
        this.user = userData
        return true
      } catch (error) {
        console.error('Fetch profile error:', error)
        // If profile fetch fails, assume the token is invalid
        this.token = null
        this.user = null
        this.isLoggedIn = false
        localStorage.removeItem('token')
        return false
      }
    },
    
    async updateProfile(profileData: UserProfileDto) {
      try {
        const updatedUser = await userApi.updateProfile(profileData)
        this.user = { ...this.user, ...updatedUser }
        return true
      } catch (error) {
        console.error('Update profile error:', error)
        return false
      }
    },
    
    async uploadAvatar(file: File) {
      try {
        const response = await userApi.uploadAvatar(file)
        
        if (this.user) {
          this.user.avatar = response.avatarUrl
        }
        
        return true
      } catch (error) {
        console.error('Avatar upload error:', error)
        return false
      }
    },
    
    async logout() {
      try {
        if (this.token) {
          await authApi.logout()
        }
      } catch (error) {
        console.error('Logout error:', error)
      } finally {
        this.token = null
        this.user = null
        this.isLoggedIn = false
        localStorage.removeItem('token')
      }
    }
  }
}) 