import axios from 'axios'
import { ApiResponse, User, Product, Order, Trade, MarketQuote } from '../types'

const api = axios.create({
  baseURL: '/api', // 使用/api，vite proxy会转发到后端
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器 - 添加token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理错误
api.interceptors.response.use(
  (response) => {
    // 如果响应数据已经是 ApiResponse 格式，直接返回
    if (response.data && typeof response.data === 'object' && 'success' in response.data) {
      return response.data
    }
    // 否则包装为标准格式
    return {
      success: true,
      message: '操作成功',
      data: response.data
    }
  },
  (error) => {
    // 处理网络错误
    if (!error.response) {
      return Promise.reject({
        success: false,
        message: '网络错误，请检查网络连接',
        data: null
      })
    }

    const status = error.response.status
    const message = error.response?.data?.message || error.message || '请求失败'

    if (status === 401) {
      // Token过期或无效，清除认证信息
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      // 只在当前不在登录页时才跳转
      if (window.location.pathname !== '/login') {
        // 保存当前路径，以便登录后返回
        const currentPath = window.location.pathname + window.location.search
        window.location.href = `/login?from=${encodeURIComponent(currentPath)}`
      }
    }

    // 返回标准错误格式
    return Promise.reject({
      success: false,
      message: message,
      data: error.response?.data?.data || null,
      status: status
    })
  }
)

export const authApi = {
  login: (username: string, password: string): Promise<ApiResponse<{ token: string; user: User }>> =>
    api.post('/auth/login', { username, password }),
  
  register: (data: {
    username: string
    password: string
    userType: string
    companyName?: string
    contactInfo?: string
  }): Promise<ApiResponse<{ token: string; user: User }>> =>
    api.post('/auth/register', data),
}

export const userApi = {
  getProfile: (params?: { userId?: number; username?: string }): Promise<ApiResponse<User>> =>
    api.get('/users/profile', { params }),
  
  updateProfile: (userId: number, data: Partial<User>): Promise<ApiResponse<User>> =>
    api.put('/users/profile', data, { params: { userId } }),
}

export const productApi = {
  getAll: (): Promise<ApiResponse<Product[]>> =>
    api.get('/products'),
  
  getById: (id: number): Promise<ApiResponse<Product>> =>
    api.get(`/products/${id}`),
  
  create: (product: Partial<Product>): Promise<ApiResponse<Product>> =>
    api.post('/products', product),
  
  update: (id: number, product: Partial<Product>): Promise<ApiResponse<Product>> =>
    api.put(`/products/${id}`, product),
  
  delete: (id: number): Promise<ApiResponse<string>> =>
    api.delete(`/products/${id}`),
}

export const orderApi = {
  create: (order: Partial<Order>): Promise<ApiResponse<Order>> =>
    api.post('/orders', order),
  
  getAll: (params?: { userId?: number; productId?: number }): Promise<ApiResponse<Order[]>> =>
    api.get('/orders', { params }),
  
  getById: (id: number): Promise<ApiResponse<Order>> =>
    api.get(`/orders/${id}`),
  
  // 匹配订单：路由到 /api/orders/{id}/match，由 order-service 处理或转发到 matching-service
  match: (targetOrderId: number, order: Partial<Order>): Promise<ApiResponse<Order>> =>
    api.post(`/orders/${targetOrderId}/match`, order),
  
  update: (id: number, order: Partial<Order>): Promise<ApiResponse<Order>> =>
    api.put(`/orders/${id}`, order),
  
  getStatistics: (userId: number): Promise<ApiResponse<any>> =>
    api.get('/orders/statistics', { params: { userId } }),
  
  getAvailableQuantity: (userId: number): Promise<ApiResponse<number>> =>
    api.get('/orders/available-quantity', { params: { userId } }),
  
  cancel: (id: number): Promise<ApiResponse<string>> =>
    api.delete(`/orders/${id}`),
}

// 匹配服务 API（如果需要直接调用匹配服务）
export const matchingApi = {
  // 匹配订单：直接调用匹配服务
  match: (targetOrderId: number, order: Partial<Order>): Promise<ApiResponse<Order>> =>
    api.post('/matching/match', { targetOrderId, ...order }),
}

export const tradeApi = {
  getAll: (params?: { productId?: number; userId?: number }): Promise<ApiResponse<Trade[]>> =>
    api.get('/trades', { params }),
  
  getById: (id: number): Promise<ApiResponse<Trade>> =>
    api.get(`/trades/${id}`),
  
  getStatistics: (productId?: number): Promise<ApiResponse<any>> =>
    api.get('/trades/statistics', { params: { productId } }),
}

export const marketApi = {
  getQuote: (productId: number): Promise<ApiResponse<MarketQuote>> =>
    api.get(`/market/quote/${productId}`),
  
  getHistory: (productId: number): Promise<ApiResponse<Trade[]>> =>
    api.get(`/market/history/${productId}`),
}

export default api

