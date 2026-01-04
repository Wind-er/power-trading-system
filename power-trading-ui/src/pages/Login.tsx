import React, { useState, useEffect } from 'react'
import { useNavigate, Link, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../services/api'
import './Login.css'

const Login: React.FC = () => {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [rememberMe, setRememberMe] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login, isAuthenticated } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  // 如果已经登录，重定向到目标页面或首页
  useEffect(() => {
    if (isAuthenticated) {
      // 优先使用 location.state 中的路径，然后是 URL 参数，最后是首页
      const fromState = (location.state as any)?.from?.pathname
      const fromQuery = new URLSearchParams(location.search).get('from')
      const from = fromState || fromQuery || '/'
      navigate(from, { replace: true })
    }
  }, [isAuthenticated, navigate, location])

  // 页面加载时从localStorage读取保存的用户名和密码
  useEffect(() => {
    const savedUsername = localStorage.getItem('saved_username')
    const savedPassword = localStorage.getItem('saved_password')
    const savedRememberMe = localStorage.getItem('remember_me') === 'true'
    
    if (savedUsername) {
      setUsername(savedUsername)
    }
    if (savedPassword && savedRememberMe) {
      // 简单解码（base64）
      try {
        const decodedPassword = atob(savedPassword)
        setPassword(decodedPassword)
      } catch (e) {
        console.error('密码解码失败', e)
      }
    }
    if (savedRememberMe) {
      setRememberMe(true)
    }
  }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const response = await authApi.login(username, password)
      if (response.success) {
        login(response.data.token, response.data.user)
        
        // 如果勾选了记住密码，保存用户名和密码
        if (rememberMe) {
          localStorage.setItem('saved_username', username)
          // 使用base64编码保存密码（简单编码，不是真正的加密）
          localStorage.setItem('saved_password', btoa(password))
          localStorage.setItem('remember_me', 'true')
        } else {
          // 如果没有勾选，清除保存的密码
          localStorage.removeItem('saved_username')
          localStorage.removeItem('saved_password')
          localStorage.removeItem('remember_me')
        }
        
        // 登录成功后，返回到之前的页面或首页
        // 优先使用 location.state 中的路径，然后是 URL 参数，最后是首页
        const fromState = (location.state as any)?.from?.pathname
        const fromQuery = new URLSearchParams(location.search).get('from')
        const from = fromState || fromQuery || '/'
        navigate(from, { replace: true })
      } else {
        setError(response.message || '登录失败')
      }
    } catch (err: any) {
      console.error('登录错误:', err)
      const errorMessage = err.response?.data?.message || err.message || '登录失败，请检查用户名和密码'
      setError(errorMessage)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <div className="login-box">
        <h2>登录</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>用户名</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>密码</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <div className="form-group remember-me">
            <label className="remember-me-label">
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={(e) => setRememberMe(e.target.checked)}
              />
              <span>记住密码</span>
            </label>
          </div>
          {error && <div className="error-message">{error}</div>}
          <button type="submit" disabled={loading}>
            {loading ? '登录中...' : '登录'}
          </button>
          <div className="register-link">
            还没有账号？<Link to="/register">立即注册</Link>
          </div>
        </form>
      </div>
    </div>
  )
}

export default Login

