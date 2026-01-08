# 电力交易系统微服务架构设计方案

## 1. 架构设计概述

### 1.1 设计目标
- **高可用性**：通过服务拆分和容错机制，提高系统整体可用性
- **可扩展性**：支持水平扩展，应对业务增长
- **解耦合**：业务模块独立部署、独立扩展
- **技术栈统一**：基于 Spring Cloud Alibaba 生态
- **渐进式演进**：支持从单体到微服务的平滑迁移

### 1.2 技术选型

#### 核心组件
- **Spring Cloud Alibaba 2022.0.0.0** (对应 Spring Cloud 2022.x)
- **Spring Boot 3.2.0** (兼容 Java 21)
- **Spring Cloud Gateway** - API网关
- **Nacos** - 服务注册发现 + 配置中心
- **Sentinel** - 流量控制、熔断降级
- **Seata** - 分布式事务
- **RocketMQ** (可选) - 消息队列

#### 数据存储
- **MySQL 8.0** - 主数据库
- **Redis** - 缓存 + 分布式锁 + Session存储
- **Elasticsearch** (可选) - 日志和检索

#### 监控运维
- **Prometheus** - 指标收集
- **Grafana** - 可视化监控
- **SkyWalking** 或 **Zipkin** - 分布式追踪

## 2. 微服务拆分方案

### 2.1 服务拆分原则

基于领域驱动设计（DDD）和业务边界，将单体应用拆分为以下微服务：

1. **用户服务 (user-service)**
2. **认证服务 (auth-service)**
3. **商品服务 (product-service)**
4. **订单服务 (order-service)**
5. **交易撮合服务 (matching-service)**
6. **交易服务 (trade-service)**
7. **市场行情服务 (market-service)**
8. **网关服务 (gateway-service)**

### 2.2 服务详细设计

#### 2.2.1 用户服务 (user-service)

**职责**：
- 用户信息管理（CRUD）
- 用户状态管理
- 用户权限信息查询

**端口**：`8001`

**数据库**：`power_trading_user` (users表)

**核心接口**：
```
GET    /api/users/{id}              - 获取用户信息
PUT    /api/users/{id}              - 更新用户信息
GET    /api/users/validate/{id}     - 验证用户是否存在
GET    /api/users/batch              - 批量获取用户信息
```

**技术特点**：
- 使用 Redis 缓存用户信息（TTL: 5分钟）
- 对外提供用户查询接口，供其他服务调用

---

#### 2.2.2 认证服务 (auth-service)

**职责**：
- 用户注册
- 用户登录
- JWT Token 生成与验证
- Token 刷新
- 密码加密与验证

**端口**：`8002`

**数据库**：共享 `power_trading_user` 数据库（用户表）

**核心接口**：
```
POST   /api/auth/register           - 用户注册
POST   /api/auth/login              - 用户登录
POST   /api/auth/logout             - 用户登出
POST   /api/auth/refresh            - 刷新Token
POST   /api/auth/validate           - 验证Token
```

**技术特点**：
- JWT Token 存储在 Redis（用于支持登出和刷新）
- 密码使用 BCrypt 加密
- 登录失败次数限制（防止暴力破解）

---

#### 2.2.3 商品服务 (product-service)

**职责**：
- 商品信息管理
- 商品列表查询
- 商品详情查询
- 商品状态管理

**端口**：`8003`

**数据库**：`power_trading_product` (products表)

**核心接口**：
```
GET    /api/products                - 获取商品列表
GET    /api/products/{id}           - 获取商品详情
GET    /api/products/validate/{id}  - 验证商品是否存在
```

**技术特点**：
- 使用 Redis 缓存商品列表（TTL: 10分钟）
- 支持商品上下架操作

---

#### 2.2.4 订单服务 (order-service)

**职责**：
- 订单创建
- 订单查询（列表、详情）
- 订单修改
- 订单撤销
- 订单状态更新

**端口**：`8004`

**数据库**：`power_trading_order` (orders表)

