import React, { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { tradeApi, productApi } from '../services/api'
import { Trade, Product } from '../types'
import { useTableSort } from '../hooks/useTableSort'
import './Trades.css'

const Trades: React.FC = () => {
  const { user } = useAuth()
  const [trades, setTrades] = useState<Trade[]>([])
  const [products, setProducts] = useState<Record<number, Product>>({})
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const tradesResponse = await tradeApi.getAll({ userId: user?.id })
      if (tradesResponse.success) {
        setTrades(tradesResponse.data)
        
        // 加载商品信息
        const productsResponse = await productApi.getAll()
        if (productsResponse.success) {
          const productsMap: Record<number, Product> = {}
          productsResponse.data.forEach(p => {
            productsMap[p.id] = p
          })
          setProducts(productsMap)
        }
      }
    } catch (err) {
      console.error('Failed to load trades', err)
    } finally {
      setLoading(false)
    }
  }

  // 使用排序Hook
  const { sortedData, handleSort, getSortDirection } = useTableSort<Trade>(
    trades,
    { key: 'tradeTime', direction: 'desc' } // 默认按成交时间降序
  )

  if (loading) {
    return <div className="loading">加载中...</div>
  }

  return (
    <div className="trades">
      <h2>交易记录</h2>
      <div className="trades-table">
        <table>
          <thead>
            <tr>
              <th className="sortable" onClick={() => handleSort('id')}>
                交易ID
                <span className="sort-icon">
                  {getSortDirection('id') === 'asc' ? '↑' : getSortDirection('id') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th>商品</th>
              <th className="sortable" onClick={() => handleSort('quantity')}>
                成交电量 (MWh)
                <span className="sort-icon">
                  {getSortDirection('quantity') === 'asc' ? '↑' : getSortDirection('quantity') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th className="sortable" onClick={() => handleSort('price')}>
                成交价格 (元/MWh)
                <span className="sort-icon">
                  {getSortDirection('price') === 'asc' ? '↑' : getSortDirection('price') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
              <th>成交额 (元)</th>
              <th>角色</th>
              <th className="sortable" onClick={() => handleSort('tradeTime')}>
                成交时间
                <span className="sort-icon">
                  {getSortDirection('tradeTime') === 'asc' ? '↑' : getSortDirection('tradeTime') === 'desc' ? '↓' : '⇅'}
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            {sortedData.map((trade) => {
              const isBuyer = trade.buyerId === user?.id
              const role = isBuyer ? '买方' : '卖方'
              const amount = trade.quantity * trade.price
              
              return (
                <tr key={trade.id}>
                  <td>{trade.id}</td>
                  <td>{products[trade.productId]?.name || '-'}</td>
                  <td>{trade.quantity.toFixed(2)}</td>
                  <td>¥{trade.price.toFixed(2)}</td>
                  <td>¥{amount.toFixed(2)}</td>
                  <td>
                    <span className={`role ${isBuyer ? 'buyer' : 'seller'}`}>
                      {role}
                    </span>
                  </td>
                  <td>{new Date(trade.tradeTime).toLocaleString()}</td>
                </tr>
              )
            })}
          </tbody>
        </table>
        {trades.length === 0 && (
          <div className="empty-state">暂无交易记录</div>
        )}
      </div>
    </div>
  )
}

export default Trades

