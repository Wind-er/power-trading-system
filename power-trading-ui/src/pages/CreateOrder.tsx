import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { orderApi, productApi } from '../services/api'
import { Product } from '../types'
import './CreateOrder.css'

const CreateOrder: React.FC = () => {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [products, setProducts] = useState<Product[]>([])
  const [formData, setFormData] = useState({
    productId: '',
    orderType: user?.userType === 'GENERATOR' ? 'SELL' : 'BUY',
    quantity: '',
    price: '',
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    loadProducts()
  }, [])

  const loadProducts = async () => {
    try {
      const response = await productApi.getAll()
      if (response.success) {
        const activeProducts = response.data.filter(p => p.status === 'ACTIVE')
        setProducts(activeProducts)
        if (activeProducts.length > 0 && !formData.productId) {
          setFormData({ ...formData, productId: activeProducts[0].id.toString() })
        }
      }
    } catch (err) {
      console.error('Failed to load products', err)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')

    if (!formData.productId || !formData.quantity || !formData.price) {
      setError('请填写所有必填项')
      return
    }

    setLoading(true)

    try {
      const orderData = {
        userId: user?.id,
        productId: parseInt(formData.productId),
        orderType: formData.orderType as 'BUY' | 'SELL',
        quantity: parseFloat(formData.quantity),
        price: parseFloat(formData.price),
      }

      const response = await orderApi.create(orderData)
      if (response.success) {
        // 通知Layout组件刷新统计数据
        window.dispatchEvent(new Event('orderUpdated'))
        navigate('/orders')
      } else {
        setError(response.message || '创建订单失败')
      }
    } catch (err: any) {
      setError(err.response?.data?.message || '创建订单失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="create-order">
      <h2>发布订单</h2>
      <div className="order-form-container">
        <form onSubmit={handleSubmit} className="order-form">
          <div className="form-group">
            <label>商品</label>
            <select
              value={formData.productId}
              onChange={(e) => setFormData({ ...formData, productId: e.target.value })}
              required
            >
              <option value="">请选择商品</option>
              {products.map((product) => (
                <option key={product.id} value={product.id}>
                  {product.name} ({product.type})
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>订单类型</label>
            <select
              value={formData.orderType}
              onChange={(e) => setFormData({ ...formData, orderType: e.target.value })}
              required
            >
              <option value="BUY">买入</option>
              <option value="SELL">卖出</option>
            </select>
          </div>

          <div className="form-group">
            <label>电量 (MWh)</label>
            <input
              type="number"
              step="0.01"
              min="0.01"
              value={formData.quantity}
              onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label>价格 (元/MWh)</label>
            <input
              type="number"
              step="0.01"
              min="0.01"
              value={formData.price}
              onChange={(e) => setFormData({ ...formData, price: e.target.value })}
              required
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <div className="form-actions">
            <button type="submit" disabled={loading} className="submit-button">
              {loading ? '提交中...' : '发布订单'}
            </button>
            <button
              type="button"
              onClick={() => navigate('/orders')}
              className="cancel-button"
            >
              取消
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default CreateOrder