**核心接口**：
```
POST   /api/orders                  - 创建订单
GET    /api/orders                  - 获取订单列表
GET    /api/orders/{id}             - 获取订单详情
PUT    /api/orders/{id}             - 修改订单
DELETE /api/orders/{id}             - 撤销订单
PUT    /api/orders/{id}/status      - 更新订单状态
GET    /api/orders/matching/{productId}/{type} - 获取可撮合订单
```

**技术特点**：
- 创建订单后，异步发送消息到撮合服务
- 支持订单状态变更事件发布（用于更新市场行情）
- 使用分布式锁保证订单状态更新的原子性

**依赖服务**：
- `user-service` - 验证用户信息
- `product-service` - 验证商品信息
- `matching-service` - 触发撮合流程

---

#### 2.2.5 交易撮合服务 (matching-service)

**职责**：
- 订单撮合逻辑
- 撮合规则执行（价格优先、时间优先）
- 撮合结果通知

**端口**：`8005`

**数据库**：无独立数据库，通过 RPC 调用其他服务

**核心接口**：
```
POST   /api/matching/match          - 执行订单撮合
GET    /api/matching/queue/{productId} - 获取撮合队列
```

**技术特点**：
- **无状态服务**：撮合逻辑不存储数据，只进行计算
- 支持高并发撮合处理
- 使用消息队列（RocketMQ）处理撮合请求
- 撮合成功后，调用交易服务创建交易记录

**依赖服务**：
- `order-service` - 查询可撮合订单、更新订单状态
- `trade-service` - 创建交易记录
- `market-service` - 更新市场行情

---

#### 2.2.6 交易服务 (trade-service)

**职责**：
- 交易记录创建
- 交易记录查询
- 交易统计
- 交易历史导出

**端口**：`8006`

**数据库**：`power_trading_trade` (trades表)

**核心接口**：
```
POST   /api/trades                  - 创建交易记录
GET    /api/trades                  - 获取交易记录列表
GET    /api/trades/{id}             - 获取交易详情
GET    /api/trades/statistics       - 获取交易统计
```

**技术特点**：
- 交易记录创建后，异步通知市场行情服务
- 支持按商品、用户、时间范围查询
- 支持分页查询

**依赖服务**：
- `order-service` - 验证订单信息
- `market-service` - 更新行情数据

---

#### 2.2.7 市场行情服务 (market-service)

**职责**：
- 实时行情数据计算
- 历史价格统计
- 价格走势分析
- 行情数据缓存

**端口**：`8007`

**数据库**：`power_trading_market` (market_quotes, market_history表)

**核心接口**：
```
GET    /api/market/quote/{productId}      - 获取商品行情
GET    /api/market/history/{productId}    - 获取历史价格
GET    /api/market/overview               - 获取市场概览
POST   /api/market/update                 - 更新行情（内部接口）
```

**技术特点**：
- 使用 Redis 缓存实时行情（TTL: 1分钟）
- 定时计算市场统计数据
- 支持 WebSocket 推送实时行情（可选）

**依赖服务**：
- `trade-service` - 接收交易事件，更新行情
- `product-service` - 获取商品信息

---

#### 2.2.8 API网关服务 (gateway-service)

**职责**：
- 统一入口路由
- 请求认证（Token验证）
- 请求限流
- 跨域处理
- 请求日志记录

**端口**：`8000`

**核心功能**：
- 路由转发到各个微服务
- 集成 Sentinel 实现限流熔断
- JWT Token 验证（调用 auth-service）
- 请求日志记录（发送到日志中心）

**路由规则**：
```
/api/auth/**          -> auth-service:8002
/api/users/**         -> user-service:8001
/api/products/**      -> product-service:8003
/api/orders/**        -> order-service:8004
/api/matching/**      -> matching-service:8005
/api/trades/**        -> trade-service:8006
/api/market/**        -> market-service:8007
```

---

## 3. 数据库拆分方案

### 3.1 数据库拆分策略

采用**垂直拆分**方式，按业务域拆分数据库：

| 服务 | 数据库名称 | 主要表 | 说明 |
|------|-----------|--------|------|
| user-service | power_trading_user | users | 用户基础信息 |
| auth-service | power_trading_user | users | 共享用户数据库 |
| product-service | power_trading_product | products | 商品信息 |
| order-service | power_trading_order | orders | 订单信息 |
| trade-service | power_trading_trade | trades | 交易记录 |
| market-service | power_trading_market | market_quotes, market_history | 市场行情 |

