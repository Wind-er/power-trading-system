import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../services/api'
import './Register.css'

const Register: React.FC = () => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    userType: 'BUYER',
    companyName: '',
    contactInfo: '',
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const response = await authApi.register(formData)
      if (response.success) {
        login(response.data.token, response.data.user)
        navigate('/')
      } else {
        setError(response.message || '注册失败')
      }
    } catch (err: any) {
      setError(err.response?.data?.message || '注册失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="register-container">
      <div className="register-box">
        <h2>注册</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>用户名</label>
            <input
              type="text"
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>密码</label>
            <input
              type="password"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>用户类型</label>
            <select
              value={formData.userType}
              onChange={(e) => setFormData({ ...formData, userType: e.target.value })}
              required
            >
              <option value="BUYER">购电商</option>
              <option value="GENERATOR">发电商</option>
            </select>
          </div>
          <div className="form-group">
            <label>企业名称</label>
            <input
              type="text"
              value={formData.companyName}
              onChange={(e) => setFormData({ ...formData, companyName: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>联系方式</label>
            <input
              type="text"
              value={formData.contactInfo}
              onChange={(e) => setFormData({ ...formData, contactInfo: e.target.value })}
            />
          </div>
          {error && <div className="error-message">{error}</div>}
          <button type="submit" disabled={loading}>
            {loading ? '注册中...' : '注册'}
          </button>
          <div className="login-link">
            已有账号？<Link to="/login">立即登录</Link>
          </div>
        </form>
      </div>
    </div>
  )
}

export default Register

