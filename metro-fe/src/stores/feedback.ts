import { defineStore } from 'pinia'
import { feedbackApi, type Feedback, type Request, type FeedbackDto, type RequestDto, type ReplyDto } from '../api'

interface FeedbackState {
  feedbacks: Feedback[]
  requests: Request[]
  userFeedbacks: Feedback[]
  userRequests: Request[]
  loading: boolean
  error: string | null
}

export const useFeedbackStore = defineStore('feedback', {
  state: (): FeedbackState => ({
    feedbacks: [],
    requests: [],
    userFeedbacks: [],
    userRequests: [],
    loading: false,
    error: null
  }),
  
  getters: {
    getFeedbackById: (state) => (id: number) => {
      return state.feedbacks.find(feedback => feedback.id === id)
    },
    getRequestById: (state) => (id: number) => {
      return state.requests.find(request => request.id === id)
    }
  },
  
  actions: {
    // Public feedbacks
    async fetchFeedbacks() {
      this.loading = true
      this.error = null
      
      try {
        this.feedbacks = await feedbackApi.getAllFeedbacks()
        return true
      } catch (error) {
        console.error('Fetch feedbacks error:', error)
        this.error = 'Failed to fetch feedbacks'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async createFeedback(feedbackData: FeedbackDto) {
      this.loading = true
      this.error = null
      
      try {
        const newFeedback = await feedbackApi.createFeedback(feedbackData)
        this.feedbacks.push(newFeedback)
        this.userFeedbacks.push(newFeedback)
        return true
      } catch (error) {
        console.error('Create feedback error:', error)
        this.error = 'Failed to create feedback'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async deleteFeedback(id: number) {
      this.loading = true
      this.error = null
      
      try {
        await feedbackApi.deleteFeedback(id)
        
        // Remove from feedbacks array
        const feedbackIndex = this.feedbacks.findIndex(feedback => feedback.id === id)
        if (feedbackIndex !== -1) {
          this.feedbacks.splice(feedbackIndex, 1)
        }
        
        // Also remove from userFeedbacks if present
        const userFeedbackIndex = this.userFeedbacks.findIndex(feedback => feedback.id === id)
        if (userFeedbackIndex !== -1) {
          this.userFeedbacks.splice(userFeedbackIndex, 1)
        }
        
        return true
      } catch (error) {
        console.error('Delete feedback error:', error)
        this.error = 'Failed to delete feedback'
        return false
      } finally {
        this.loading = false
      }
    },
    
    // User's own feedbacks
    async fetchUserFeedbacks() {
      this.loading = true
      this.error = null
      
      try {
        this.userFeedbacks = await feedbackApi.getUserFeedbacks()
        return true
      } catch (error) {
        console.error('Fetch user feedbacks error:', error)
        this.error = 'Failed to fetch your feedbacks'
        return false
      } finally {
        this.loading = false
      }
    },
    
    // Requests (requirements)
    async fetchRequests() {
      this.loading = true
      this.error = null
      
      try {
        this.requests = await feedbackApi.getAllRequests()
        return true
      } catch (error) {
        console.error('Fetch requests error:', error)
        this.error = 'Failed to fetch requests'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async createRequest(requestData: RequestDto) {
      this.loading = true
      this.error = null
      
      try {
        const newRequest = await feedbackApi.createRequest(requestData)
        this.requests.push(newRequest)
        this.userRequests.push(newRequest)
        return true
      } catch (error) {
        console.error('Create request error:', error)
        this.error = 'Failed to create request'
        return false
      } finally {
        this.loading = false
      }
    },
    
    // User's own requests
    async fetchUserRequests() {
      this.loading = true
      this.error = null
      
      try {
        this.userRequests = await feedbackApi.getUserRequests()
        return true
      } catch (error) {
        console.error('Fetch user requests error:', error)
        this.error = 'Failed to fetch your requests'
        return false
      } finally {
        this.loading = false
      }
    },
    
    // Reply actions
    async replyToFeedback(feedbackId: number, content: string) {
      this.loading = true
      this.error = null
      
      try {
        const replyDto: ReplyDto = { content }
        const newReply = await feedbackApi.replyToFeedback(feedbackId, replyDto)
        
        // Update the feedback in the store
        const feedback = this.feedbacks.find(f => f.id === feedbackId)
        if (feedback) {
          feedback.replies.push(newReply)
        }
        
        // Also update in userFeedbacks if present
        const userFeedback = this.userFeedbacks.find(f => f.id === feedbackId)
        if (userFeedback) {
          userFeedback.replies.push(newReply)
        }
        
        return true
      } catch (error) {
        console.error('Reply to feedback error:', error)
        this.error = 'Failed to reply to feedback'
        return false
      } finally {
        this.loading = false
      }
    },
    
    async replyToRequest(requestId: number, content: string) {
      this.loading = true
      this.error = null
      
      try {
        const replyDto: ReplyDto = { content }
        const newReply = await feedbackApi.replyToRequest(requestId, replyDto)
        
        // Update the request in the store
        const request = this.requests.find(r => r.id === requestId)
        if (request) {
          request.replies.push(newReply)
        }
        
        // Also update in userRequests if present
        const userRequest = this.userRequests.find(r => r.id === requestId)
        if (userRequest) {
          userRequest.replies.push(newReply)
        }
        
        return true
      } catch (error) {
        console.error('Reply to request error:', error)
        this.error = 'Failed to reply to request'
        return false
      } finally {
        this.loading = false
      }
    },
    
    // Admin actions
    async updateRequestStatus(requestId: number, status: Request['status']) {
      this.loading = true
      this.error = null
      
      try {
        const updatedRequest = await feedbackApi.updateRequestStatus(requestId, status)
        
        // Update the request in the store
        const requestIndex = this.requests.findIndex(r => r.id === requestId)
        if (requestIndex !== -1) {
          this.requests[requestIndex] = updatedRequest
        }
        
        // Also update in userRequests if present
        const userRequestIndex = this.userRequests.findIndex(r => r.id === requestId)
        if (userRequestIndex !== -1) {
          this.userRequests[userRequestIndex] = updatedRequest
        }
        
        return true
      } catch (error) {
        console.error('Update request status error:', error)
        this.error = 'Failed to update request status'
        return false
      } finally {
        this.loading = false
      }
    }
  }
}) 