### 3.2 数据一致性方案

#### 3.2.1 分布式事务

对于跨服务的业务操作（如：创建订单 -> 触发撮合 -> 创建交易），使用 **Seata** 实现分布式事务：

**场景1：订单创建 + 撮合流程**
```
订单服务：创建订单（本地事务）
  ↓
撮合服务：撮合逻辑（无状态）
  ↓
交易服务：创建交易记录（Seata分布式事务）
  ↓
订单服务：更新订单状态（Seata分布式事务）
```

**场景2：交易完成后的数据更新**
```
交易服务：创建交易记录（本地事务）
  ↓ (异步消息)
市场行情服务：更新行情数据（最终一致性）
```

#### 3.2.2 最终一致性

对于非核心一致性要求的场景，采用**事件驱动 + 最终一致性**：

- 订单状态变更 → 发布事件 → 市场行情服务异步更新
- 交易创建 → 发布事件 → 市场行情服务异步更新

使用 **RocketMQ** 作为消息中间件，保证消息可靠投递。

---

## 4. 服务间通信方案

### 4.1 通信方式选择

| 场景 | 通信方式 | 技术选型 | 说明 |
|------|---------|---------|------|
| 同步调用（需要返回值） | REST API | OpenFeign | 服务间查询、验证 |
| 异步事件（不需要立即返回） | 消息队列 | RocketMQ | 订单撮合、行情更新 |
| 高性能场景 | RPC | Dubbo (可选) | 撮合服务高频调用 |

### 4.2 OpenFeign 服务调用

**依赖配置**：
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

**示例：订单服务调用用户服务**
```java
@FeignClient(name = "user-service", path = "/api/users")
public interface UserServiceClient {
    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable Long id);
    
    @GetMapping("/validate/{id}")
    Boolean validateUser(@PathVariable Long id);
}
```

### 4.3 RocketMQ 消息通信

**使用场景**：
1. 订单创建后，发送撮合消息
2. 交易完成后，发送行情更新消息
3. 订单状态变更，发送通知消息

**消息主题设计**：
```
order-created-topic      - 订单创建事件
order-status-changed     - 订单状态变更事件
trade-created-topic      - 交易创建事件
matching-request-topic   - 撮合请求
```

---

## 5. 认证授权方案

### 5.1 统一认证架构

```
客户端请求
  ↓
API网关 (验证Token)
  ↓ (Token有效)
转发到具体服务
```

### 5.2 JWT Token 设计

**Token结构**：
```json
{
  "userId": 123,
  "username": "buyer1",
  "userType": "BUYER",
  "exp": 1234567890
}
```

**Token存储**：
- **客户端**：LocalStorage 或 Cookie
- **服务端**：Redis（用于支持登出和刷新，Key: `token:{userId}`, TTL: 24小时）

### 5.3 网关认证流程

1. 客户端请求携带 Token（Header: `Authorization: Bearer {token}`）
2. 网关拦截请求，调用 `auth-service` 验证 Token
3. Token 有效：解析用户信息，添加到请求头（`X-User-Id`, `X-User-Type`）
4. Token 无效：返回 401 Unauthorized

### 5.4 服务内授权

各微服务从请求头获取用户信息，进行业务权限校验：
```java
@GetMapping("/orders")
public List<Order> getOrders(HttpServletRequest request) {
    Long userId = Long.parseLong(request.getHeader("X-User-Id"));
    // 业务逻辑...
}
```

---

## 6. 服务注册与发现

### 6.1 Nacos 配置

**服务注册**：
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: power-trading
        group: DEFAULT_GROUP
```

**配置中心**：
```yaml
spring:
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        namespace: power-trading
        group: DEFAULT_GROUP
        file-extension: yml
        shared-configs:
          - data-id: common-config.yml
            group: DEFAULT_GROUP
            refresh: true
