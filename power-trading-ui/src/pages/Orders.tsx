import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { orderApi, productApi } from '../services/api'
import { Order, Product } from '../types'
import ToastContainer from '../components/ToastContainer'
import { ToastType } from '../components/Toast'
import { useTableSort } from '../hooks/useTableSort'
import { FaFileAlt } from 'react-icons/fa'
import './Orders.css'

const Orders: React.FC = () => {
  const { user } = useAuth()
  const [orders, setOrders] = useState<Order[]>([])
  const [filteredOrders, setFilteredOrders] = useState<Order[]>([])
  const [products, setProducts] = useState<Record<number, Product>>({})
  const [loading, setLoading] = useState(true)
  const [viewMode, setViewMode] = useState<'my' | 'market'>('my') // 新增：视图模式
  const [filters, setFilters] = useState({
    status: '',
    orderType: '',
    productId: '',
  })
  const [matchingOrder, setMatchingOrder] = useState<Order | null>(null) // 要撮合的订单
  const [matchForm, setMatchForm] = useState({
    quantity: '',
    price: '',
  })
  const [matchLoading, setMatchLoading] = useState(false)
  const [matchError, setMatchError] = useState('')
  const [toasts, setToasts] = useState<Array<{ id: string; message: string; type: ToastType; duration?: number }>>([])

  useEffect(() => {
    // 等待user加载完成后再加载数据
    if (user?.id || viewMode === 'market') {
      loadData()
    } else {
      setLoading(false)
    }
  }, [user?.id, viewMode])

  // 使用排序Hook
  const { sortedData, handleSort, getSortDirection } = useTableSort<Order>(
    orders,
    { key: 'createTime', direction: 'desc' } // 默认按创建时间降序
  )

  useEffect(() => {
    applyFilters()
  }, [sortedData, filters])

  const applyFilters = () => {
    let filtered = [...sortedData]

    // 按状态筛选
    if (filters.status) {
      filtered = filtered.filter(order => order.status === filters.status)
    }

    // 按类型筛选
    if (filters.orderType) {
      filtered = filtered.filter(order => order.orderType === filters.orderType)
    }

    // 按商品筛选
    if (filters.productId) {
      filtered = filtered.filter(order => order.productId === parseInt(filters.productId))
    }

    setFilteredOrders(filtered)
  }

  const loadData = async () => {
    setLoading(true)
    try {
      let ordersResponse
      
      if (viewMode === 'my') {
        // 我的订单：只加载当前用户的订单
        if (!user?.id) {
          console.warn('用户ID不存在，无法加载我的订单')
          setLoading(false)
          return
        }
        console.log('加载我的订单，用户ID:', user.id)
        ordersResponse = await orderApi.getAll({ userId: user.id })
      } else {
        // 市场订单：加载所有待撮合的订单
        console.log('加载市场订单（所有待撮合订单）')
        ordersResponse = await orderApi.getAll()
        // 只显示待撮合和部分成交的订单，并且根据用户类型过滤
        if (ordersResponse.success && ordersResponse.data) {
          let marketOrders = ordersResponse.data.filter(
            order => order.status === 'PENDING' || order.status === 'PARTIAL'
          )
          
          // 如果是购电商，显示所有卖出订单（可以买入）
          // 如果是发电商，显示所有买入订单（可以卖出）
          if (user?.userType === 'BUYER') {
            marketOrders = marketOrders.filter(order => order.orderType === 'SELL')
          } else if (user?.userType === 'GENERATOR') {
            marketOrders = marketOrders.filter(order => order.orderType === 'BUY')
          }
          
          ordersResponse.data = marketOrders
        }
      }
      
      console.log('订单API响应:', ordersResponse)
      if (ordersResponse.success) {
        // 确保按创建时间降序排列（最新的在前）
        const sortedOrders = (ordersResponse.data || []).sort((a, b) => {
          const timeA = new Date(a.createTime).getTime()
          const timeB = new Date(b.createTime).getTime()
          return timeB - timeA // 降序：最新的在前
        })
        setOrders(sortedOrders)
        console.log('订单数量:', sortedOrders.length)
        
        // 加载商品信息
        const productsResponse = await productApi.getAll()
        if (productsResponse.success) {
          const productsMap: Record<number, Product> = {}
          productsResponse.data.forEach(p => {
            productsMap[p.id] = p
          })
          setProducts(productsMap)
        }
      } else {
        console.error('订单API返回失败:', ordersResponse.message)
      }
    } catch (err: any) {
      console.error('Failed to load orders', err)
      console.error('错误详情:', err.response?.data || err.message)
    } finally {
      setLoading(false)
    }
  }

  const showToast = (message: string, type: ToastType = 'info', duration?: number) => {
    const id = Date.now().toString() + Math.random().toString(36).substr(2, 9)
    setToasts((prev) => [...prev, { id, message, type, duration }])
  }

  const removeToast = (id: string) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id))
  }

  const handleCancel = async (id: number) => {
    if (!window.confirm('确定要撤销此订单吗？')) {
      return
    }

    try {
      await orderApi.cancel(id)
      showToast('订单已成功撤销', 'success')
      loadData()
      // 通知Layout组件刷新统计数据
      window.dispatchEvent(new Event('orderUpdated'))
    } catch (err: any) {
      showToast(err.response?.data?.message || '撤销订单失败', 'error')
    }
  }

  const handleMatch = (order: Order) => {
    // 设置要撮合的订单，并预填充表单
    setMatchingOrder(order)
    // 如果是买家撮合卖出订单，使用卖出订单的价格
    // 如果是卖家撮合买入订单，使用买入订单的价格
    setMatchForm({
      quantity: order.remainingQuantity.toFixed(2),
      price: order.price.toFixed(2),
    })
    setMatchError('')
  }

  const handleMatchSubmit = async () => {
    if (!matchingOrder || !user) {
      return
    }

    setMatchError('')
    setMatchLoading(true)

    try {
      // 确定订单类型（与目标订单相反）
      const orderType: 'BUY' | 'SELL' = matchingOrder.orderType === 'BUY' ? 'SELL' : 'BUY'
      
      // 创建撮合订单
      const newOrder = {
        userId: user.id,
        productId: matchingOrder.productId,
        orderType: orderType,
        quantity: parseFloat(matchForm.quantity),
        price: parseFloat(matchForm.price),
      }

      const response = await orderApi.match(matchingOrder.id, newOrder)
      if (response.success) {
        // 显示成功提示
        const matchedQuantity = Math.min(parseFloat(matchForm.quantity), matchingOrder.remainingQuantity)
        showToast(
          `撮合成功！已匹配 ${matchedQuantity.toFixed(2)} MWh，成交价 ¥${matchingOrder.price.toFixed(2)}/MWh。订单已创建并自动匹配。`,
          'success',
          5000
        )
        setMatchingOrder(null)
        loadData() // 重新加载数据
        // 通知Layout组件刷新统计数据
        window.dispatchEvent(new Event('orderUpdated'))
      } else {
        setMatchError(response.message || '撮合失败')
        showToast(response.message || '撮合失败，请检查价格和数量', 'error')
      }
    } catch (err: any) {
      const errorMsg = err.response?.data?.message || '撮合失败，请检查价格和数量'
      setMatchError(errorMsg)
      showToast(errorMsg, 'error')
    } finally {
      setMatchLoading(false)
    }
  }

  const getStatusText = (status: string) => {
    const statusMap: Record<string, string> = {
      PENDING: '待撮合',
      PARTIAL: '部分成交',
      FILLED: '已成交',
      CANCELLED: '已取消',
    }
    return statusMap[status] || status
  }

  const getStatusClass = (status: string) => {
    const classMap: Record<string, string> = {
      PENDING: 'pending',
      PARTIAL: 'partial',
      FILLED: 'filled',
      CANCELLED: 'cancelled',
    }
    return classMap[status] || ''
  }

  if (loading) {
    return <div className="loading">加载中...</div>
  }

  return (
    <div className="orders">
      <div className="orders-header">
        <h2>
          <FaFileAlt className="page-icon" />
          {viewMode === 'my' ? '我的订单' : '市场订单'}
        </h2>
        <div className="header-actions">
          <div className="view-mode-toggle">
            <button
              className={viewMode === 'my' ? 'active' : ''}
              onClick={() => setViewMode('my')}
            >
              我的订单
            </button>
            <button
              className={viewMode === 'market' ? 'active' : ''}
              onClick={() => setViewMode('market')}
            >
              市场订单
            </button>
          </div>
          <Link to="/orders/create" className="create-button">
            发布订单
          </Link>
        </div>
      </div>
      <div className="orders-filters">
        <div className="filter-group">
          <label>订单状态:</label>
          <select
            value={filters.status}
            onChange={(e) => setFilters({ ...filters, status: e.target.value })}
          >
            <option value="">全部</option>
            <option value="PENDING">待撮合</option>
            <option value="PARTIAL">部分成交</option>
            <option value="FILLED">已成交</option>
            <option value="CANCELLED">已取消</option>
          </select>
        </div>
        <div className="filter-group">
          <label>订单类型:</label>
          <select
            value={filters.orderType}
            onChange={(e) => setFilters({ ...filters, orderType: e.target.value })}
          >
            <option value="">全部</option>
            <option value="BUY">买入</option>
            <option value="SELL">卖出</option>
          </select>
        </div>
        <div className="filter-group">
          <label>商品:</label>
          <select
            value={filters.productId}
            onChange={(e) => setFilters({ ...filters, productId: e.target.value })}
          >
            <option value="">全部</option>
            {Object.values(products).map(product => (
              <option key={product.id} value={product.id.toString()}>
                {product.name}
              </option>
            ))}
          </select>
        </div>
      </div>
      <div className="orders-table">
        <table>
          <thead>
            <tr>
              <th className="sortable" onClick={() => handleSort('id')}>
                订单ID
                <span className="sort-icon">
                  {getSortDirection('id') === 'asc' ? '↑' : getSortDirection('id') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th>商品</th>
              <th className="sortable" onClick={() => handleSort('orderType')}>
                类型
                <span className="sort-icon">
                  {getSortDirection('orderType') === 'asc' ? '↑' : getSortDirection('orderType') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th className="sortable" onClick={() => handleSort('quantity')}>
                电量 (MWh)
                <span className="sort-icon">
                  {getSortDirection('quantity') === 'asc' ? '↑' : getSortDirection('quantity') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th className="sortable" onClick={() => handleSort('price')}>
                价格 (元/MWh)
                <span className="sort-icon">
                  {getSortDirection('price') === 'asc' ? '↑' : getSortDirection('price') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th className="sortable" onClick={() => handleSort('remainingQuantity')}>
                剩余电量
                <span className="sort-icon">
                  {getSortDirection('remainingQuantity') === 'asc' ? '↑' : getSortDirection('remainingQuantity') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th className="sortable" onClick={() => handleSort('status')}>
                状态
                <span className="sort-icon">
                  {getSortDirection('status') === 'asc' ? '↑' : getSortDirection('status') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th className="sortable" onClick={() => handleSort('createTime')}>
                创建时间
                <span className="sort-icon">
                  {getSortDirection('createTime') === 'asc' ? '↑' : getSortDirection('createTime') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {filteredOrders.map((order) => (
              <tr key={order.id}>
                <td>{order.id}</td>
                <td>{products[order.productId]?.name || '-'}</td>
                <td>
                  <span className={`order-type ${order.orderType.toLowerCase()}`}>
                    {order.orderType === 'BUY' ? '买入' : '卖出'}
                  </span>
                </td>
                <td>{order.quantity.toFixed(2)}</td>
                <td>¥{order.price.toFixed(2)}</td>
                <td>{order.remainingQuantity.toFixed(2)}</td>
                <td>
                  <span className={`status ${getStatusClass(order.status)}`}>
                    {getStatusText(order.status)}
                  </span>
                </td>
                <td>{new Date(order.createTime).toLocaleString()}</td>
                <td>
                  {viewMode === 'my' && order.status === 'PENDING' && (
                    <button
                      className="cancel-button"
                      onClick={() => handleCancel(order.id)}
                    >
                      撤销
                    </button>
                  )}
                  {viewMode === 'market' && (
                    <button
                      className="match-button"
                      onClick={() => handleMatch(order)}
                      disabled={order.userId === user?.id}
                    >
                      {order.userId === user?.id ? '我的订单' : '撮合'}
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {filteredOrders.length === 0 && orders.length > 0 && (
          <div className="empty-state">没有符合条件的订单</div>
        )}
        {orders.length === 0 && (
          <div className="empty-state">
            {viewMode === 'my' ? '暂无订单' : '暂无市场订单'}
            {viewMode === 'my' && (
              <div style={{ marginTop: '8px' }}>
                去 <Link to="/orders/create">发布订单</Link> 吧！
              </div>
            )}
          </div>
        )}
      </div>

      {/* 撮合对话框 */}
      {matchingOrder && (
        <div className="modal-overlay" onClick={() => setMatchingOrder(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>撮合订单</h3>
            <div className="match-order-info">
              <p><strong>目标订单ID:</strong> {matchingOrder.id}</p>
              <p><strong>商品:</strong> {products[matchingOrder.productId]?.name || '-'}</p>
              <p><strong>类型:</strong> {matchingOrder.orderType === 'BUY' ? '买入' : '卖出'}</p>
              <p><strong>目标价格:</strong> ¥{matchingOrder.price.toFixed(2)}/MWh</p>
              <p><strong>可撮合电量:</strong> {matchingOrder.remainingQuantity.toFixed(2)} MWh</p>
            </div>
            <div className="form-group">
              <label>您的{user?.userType === 'BUYER' ? '买入' : '卖出'}价格 (元/MWh)</label>
              <input
                type="number"
                step="0.01"
                value={matchForm.price}
                onChange={(e) => setMatchForm({ ...matchForm, price: e.target.value })}
                placeholder="请输入价格"
                required
              />
              <small>
                {matchingOrder.orderType === 'SELL' 
                  ? '您的买入价需 ≥ 卖出价 ¥' + matchingOrder.price.toFixed(2)
                  : '您的卖出价需 ≤ 买入价 ¥' + matchingOrder.price.toFixed(2)}
              </small>
            </div>
            <div className="form-group">
              <label>撮合电量 (MWh)</label>
              <input
                type="number"
                step="0.01"
                value={matchForm.quantity}
                onChange={(e) => setMatchForm({ ...matchForm, quantity: e.target.value })}
                placeholder="请输入电量"
                required
                max={matchingOrder.remainingQuantity}
              />
              <small>最多可撮合 {matchingOrder.remainingQuantity.toFixed(2)} MWh</small>
            </div>
            {matchError && <div className="error-message">{matchError}</div>}
            <div className="modal-actions">
              <button 
                onClick={handleMatchSubmit} 
                disabled={matchLoading || !matchForm.price || !matchForm.quantity}
                className="match-submit-button"
              >
                {matchLoading ? '撮合中...' : '确认撮合'}
              </button>
              <button 
                onClick={() => setMatchingOrder(null)} 
                disabled={matchLoading}
                className="cancel-button"
              >
                取消
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Toast通知容器 */}
      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </div>
  )
}

export default Orders

