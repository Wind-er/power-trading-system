import React, { useEffect, useState } from 'react'
import { Outlet, Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { orderApi } from '../services/api'
import { 
  FaBolt, FaHome, FaBox, FaFileAlt, FaPlusCircle, 
  FaChartLine, FaUser, FaSignOutAlt, FaIndustry,
  FaShoppingCart, FaPlug, FaBatteryHalf
} from 'react-icons/fa'
import './Layout.css'

const Layout: React.FC = () => {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [statistics, setStatistics] = useState<any>(null)

  useEffect(() => {
    if (user?.id) {
      loadStatistics()
      // 每30秒刷新一次统计数据
      const interval = setInterval(() => {
        loadStatistics()
      }, 30000)
      
      // 监听订单更新事件，立即刷新统计数据
      const handleOrderUpdate = () => {
        loadStatistics()
      }
      window.addEventListener('orderUpdated', handleOrderUpdate)
      
      return () => {
        clearInterval(interval)
        window.removeEventListener('orderUpdated', handleOrderUpdate)
      }
    }
  }, [user?.id])

  const loadStatistics = async () => {
    if (!user?.id) return
    
    try {
      const response = await orderApi.getStatistics(user.id)
      if (response.success) {
        setStatistics(response.data)
      }
    } catch (err) {
      console.error('Failed to load statistics', err)
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="layout">
      <header className="header">
        <div className="header-content">
          <h1 className="logo">
            <FaBolt className="logo-icon" />
            电力交易系统
          </h1>
          <nav className="nav">
            <Link to="/">
              <FaHome className="nav-icon" />
              首页
            </Link>
            <Link to="/products">
              <FaBox className="nav-icon" />
              商品
            </Link>
            <Link to="/orders">
              <FaFileAlt className="nav-icon" />
              订单
            </Link>
            <Link to="/orders/create" className="nav-create-order">
              <FaPlusCircle className="nav-icon" />
              发布订单
            </Link>
            <Link to="/trades">
              <FaChartLine className="nav-icon" />
              交易记录
            </Link>
            <Link to="/profile">
              <FaUser className="nav-icon" />
              个人中心
            </Link>
          </nav>
          <div className="user-info">
            <span className="username">
              {user?.userType === 'GENERATOR' ? <FaIndustry className="user-icon" /> : 
               user?.userType === 'BUYER' ? <FaShoppingCart className="user-icon" /> : 
               <FaUser className="user-icon" />}
              {user?.username}
            </span>
            <span className="user-type">
              {user?.userType === 'GENERATOR' ? '发电商' : 
               user?.userType === 'BUYER' ? '购电商' : '管理员'}
            </span>
            <button onClick={handleLogout}>
              <FaSignOutAlt className="logout-icon" />
              退出
            </button>
          </div>
        </div>
      </header>
      
      {/* 固定统计面板 */}
      {user && (user.userType === 'GENERATOR' || user.userType === 'BUYER') && statistics && (
        <div className="statistics-bar">
          <div className="statistics-content">
            {user.userType === 'GENERATOR' && (
              <>
                <div className="stat-card">
                  <div className="stat-icon-wrapper">
                    <FaBatteryHalf className="stat-icon" />
                  </div>
                  <div className="stat-label">可售电量</div>
                  <div className="stat-value highlight">
                    {statistics.availableSellQuantity?.toFixed(2) || '0.00'} <span className="stat-unit">MWh</span>
                  </div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon-wrapper">
                    <FaChartLine className="stat-icon" />
                  </div>
                  <div className="stat-label">已售电量</div>
                  <div className="stat-value">
                    {statistics.soldQuantity?.toFixed(2) || '0.00'} <span className="stat-unit">MWh</span>
                  </div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon-wrapper">
                    <FaPlug className="stat-icon" />
                  </div>
                  <div className="stat-label">总发布电量</div>
                  <div className="stat-value">
                    {statistics.totalSellQuantity?.toFixed(2) || '0.00'} <span className="stat-unit">MWh</span>
                  </div>
                </div>
                <div className="stat-card">
                  <div className="stat-icon-wrapper">
                    <FaFileAlt className="stat-icon" />
                  </div>
                  <div className="stat-label">卖出订单</div>
                  <div className="stat-value">
                    {statistics.sellOrdersCount || 0} <span className="stat-unit">个</span>
                  </div>
                </div>
              </>
            )}
            {user.userType === 'BUYER' && (
              <>
                <div className="stat-card">
                  <div className="stat-label">待采购电量</div>
                  <div className="stat-value highlight">
                    {statistics.availableBuyQuantity?.toFixed(2) || '0.00'} <span className="stat-unit">MWh</span>
                  </div>
                </div>
                <div className="stat-card">
                  <div className="stat-label">已采购电量</div>
                  <div className="stat-value">
                    {statistics.boughtQuantity?.toFixed(2) || '0.00'} <span className="stat-unit">MWh</span>
                  </div>
                </div>
                <div className="stat-card">
                  <div className="stat-label">总采购电量</div>
                  <div className="stat-value">
                    {statistics.totalBuyQuantity?.toFixed(2) || '0.00'} <span className="stat-unit">MWh</span>
                  </div>
                </div>
                <div className="stat-card">
                  <div className="stat-label">买入订单</div>
                  <div className="stat-value">
                    {statistics.buyOrdersCount || 0} <span className="stat-unit">个</span>
                  </div>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  )
}

export default Layout

