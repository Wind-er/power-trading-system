import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { productApi, marketApi } from '../services/api'
import { Product, MarketQuote, Trade } from '../types'
import PriceChart from '../components/PriceChart'
import { FaBolt, FaPlusCircle, FaChartLine, FaArrowUp, FaArrowDown, FaDollarSign, FaBatteryHalf } from 'react-icons/fa'
import './Home.css'

const Home: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([])
  const [quotes, setQuotes] = useState<Record<number, MarketQuote>>({})
  const [trades, setTrades] = useState<Record<number, Trade[]>>({})
  const [loading, setLoading] = useState(true)
  const [selectedProduct, setSelectedProduct] = useState<number | null>(null)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const productsResponse = await productApi.getAll()
      if (productsResponse.success) {
        const activeProducts = productsResponse.data.filter(p => p.status === 'ACTIVE')
        setProducts(activeProducts)
        
        // 加载每个商品的行情和历史交易
        const quotesData: Record<number, MarketQuote> = {}
        const tradesData: Record<number, Trade[]> = {}
        for (const product of activeProducts) {
          try {
            const quoteResponse = await marketApi.getQuote(product.id)
            if (quoteResponse.success) {
              quotesData[product.id] = quoteResponse.data
            }
            
            const historyResponse = await marketApi.getHistory(product.id)
            if (historyResponse.success) {
              tradesData[product.id] = historyResponse.data
            }
          } catch (err) {
            console.error(`Failed to load data for product ${product.id}`, err)
          }
        }
        setQuotes(quotesData)
        setTrades(tradesData)
        if (activeProducts.length > 0) {
          setSelectedProduct(activeProducts[0].id)
        }
      }
    } catch (err) {
      console.error('Failed to load data', err)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <div className="loading">加载中...</div>
  }

  return (
    <div className="home">
      <div className="home-header">
        <h2>
          <FaChartLine className="header-icon" />
          市场行情
        </h2>
        <Link to="/orders/create" className="home-create-button">
          <FaPlusCircle />
          发布订单
        </Link>
      </div>
      <div className="products-grid">
        {products.map((product) => {
          const quote = quotes[product.id]
          return (
            <div key={product.id} className="product-card">
              <div className="product-card-header">
                <FaBolt className="product-icon" />
                <div>
                  <h3>{product.name}</h3>
                  <p className="product-type">{product.type}</p>
                </div>
              </div>
              {quote ? (
                <div className="quote-info">
                  <div className="quote-item highlight">
                    <span className="label">
                      <FaDollarSign className="quote-icon" />
                      最新价:
                    </span>
                    <span className="value price">¥{quote.latestPrice.toFixed(2)}</span>
                  </div>
                  <div className="quote-item">
                    <span className="label">
                      <FaArrowUp className="quote-icon up" />
                      最高价:
                    </span>
                    <span className="value">¥{quote.maxPrice.toFixed(2)}</span>
                  </div>
                  <div className="quote-item">
                    <span className="label">
                      <FaArrowDown className="quote-icon down" />
                      最低价:
                    </span>
                    <span className="value">¥{quote.minPrice.toFixed(2)}</span>
                  </div>
                  <div className="quote-item">
                    <span className="label">
                      <FaBatteryHalf className="quote-icon" />
                      成交量:
                    </span>
                    <span className="value">{quote.volume.toFixed(2)} MWh</span>
                  </div>
                  <div className="quote-item">
                    <span className="label">
                      <FaDollarSign className="quote-icon" />
                      成交额:
                    </span>
                    <span className="value">¥{quote.amount.toFixed(2)}</span>
                  </div>
                </div>
              ) : (
                <div className="no-data">暂无交易数据</div>
              )}
              <button 
                className="view-chart-button"
                onClick={() => setSelectedProduct(product.id)}
              >
                <FaChartLine />
                查看走势图
              </button>
            </div>
          )
        })}
      </div>
      {selectedProduct && trades[selectedProduct] && (
        <PriceChart 
          trades={trades[selectedProduct]} 
          productName={products.find(p => p.id === selectedProduct)?.name || ''}
        />
      )}
    </div>
  )
}

export default Home

