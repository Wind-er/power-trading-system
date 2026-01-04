import React, { useEffect, useState } from 'react'
import { productApi } from '../services/api'
import { Product } from '../types'
import { useTableSort } from '../hooks/useTableSort'
import './Products.css'

const Products: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadProducts()
  }, [])

  const loadProducts = async () => {
    try {
      const response = await productApi.getAll()
      if (response.success) {
        setProducts(response.data)
      }
    } catch (err) {
      console.error('Failed to load products', err)
    } finally {
      setLoading(false)
    }
  }

  // 使用排序Hook
  const { sortedData, handleSort, getSortDirection } = useTableSort<Product>(
    products,
    { key: 'id', direction: 'asc' } // 默认按ID升序
  )

  if (loading) {
    return <div className="loading">加载中...</div>
  }

  return (
    <div className="products">
      <h2>商品列表</h2>
      <div className="products-table">
        <table>
          <thead>
            <tr>
              <th className="sortable" onClick={() => handleSort('id')}>
                ID
                <span className="sort-icon">
                  {getSortDirection('id') === 'asc' ? '↑' : getSortDirection('id') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th className="sortable" onClick={() => handleSort('name')}>
                商品名称
                <span className="sort-icon">
                  {getSortDirection('name') === 'asc' ? '↑' : getSortDirection('name') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th className="sortable" onClick={() => handleSort('type')}>
                类型
                <span className="sort-icon">
                  {getSortDirection('type') === 'asc' ? '↑' : getSortDirection('type') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th>单位</th>
              <th>描述</th>
              <th className="sortable" onClick={() => handleSort('status')}>
                状态
                <span className="sort-icon">
                  {getSortDirection('status') === 'asc' ? '↑' : getSortDirection('status') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            {sortedData.map((product) => (
              <tr key={product.id}>
                <td>{product.id}</td>
                <td>{product.name}</td>
                <td>{product.type}</td>
                <td>{product.unit}</td>
                <td>{product.description || '-'}</td>
                <td>
                  <span className={`status ${product.status.toLowerCase()}`}>
                    {product.status === 'ACTIVE' ? '启用' : '禁用'}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default Products

