import React, { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { userApi, orderApi } from '../services/api'
import './Profile.css'

const Profile: React.FC = () => {
  const { user, login } = useAuth()
  const [isEditing, setIsEditing] = useState(false)
  const [formData, setFormData] = useState({
    companyName: user?.companyName || '',
    contactInfo: user?.contactInfo || '',
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [statistics, setStatistics] = useState<any>(null)

  const getUserTypeText = (type: string) => {
    const typeMap: Record<string, string> = {
      GENERATOR: '发电商',
      BUYER: '购电商',
      ADMIN: '管理员',
    }
    return typeMap[type] || type
  }

  const getStatusText = (status: string) => {
    return status === 'ACTIVE' ? '启用' : '禁用'
  }

  useEffect(() => {
    if (user?.id) {
      loadStatistics()
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

  const handleSave = async () => {
    if (!user?.id) return

    setError('')
    setLoading(true)

    try {
      const response = await userApi.updateProfile(user.id, formData)
      if (response.success) {
        // 更新本地用户信息
        const updatedUser = { ...user, ...response.data }
        const token = localStorage.getItem('token')
        if (token) {
          login(token, updatedUser)
        }
        setIsEditing(false)
      } else {
        setError(response.message || '更新失败')
      }
    } catch (err: any) {
      setError(err.response?.data?.message || '更新失败')
    } finally {
      setLoading(false)
    }
  }

  const handleCancel = () => {
    setFormData({
      companyName: user?.companyName || '',
      contactInfo: user?.contactInfo || '',
    })
    setIsEditing(false)
    setError('')
  }

  return (
    <div className="profile">
      <h2>个人中心</h2>
      <div className="profile-card">
        <div className="profile-item">
          <span className="label">用户名:</span>
          <span className="value">{user?.username}</span>
        </div>
        <div className="profile-item">
          <span className="label">用户类型:</span>
          <span className="value">{getUserTypeText(user?.userType || '')}</span>
        </div>
        <div className="profile-item">
          <span className="label">企业名称:</span>
          {isEditing ? (
            <input
              type="text"
              value={formData.companyName}
              onChange={(e) => setFormData({ ...formData, companyName: e.target.value })}
              className="profile-input"
            />
          ) : (
            <span className="value">{user?.companyName || '-'}</span>
          )}
        </div>
        <div className="profile-item">
          <span className="label">联系方式:</span>
          {isEditing ? (
            <input
              type="text"
              value={formData.contactInfo}
              onChange={(e) => setFormData({ ...formData, contactInfo: e.target.value })}
              className="profile-input"
            />
          ) : (
            <span className="value">{user?.contactInfo || '-'}</span>
          )}
        </div>
        <div className="profile-item">
          <span className="label">状态:</span>
          <span className={`value status ${user?.status?.toLowerCase()}`}>
            {getStatusText(user?.status || '')}
          </span>
        </div>
        
        {/* 发电商显示可售电量统计 */}
        {user?.userType === 'GENERATOR' && statistics && (
          <div className="profile-statistics">
            <h3>销售统计</h3>
            <div className="stat-item">
              <span className="stat-label">可售电量:</span>
              <span className="stat-value highlight">
                {statistics.availableSellQuantity?.toFixed(2) || '0.00'} MWh
              </span>
            </div>
            <div className="stat-item">
              <span className="stat-label">已售电量:</span>
              <span className="stat-value">
                {statistics.soldQuantity?.toFixed(2) || '0.00'} MWh
              </span>
            </div>
            <div className="stat-item">
              <span className="stat-label">总发布电量:</span>
              <span className="stat-value">
                {statistics.totalSellQuantity?.toFixed(2) || '0.00'} MWh
              </span>
            </div>
            <div className="stat-item">
              <span className="stat-label">卖出订单数:</span>
              <span className="stat-value">
                {statistics.sellOrdersCount || 0} 个
              </span>
            </div>
          </div>
        )}

        {/* 购电商显示采购统计 */}
        {user?.userType === 'BUYER' && statistics && (
          <div className="profile-statistics">
            <h3>采购统计</h3>
            <div className="stat-item">
              <span className="stat-label">待采购电量:</span>
              <span className="stat-value highlight">
                {statistics.availableBuyQuantity?.toFixed(2) || '0.00'} MWh
              </span>
            </div>
            <div className="stat-item">
              <span className="stat-label">已采购电量:</span>
              <span className="stat-value">
                {statistics.boughtQuantity?.toFixed(2) || '0.00'} MWh
              </span>
            </div>
            <div className="stat-item">
              <span className="stat-label">总采购电量:</span>
              <span className="stat-value">
                {statistics.totalBuyQuantity?.toFixed(2) || '0.00'} MWh
              </span>
            </div>
            <div className="stat-item">
              <span className="stat-label">买入订单数:</span>
              <span className="stat-value">
                {statistics.buyOrdersCount || 0} 个
              </span>
            </div>
          </div>
        )}

        {error && <div className="error-message">{error}</div>}
        <div className="profile-actions">
          {isEditing ? (
            <>
              <button onClick={handleSave} disabled={loading} className="save-button">
                {loading ? '保存中...' : '保存'}
              </button>
              <button onClick={handleCancel} className="cancel-button">
                取消
              </button>
            </>
          ) : (
            <button onClick={() => setIsEditing(true)} className="edit-button">
              编辑信息
            </button>
          )}
        </div>
      </div>
    </div>
  )
}

export default Profile

