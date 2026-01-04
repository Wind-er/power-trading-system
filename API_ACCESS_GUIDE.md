# API 访问指南

## ⚠️ 重要说明

后端服务的 `context-path` 设置为 `/api`，这意味着：

- ❌ **错误访问**：`http://localhost:8080/api` （会返回404）
- ✅ **正确访问**：`http://localhost:8080/api/products` （具体接口路径）

## 正确的 API 访问方式

### 1. 商品接口
```
GET http://localhost:8080/api/products
GET http://localhost:8080/api/products/1
```

### 2. 认证接口
```
POST http://localhost:8080/api/auth/login
POST http://localhost:8080/api/auth/register
```

### 3. 订单接口（需要认证）
```
GET http://localhost:8080/api/orders
POST http://localhost:8080/api/orders
```

### 4. 交易接口（需要认证）
```
GET http://localhost:8080/api/trades
GET http://localhost:8080/api/trades/statistics
```

### 5. 行情接口
```
GET http://localhost:8080/api/market/quote/1
GET http://localhost:8080/api/market/history/1
```

## 前端访问

前端通过 Vite 代理访问，配置在 `vite.config.ts` 中：
- 前端请求：`/api/products`
- Vite 代理转发到：`http://localhost:8080/api/products`

所以前端代码中直接使用 `/api/products` 即可。

## 测试 API

### 使用浏览器测试
访问：`http://localhost:8080/api/products`

### 使用 curl 测试
```bash
curl http://localhost:8080/api/products
```

### 使用 PowerShell 测试
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/products" -Method GET
```

## 常见问题

### 问题1：访问 http://localhost:8080/api 返回404
**原因**：`/api` 是 context-path，不是具体的接口路径
**解决**：访问具体的接口，如 `/api/products`

### 问题2：403 Forbidden
**原因**：接口需要认证但未提供token
**解决**：登录接口 `/api/auth/login` 是公开的，不需要认证

### 问题3：连接被拒绝
**原因**：后端服务未启动
**解决**：检查后端服务是否运行在8080端口

