import axios from 'axios'
import type { AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

// Define response structure to match backend R<T> class
export interface ApiResponse<T = any> {
  code: number;
  data: T;
  message: string;
  success: boolean;
}

// Determine if we're in a Docker environment (this can be set in your Docker container)
const isDocker = import.meta.env.VITE_DOCKER_ENV === 'true';

// Choose the appropriate base URL
const apiBaseUrl = isDocker 
  ? import.meta.env.VITE_API_BASE_URL_DOCKER 
  : import.meta.env.VITE_API_BASE_URL;

// Create axios instance
const service = axios.create({
  baseURL: apiBaseUrl || 'http://8.130.172.82:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor for API calls
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response interceptor for API calls
service.interceptors.response.use(
  response => {
    // Extract the actual data from the response based on backend structure
    if (response.data && typeof response.data === 'object') {
      if ('success' in response.data && !response.data.success) {
        // Handle API-level errors that return success: false
        ElMessage.error(response.data.message || '操作失败')
        return Promise.reject(new Error(response.data.message || '操作失败'))
      }
    }
    return response
  },
  error => {
    const { response } = error
    
    if (response) {
      // Handle different error statuses
      switch (response.status) {
        case 401:
          // Unauthorized - clear token but don't refresh page
          localStorage.removeItem('token')
          
          // Show error message
          ElMessage.error('您的登录已过期，请重新登录')
          
          // Don't use window.location.href which causes page refresh
          // Instead, let the component handle redirection via router
          // The router navigation guard will redirect to login on next route change
          break
        case 403:
          // Forbidden
          ElMessage.error('您没有权限执行此操作')
          break
        case 404:
          // Not found
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          // Server error
          ElMessage.error('服务器错误，请稍后再试')
          break
        default:
          // Other errors
          ElMessage.error(response.data?.message || '请求失败')
      }
    } else {
      // Network error
      ElMessage.error('网络错误，请检查您的网络连接')
    }
    
    return Promise.reject(error)
  }
)

// Helper methods to better match backend response structure
export const request = {
  get: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    return service.get<ApiResponse<T>>(url, config).then(res => res.data.data)
  },
  
  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
    return service.post<ApiResponse<T>>(url, data, config).then(res => res.data.data)
  },
  
  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
    return service.put<ApiResponse<T>>(url, data, config).then(res => res.data.data)
  },
  
  delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    return service.delete<ApiResponse<T>>(url, config).then(res => res.data.data)
  }
}

export default service 