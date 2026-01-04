import React from 'react'
import Toast, { ToastType } from './Toast'
import './ToastContainer.css'

interface ToastItem {
  id: string
  message: string
  type: ToastType
  duration?: number
}

interface ToastContainerProps {
  toasts: ToastItem[]
  onRemove: (id: string) => void
}

const ToastContainer: React.FC<ToastContainerProps> = ({ toasts, onRemove }) => {
  if (toasts.length === 0) return null

  return (
    <div className="toast-container">
      {toasts.map((toast) => (
        <Toast
          key={toast.id}
          message={toast.message}
          type={toast.type}
          duration={toast.duration}
          onClose={() => onRemove(toast.id)}
        />
      ))}
    </div>
  )
}

export default ToastContainer