```

### 6.2 服务发现使用

通过 OpenFeign 自动发现服务：
```java
@FeignClient(name = "user-service")  // 自动从Nacos发现
public interface UserServiceClient {
    // ...
}
```

---

## 7. 流量控制与容错

### 7.1 Sentinel 集成

**限流规则**：
- API网关：全局限流 QPS = 1000
- 订单服务：创建订单接口 QPS = 100
- 撮合服务：撮合接口 QPS = 500

**熔断规则**：
- 服务调用失败率 > 50%，触发熔断
- 熔断持续时间：10秒
- 最小请求数：5

**降级策略**：
- 用户服务不可用：返回缓存数据或默认值
- 商品服务不可用：返回缓存数据
- 撮合服务不可用：订单进入队列，稍后处理

### 7.2 配置示例

**网关限流**：
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200
```

---

## 8. 分布式事务（Seata）

### 8.1 使用场景

**主要场景：订单撮合 + 交易创建**

```
1. 订单服务：更新订单状态（部分成交/完全成交）
2. 交易服务：创建交易记录
3. 以上操作需要在同一个分布式事务中
```

### 8.2 Seata 配置

**AT模式（自动补偿）**：
```yaml
seata:
  enabled: true
  application-id: order-service
  tx-service-group: power-trading-group
  config:
    type: nacos
    nacos:
      server-addr: localhost:8848
      namespace: power-trading
  registry:
    type: nacos
    nacos:
      server-addr: localhost:8848
      namespace: power-trading
```

**业务代码**：
```java
@GlobalTransactional
public void executeTrade(Order buyOrder, Order sellOrder) {
    // 1. 创建交易记录
    tradeService.createTrade(...);
    // 2. 更新订单状态
    orderService.updateOrderStatus(...);
}
```

---

## 9. 缓存策略

### 9.1 Redis 使用场景

| 场景 | Key设计 | TTL | 说明 |
|------|---------|-----|------|
| 用户信息 | `user:{userId}` | 5分钟 | 用户查询缓存 |
| 商品信息 | `product:{productId}` | 10分钟 | 商品查询缓存 |
| 商品列表 | `products:list` | 10分钟 | 商品列表缓存 |
| 行情数据 | `market:quote:{productId}` | 1分钟 | 实时行情缓存 |
| JWT Token | `token:{userId}` | 24小时 | Token存储 |
| 分布式锁 | `lock:order:{orderId}` | 10秒 | 订单操作锁 |

### 9.2 缓存更新策略

- **Cache Aside**：先查缓存，缓存未命中查询数据库，写入缓存
- **Write Through**：写入数据库同时更新缓存
- **失效策略**：数据更新时，删除对应缓存

---

## 10. 监控与日志

### 10.1 链路追踪

使用 **SkyWalking** 实现分布式追踪：

```yaml
agent:
  service_name: order-service
  collector:
    backend_service: localhost:11800
```

### 10.2 日志聚合

- **ELK Stack** (Elasticsearch + Logstash + Kibana)
- 或 **Loki + Grafana**

各服务日志格式：
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "level": "INFO",
  "service": "order-service",
  "traceId": "xxx",
  "message": "..."
}
```

### 10.3 指标监控

**Prometheus + Grafana**：
- JVM 指标（内存、GC）
- HTTP 请求指标（QPS、延迟、错误率）
- 数据库连接池指标
- Redis 连接指标

---

## 11. 部署架构

### 11.1 部署环境

**开发环境**：
- 本地开发，使用 Docker Compose 启动 Nacos、MySQL、Redis
- 各微服务本地启动

**测试环境**：
- Kubernetes 或 Docker Swarm
- 每个服务 1-2 个实例

**生产环境**：
- Kubernetes 集群
- 每个服务至少 2 个实例（保证高可用）
- 自动扩缩容（基于CPU、内存、QPS）

### 11.2 Docker Compose 示例

```yaml
version: '3.8'
services:
  nacos:
    image: nacos/nacos-server:v2.2.0
    ports:
      - "8848:8848"
    environment:
      MODE: standalone
  
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: root
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
  
  seata-server:
    image: seataio/seata-server:latest
    ports:
      - "8091:8091"
    environment:
      SEATA_PORT: 8091
