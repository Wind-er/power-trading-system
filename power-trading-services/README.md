# 电力交易系统后端服务

## 技术栈
- Spring Boot 2.7.14
- Spring Data JPA
- MySQL 8.0
- JWT 认证
- Maven

## 快速开始

### 1. 数据库配置
创建数据库：
```sql
CREATE DATABASE power_trading CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改 `src/main/resources/application.yml` 中的数据库连接信息。

### 2. 运行项目
```bash
mvn spring-boot:run
```

服务将在 http://localhost:8080/api 启动

## API 接口

### 认证接口
- POST /api/auth/register - 用户注册
- POST /api/auth/login - 用户登录

### 商品接口
- GET /api/products - 获取商品列表
- GET /api/products/{id} - 获取商品详情

### 订单接口
- POST /api/orders - 创建订单
- GET /api/orders - 获取订单列表
- GET /api/orders/{id} - 获取订单详情
- PUT /api/orders/{id} - 修改订单
- DELETE /api/orders/{id} - 撤销订单

### 交易接口
- GET /api/trades - 获取交易记录
- GET /api/trades/{id} - 获取交易详情
- GET /api/trades/statistics - 获取交易统计

### 行情接口
- GET /api/market/quote/{productId} - 获取商品行情
- GET /api/market/history/{productId} - 获取历史价格

