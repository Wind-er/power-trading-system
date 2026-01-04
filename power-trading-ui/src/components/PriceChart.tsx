import React from 'react'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import { Trade } from '../types'
import './PriceChart.css'

interface PriceChartProps {
  trades: Trade[]
  productName: string
}

const PriceChart: React.FC<PriceChartProps> = ({ trades, productName }) => {
  // 准备图表数据：按时间排序，取最近20条记录
  const chartData = trades
    .slice(0, 20)
    .reverse()
    .map((trade, index) => ({
      time: new Date(trade.tradeTime).toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      }),
      price: Number(trade.price),
      quantity: Number(trade.quantity),
      index: index + 1,
    }))

  if (chartData.length === 0) {
    return (
      <div className="price-chart-empty">
        <p>暂无交易数据</p>
      </div>
    )
  }

  return (
    <div className="price-chart">
      <h3>{productName} 价格走势</h3>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="time" />
          <YAxis 
            label={{ value: '价格 (元/MWh)', angle: -90, position: 'insideLeft' }}
          />
          <Tooltip 
            formatter={(value: number) => [`¥${value.toFixed(2)}`, '价格']}
            labelFormatter={(label) => `时间: ${label}`}
          />
          <Legend />
          <Line 
            type="monotone" 
            dataKey="price" 
            stroke="#FF8C00" 
            strokeWidth={2}
            name="成交价格"
            dot={{ r: 4 }}
            activeDot={{ r: 6 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  )
}

export default PriceChart

