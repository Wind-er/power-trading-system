import { useState, useMemo } from 'react'

export type SortDirection = 'asc' | 'desc' | null

export interface SortConfig<T> {
  key: keyof T | null
  direction: SortDirection
}

export function useTableSort<T>(data: T[], defaultSort?: SortConfig<T>) {
  const [sortConfig, setSortConfig] = useState<SortConfig<T>>(
    defaultSort || { key: null, direction: null }
  )

  const sortedData = useMemo(() => {
    if (!sortConfig.key || !sortConfig.direction) {
      return data
    }

    return [...data].sort((a, b) => {
      const aValue = a[sortConfig.key!]
      const bValue = b[sortConfig.key!]

      // 处理 null/undefined
      if (aValue == null && bValue == null) return 0
      if (aValue == null) return 1
      if (bValue == null) return -1

      // 处理数字
      if (typeof aValue === 'number' && typeof bValue === 'number') {
        return sortConfig.direction === 'asc' 
          ? aValue - bValue 
          : bValue - aValue
      }

      // 处理日期字符串
      if (typeof aValue === 'string' && typeof bValue === 'string') {
        const aDate = new Date(aValue).getTime()
        const bDate = new Date(bValue).getTime()
        if (!isNaN(aDate) && !isNaN(bDate)) {
          return sortConfig.direction === 'asc'
            ? aDate - bDate
            : bDate - aDate
        }
        // 字符串比较
        return sortConfig.direction === 'asc'
          ? aValue.localeCompare(bValue)
          : bValue.localeCompare(aValue)
      }

      // 默认字符串比较
      const aStr = String(aValue)
      const bStr = String(bValue)
      return sortConfig.direction === 'asc'
        ? aStr.localeCompare(bStr)
        : bStr.localeCompare(aStr)
    })
  }, [data, sortConfig])

  const handleSort = (key: keyof T) => {
    let direction: SortDirection = 'asc'
    
    if (sortConfig.key === key && sortConfig.direction === 'asc') {
      direction = 'desc'
    } else if (sortConfig.key === key && sortConfig.direction === 'desc') {
      direction = null
    }

    setSortConfig({ key: direction ? key : null, direction })
  }

  const getSortDirection = (key: keyof T): SortDirection => {
    if (sortConfig.key === key) {
      return sortConfig.direction
    }
    return null
  }

  return {
    sortedData,
    sortConfig,
    handleSort,
    getSortDirection,
  }
}