```

---

## 12. 迁移方案

### 12.1 迁移步骤

#### 阶段一：基础设施搭建（1周）
1. 搭建 Nacos、Redis、MySQL 集群
2. 搭建 API 网关
3. 配置服务注册发现

#### 阶段二：服务拆分（2-3周）
1. **第一周**：拆分用户服务和认证服务
2. **第二周**：拆分商品服务、订单服务
3. **第三周**：拆分撮合服务、交易服务、市场行情服务

#### 阶段三：功能验证（1周）
1. 接口联调测试
2. 性能测试
3. 压力测试

#### 阶段四：灰度发布（1周）
1. 20% 流量切到微服务
2. 50% 流量切到微服务
3. 100% 流量切到微服务

#### 阶段五：优化（持续）
1. 性能优化
2. 监控告警完善
3. 文档完善

### 12.2 兼容性考虑

**双写策略**（过渡期）：
- 新请求走微服务
- 同时写入原单体数据库（用于数据对比验证）
- 验证无误后，停止双写

---

## 13. 项目结构

### 13.1 父项目结构

```
power-trading-microservices/
├── pom.xml                          # 父POM
├── power-trading-common/            # 公共模块
│   ├── common-core/                 # 核心工具类
│   ├── common-dto/                  # 公共DTO
│   └── common-feign/                # Feign客户端定义
├── power-trading-gateway/           # API网关
├── power-trading-user-service/      # 用户服务
├── power-trading-auth-service/      # 认证服务
├── power-trading-product-service/   # 商品服务
├── power-trading-order-service/     # 订单服务
├── power-trading-matching-service/  # 撮合服务
├── power-trading-trade-service/     # 交易服务
└── power-trading-market-service/    # 市场行情服务
```

### 13.2 单个服务结构

```
order-service/
├── src/main/java/com/power/trading/order/
│   ├── OrderApplication.java
│   ├── controller/                  # REST控制器
│   ├── service/                     # 业务逻辑
│   ├── repository/                  # 数据访问
│   ├── entity/                      # 实体类
│   ├── dto/                         # DTO
│   ├── feign/                       # Feign客户端
│   ├── config/                      # 配置类
│   └── mq/                          # 消息队列
├── src/main/resources/
│   ├── application.yml
│   └── bootstrap.yml
└── pom.xml
```

---

## 14. 关键技术点

### 14.1 服务间调用超时配置

```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
  hystrix:
    enabled: false  # 使用Sentinel替代
```

### 14.2 数据库连接池

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

### 14.3 服务启动顺序

1. Nacos Server
2. MySQL、Redis
3. Seata Server
4. 基础服务（user-service, auth-service, product-service）
5. 业务服务（order-service, trade-service, market-service）
6. 核心服务（matching-service）
7. API Gateway

---

## 15. 风险与挑战

### 15.1 技术风险

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| 分布式事务性能 | 高 | 优化Seata配置，减少分布式事务范围 |
| 服务间网络延迟 | 中 | 合理使用缓存，减少服务调用 |
| 数据一致性 | 高 | 核心场景用分布式事务，非核心用最终一致性 |
| 服务雪崩 | 高 | Sentinel熔断降级，服务隔离 |

### 15.2 运维风险

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| 服务数量增加 | 中 | 自动化部署，容器化部署 |
| 日志分散 | 中 | 统一日志中心，链路追踪 |
| 监控复杂 | 中 | 统一监控平台，告警机制 |

---

## 16. 总结

本微服务架构方案基于 Spring Cloud Alibaba 生态，通过合理的服务拆分、统一的网关入口、完善的服务治理机制，实现了系统的高可用、高扩展、易维护。

### 核心优势

1. **技术栈统一**：基于 Spring Cloud Alibaba，学习成本低
2. **渐进式迁移**：支持平滑迁移，降低风险
3. **完善的服务治理**：注册发现、配置中心、限流熔断、分布式事务
4. **可观测性**：链路追踪、日志聚合、指标监控

### 下一步工作

1. 搭建基础环境（Nacos、MySQL、Redis）
2. 创建项目骨架和公共模块
3. 逐个拆分服务并验证
4. 完善监控和告警
5. 性能测试和优化
