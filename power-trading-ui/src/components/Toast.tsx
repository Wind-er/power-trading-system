import React, { useEffect } from 'react'
import './Toast.css'

export type ToastType = 'success' | 'error' | 'warning' | 'info'

interface ToastProps {
  message: string
  type?: ToastType
  duration?: number
  onClose: () => void
}

const Toast: React.FC<ToastProps> = ({ message, type = 'info', duration = 3000, onClose }) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose()
    }, duration)

    return () => clearTimeout(timer)
  }, [duration, onClose])

  const getIcon = () => {
    switch (type) {
      case 'success':
        return '✓'
      case 'error':
        return '✕'
      case 'warning':
        return '⚠'
      case 'info':
        return 'ℹ'
      default:
        return 'ℹ'
    }
  }

  const getTitle = () => {
    switch (type) {
      case 'success':
        return '成功'
      case 'error':
        return '错误'
      case 'warning':
        return '警告'
      case 'info':
        return '提示'
      default:
        return '提示'
    }
  }

  return (
    <div className={`toast toast-${type}`}>
      <div className="toast-content">
        <div className="toast-icon-wrapper">
          <span className="toast-icon">{getIcon()}</span>
        </div>
        <div className="toast-text">
          <div className="toast-title">{getTitle()}</div>
          <div className="toast-message">{message}</div>
        </div>
        <button className="toast-close" onClick={onClose} aria-label="关闭">×</button>
      </div>
      <div className="toast-progress">
        <div 
          className="toast-progress-bar" 
          style={{ 
            animationDuration: `${duration}ms`,
            animationName: 'toast-progress-animation'
          }}
        />
      </div>
    </div>
  )
}

export default Toast

