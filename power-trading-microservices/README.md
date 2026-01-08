# 电力交易系统微服务架构 - 本地开发环境

## 环境要求

- Java 21
- Maven 3.8+
- Docker & Docker Compose
- IDE (推荐 IntelliJ IDEA 或 VS Code)

## 快速开始

### 1. 启动基础设施服务

使用 Docker Compose 启动 Nacos、MySQL、Redis、Seata：

```bash
# 在项目根目录执行
docker-compose up -d
```

等待所有服务启动完成（约 1-2 分钟），可以通过以下方式检查：

- **Nacos**: http://localhost:8848/nacos (用户名/密码: nacos/nacos)
- **MySQL**: localhost:3306 (root/root123)
- **Redis**: localhost:6379 (密码: redis123)
- **Seata**: localhost:8091

### 2. 初始化 Nacos 配置

访问 Nacos 控制台：http://localhost:8848/nacos

1. 登录（默认用户名/密码：nacos/nacos）
2. 创建命名空间 `power-trading`
3. 导入配置（可选，各服务已配置本地配置文件）

### 3. 初始化数据库

数据库会在 Docker 启动时自动创建，但表结构需要各服务启动时通过 JPA 自动创建，或手动执行 SQL 脚本。

### 4. 启动微服务

#### 方式一：使用 IDE 启动

在 IDE 中依次启动以下服务（按顺序）：

1. **user-service** (端口: 8001)
2. **auth-service** (端口: 8002)
3. **product-service** (端口: 8003)
4. **order-service** (端口: 8004)
5. **matching-service** (端口: 8005)
6. **trade-service** (端口: 8006)
7. **market-service** (端口: 8007)
8. **gateway-service** (端口: 8000)

#### 方式二：使用 Maven 命令启动

```bash
# 在 power-trading-microservices 目录下
cd power-trading-user-service
mvn spring-boot:run

# 新开终端窗口，启动下一个服务
cd power-trading-auth-service
mvn spring-boot:run

# ... 依次启动其他服务
```

#### 方式三：使用启动脚本（Windows）

```powershell
.\start-services.ps1
```

### 5. 验证服务启动

- 访问网关：http://localhost:8000
- 查看 Nacos 服务列表：http://localhost:8848/nacos/#/serviceManagement
- 检查各服务健康状态

## 服务端口列表

| 服务 | 端口 | 说明 |
|------|------|------|
| gateway-service | 8000 | API网关 |
| user-service | 8001 | 用户服务 |
| auth-service | 8002 | 认证服务 |
| product-service | 8003 | 商品服务 |
| order-service | 8004 | 订单服务 |
| matching-service | 8005 | 撮合服务 |
| trade-service | 8006 | 交易服务 |
| market-service | 8007 | 市场行情服务 |

## 数据库配置

| 数据库 | 说明 |
|--------|------|
| power_trading_user | 用户数据库（user-service, auth-service 共享） |
| power_trading_product | 商品数据库 |
| power_trading_order | 订单数据库 |
| power_trading_trade | 交易数据库 |
| power_trading_market | 市场行情数据库 |
| seata | Seata 分布式事务数据库 |
| nacos_config | Nacos 配置数据库 |

**数据库连接信息**：
- 主机: localhost:3306
- 用户名: power_trading
- 密码: power_trading123

## 常见问题

### 1. 服务无法注册到 Nacos

- 检查 Nacos 是否启动：http://localhost:8848/nacos
- 检查命名空间 `power-trading` 是否存在
- 检查服务配置文件中的 Nacos 地址是否正确

### 2. 数据库连接失败

- 检查 MySQL 容器是否正常运行：`docker ps`
- 检查数据库是否已创建：`docker exec -it mysql-server mysql -uroot -proot123 -e "SHOW DATABASES;"`
- 检查数据库用户名密码是否正确

### 3. Redis 连接失败

- 检查 Redis 容器是否正常运行
- 确认 Redis 密码为 `redis123`

### 4. 端口被占用

- 检查端口占用：`netstat -ano | findstr :8000`
- 修改服务配置文件中的端口号

## 开发建议

1. **服务启动顺序**：先启动基础服务（user, auth, product），再启动业务服务（order, trade, market），最后启动网关
2. **日志查看**：各服务日志会输出到控制台，建议使用 IDE 的日志窗口查看
3. **配置管理**：开发环境使用本地配置文件，生产环境使用 Nacos 配置中心
4. **数据库迁移**：建议使用 Flyway 或 Liquibase 管理数据库版本

## 下一步

- 实现各服务的业务逻辑
- 配置 Sentinel 限流熔断规则
- 配置 Seata 分布式事务
- 集成消息队列（RocketMQ）
- 配置监控和链路追踪

## 参考文档

- [微服务架构设计方案](./microservices.md)
- [Spring Cloud Alibaba 官方文档](https://github.com/alibaba/spring-cloud-alibaba)
- [Nacos 官方文档](https://nacos.io/docs/latest/what-is-nacos/)
