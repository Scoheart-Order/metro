import axios from 'axios';
import type { AxiosRequestConfig, AxiosResponse } from 'axios';

// Define response structure to match backend R<T> class
export interface ApiResponse<T = any> {
  code: number;
  data: T;
  message: string;
  success: boolean;
}

// Create axios instance
const axiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for API calls
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for API calls
axiosInstance.interceptors.response.use(
  (response) => {
    // Extract the actual data from the response based on backend structure
    if (response.data && typeof response.data === 'object') {
      if ('success' in response.data && !response.data.success) {
        // Handle API-level errors that return success: false without showing messages
        return Promise.reject(new Error(response.data.message || '操作失败'));
      }
    }
    return response;
  },
  (error) => {
    const { response } = error;
    if (response) {
      // Handle special cases like token expiration
      switch (response.status) {
        case 401:
          localStorage.removeItem('token');
          break;
      }
      error.statusCode = response.status;
      error.statusMessage = response.statusText;
      error.apiMessage = response.data?.message;
    }

    return Promise.reject(error);
  }
);

// Helper methods to better match backend response structure
export const request = {
  get: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    return axiosInstance
      .get<ApiResponse<T>>(url, config)
      .then((res) => res.data.data);
  },

  post: <T = any>(
    url: string,
    data?: any,
    config?: AxiosRequestConfig
  ): Promise<T> => {
    return axiosInstance
      .post<ApiResponse<T>>(url, data, config)
      .then((res) => res.data.data);
  },

  put: <T = any>(
    url: string,
    data?: any,
    config?: AxiosRequestConfig
  ): Promise<T> => {
    return axiosInstance
      .put<ApiResponse<T>>(url, data, config)
      .then((res) => res.data.data);
  },

  delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
    return axiosInstance
      .delete<ApiResponse<T>>(url, config)
      .then((res) => res.data.data);
  },
};

export default axiosInstance;
