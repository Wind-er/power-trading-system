# 微服务架构集成说明

## 概述

前端已适配微服务架构，通过 API 网关（Gateway Service）统一访问所有后端微服务。

## 架构说明

### 服务端口

- **前端开发服务器**: `http://localhost:3000`
- **API 网关**: `http://localhost:8000`
- **微服务**:
  - auth-service: 8002
  - user-service: 8001
  - product-service: 8003
  - order-service: 8004
  - matching-service: 8005
  - trade-service: 8006
  - market-service: 8007
  - gateway-service: 8000

### API 路由配置

所有前端 API 请求通过 `/api` 前缀，由 Vite 开发服务器代理到网关服务：

```
前端请求: /api/xxx
    ↓
Vite Proxy (localhost:3000)
    ↓
API Gateway (localhost:8000)
    ↓
微服务 (通过服务发现)
```

### 网关路由映射

| 前端 API 路径 | 网关路由 | 目标微服务 |
|-------------|---------|-----------|
| `/api/auth/**` | `/api/auth/**` | auth-service |
| `/api/users/**` | `/api/users/**` | user-service |
| `/api/products/**` | `/api/products/**` | product-service |
| `/api/orders/**` | `/api/orders/**` | order-service |
| `/api/matching/**` | `/api/matching/**` | matching-service |
| `/api/trades/**` | `/api/trades/**` | trade-service |
| `/api/market/**` | `/api/market/**` | market-service |

## 配置更改

### 1. Vite 配置 (`vite.config.ts`)

```typescript
proxy: {
  '/api': {
    target: 'http://localhost:8000', // 网关服务端口
    changeOrigin: true,
    secure: false
  }
}
```

**更改**: 代理目标从 `8080` 改为 `8000`（网关端口）

### 2. API 服务配置 (`src/services/api.ts`)

#### 基础配置
- `baseURL: '/api'` - 所有请求通过 `/api` 前缀
- 自动添加 `Authorization: Bearer {token}` 请求头
- 统一错误处理和响应格式

#### API 端点

**认证服务 (authApi)**
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

**用户服务 (userApi)**
- `GET /api/users/profile` - 获取用户信息
- `PUT /api/users/profile` - 更新用户信息

**商品服务 (productApi)**
- `GET /api/products` - 获取所有商品
- `GET /api/products/{id}` - 获取商品详情
- `POST /api/products` - 创建商品
- `PUT /api/products/{id}` - 更新商品
- `DELETE /api/products/{id}` - 删除商品

**订单服务 (orderApi)**
- `POST /api/orders` - 创建订单
- `GET /api/orders` - 获取订单列表
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders/{id}/match` - 匹配订单
- `PUT /api/orders/{id}` - 更新订单
- `DELETE /api/orders/{id}` - 取消订单
- `GET /api/orders/statistics` - 获取订单统计
- `GET /api/orders/available-quantity` - 获取可用数量

**交易服务 (tradeApi)**
- `GET /api/trades` - 获取交易列表
- `GET /api/trades/{id}` - 获取交易详情
- `GET /api/trades/statistics` - 获取交易统计

**市场服务 (marketApi)**
- `GET /api/market/quote/{productId}` - 获取市场行情
- `GET /api/market/history/{productId}` - 获取历史交易

**匹配服务 (matchingApi)**
- `POST /api/matching/match` - 直接匹配订单（可选）

## 错误处理

### 响应拦截器

1. **成功响应**: 自动包装为标准 `ApiResponse` 格式
   ```typescript
   {
     success: boolean,
     message: string,
     data: T
   }
   ```

2. **错误响应**:
   - `401 Unauthorized`: 自动清除 token，跳转到登录页
   - 网络错误: 返回友好的错误消息
   - 其他错误: 返回服务器错误消息

### 认证流程

1. 用户登录后，token 存储在 `localStorage`
2. 所有 API 请求自动携带 `Authorization: Bearer {token}` 头
3. Token 过期时自动跳转到登录页
4. 登录后可返回原页面

## 开发指南

### 启动前端

```bash
cd power-trading-ui
npm install
npm run dev
```

前端将在 `http://localhost:3000` 启动

### 启动后端服务

确保所有微服务已启动并注册到 Nacos：

```powershell
cd power-trading-microservices
.\start-services.ps1
```

### 验证连接

1. 访问前端: http://localhost:3000
2. 检查浏览器控制台，确认 API 请求正常
3. 检查网络面板，确认请求代理到 `localhost:8000`

## 注意事项

1. **CORS**: 网关服务需要配置 CORS 允许前端域名
2. **服务发现**: 确保所有微服务已注册到 Nacos
3. **网关路由**: 确保网关路由配置与前端 API 路径匹配
4. **认证**: 确保网关正确处理 JWT token 并转发到微服务

## 故障排查

### 问题: API 请求失败，返回 404

**可能原因**:
- 网关服务未启动
- 网关路由配置不正确
- 目标微服务未注册到 Nacos

**解决方案**:
1. 检查网关服务是否运行: `netstat -ano | findstr :8000`
2. 检查 Nacos 控制台，确认服务已注册
3. 检查网关日志，查看路由匹配情况

### 问题: 401 Unauthorized

**可能原因**:
- Token 过期或无效
- 网关未正确转发认证头

**解决方案**:
1. 清除 localStorage 中的 token
2. 重新登录
3. 检查网关配置，确保认证头正确转发

### 问题: 网络错误

**可能原因**:
- 网关服务未启动
- 代理配置错误

**解决方案**:
1. 检查网关服务状态
2. 验证 `vite.config.ts` 中的代理配置
3. 检查防火墙设置

## 更新日志

### 2026-01-08
- ✅ 更新 Vite 代理目标端口为 8000（网关端口）
- ✅ 改进 API 响应拦截器，统一错误处理
- ✅ 添加匹配服务 API（可选）
- ✅ 优化错误消息显示
