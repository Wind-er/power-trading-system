export interface User {
  id: number
  username: string
  userType: 'GENERATOR' | 'BUYER' | 'ADMIN'
  companyName?: string
  contactInfo?: string
  status: 'ACTIVE' | 'INACTIVE'
}

export interface Product {
  id: number
  name: string
  type: string
  unit: string
  description?: string
  status: 'ACTIVE' | 'INACTIVE'
}

export interface Order {
  id: number
  userId: number
  productId: number
  orderType: 'BUY' | 'SELL'
  quantity: number
  price: number
  status: 'PENDING' | 'PARTIAL' | 'FILLED' | 'CANCELLED'
  remainingQuantity: number
  createTime: string
  updateTime: string
}

export interface Trade {
  id: number
  buyOrderId: number
  sellOrderId: number
  productId: number
  quantity: number
  price: number
  buyerId: number
  sellerId: number
  tradeTime: string
}

export interface MarketQuote {
  latestPrice: number
  maxPrice: number
  minPrice: number
  volume: number
  amount: number
  changePercent: number
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

