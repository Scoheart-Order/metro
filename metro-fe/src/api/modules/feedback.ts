import { request } from '../request'

// Feedback interfaces
export interface FeedbackDto {
  content: string
  rating: number
}

export interface Feedback {
  id: number
  userId: number
  username: string
  content: string
  rating: number
  createdAt: string
  replies: Reply[]
}

// Request interfaces
export interface RequestDto {
  title: string
  content: string
}

export interface Request {
  id: number
  userId: number
  username: string
  title: string
  content: string
  status: 'pending' | 'processing' | 'resolved' | 'rejected'
  createdAt: string
  replies: Reply[]
}

// Reply interface
export interface Reply {
  id: number
  userId: number
  username: string
  content: string
  createdAt: string
  isAdmin: boolean
}

export interface ReplyDto {
  content: string
}

/**
 * Feedback and request API methods
 */
export const feedbackApi = {
  // Feedback methods
  getAllFeedbacks: () => {
    return request.get<Feedback[]>('/feedbacks')
  },

  getFeedbackById: (id: number) => {
    return request.get<Feedback>(`/feedbacks/${id}`)
  },

  createFeedback: (feedbackDto: FeedbackDto) => {
    return request.post<Feedback>('/feedbacks', feedbackDto)
  },
  
  getUserFeedbacks: () => {
    return request.get<Feedback[]>('/feedbacks/user')
  },
  
  deleteFeedback: (id: number) => {
    return request.delete<void>(`/feedbacks/${id}`)
  },
  
  // Request methods
  getAllRequests: () => {
    return request.get<Request[]>('/requests')
  },
  
  getRequestById: (id: number) => {
    return request.get<Request>(`/requests/${id}`)
  },
  
  createRequest: (requestDto: RequestDto) => {
    return request.post<Request>('/requests', requestDto)
  },
  
  getUserRequests: () => {
    return request.get<Request[]>('/requests/user')
  },
  
  // Reply methods
  replyToFeedback: (feedbackId: number, replyDto: ReplyDto) => {
    return request.post<Reply>(`/feedbacks/${feedbackId}/replies`, replyDto)
  },
  
  replyToRequest: (requestId: number, replyDto: ReplyDto) => {
    return request.post<Reply>(`/requests/${requestId}/replies`, replyDto)
  },
  
  // Admin actions
  updateRequestStatus: (requestId: number, status: Request['status']) => {
    return request.put<Request>(`/requests/${requestId}/status?status=${status}`, {})
  }
} 