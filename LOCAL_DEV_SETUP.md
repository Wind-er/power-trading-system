# 本地开发环境搭建指南

本文档说明如何搭建电力交易系统微服务架构的本地开发环境。

## 前置要求

### 必需软件

1. **Java 21**
   - 下载地址：https://www.oracle.com/java/technologies/downloads/#java21
   - 验证安装：`java -version`

2. **Maven 3.8+**
   - 下载地址：https://maven.apache.org/download.cgi
   - 验证安装：`mvn -version`

3. **Docker Desktop**
   - Windows: https://www.docker.com/products/docker-desktop
   - 验证安装：`docker --version` 和 `docker-compose --version`

### 可选工具

- **IntelliJ IDEA** 或 **VS Code**（推荐使用 IDEA）
- **Postman** 或 **Apifox**（用于 API 测试）
- **DBeaver** 或 **Navicat**（数据库管理工具）

## 环境搭建步骤

### 第一步：启动基础设施服务

1. 打开终端，进入项目根目录

2. 启动 Docker Compose 服务：

```bash
docker-compose up -d
```

3. 等待服务启动（约 1-2 分钟），检查服务状态：

```bash
docker ps
```

应该看到以下容器运行中：
- `nacos-server`
- `mysql-server`
- `redis-server`
- `seata-server`

4. 验证服务：

   - **Nacos**: 浏览器访问 http://localhost:8848/nacos
     - 默认用户名/密码：`nacos/nacos`
     - 创建命名空间：`power-trading`
   
   - **MySQL**: 使用数据库工具连接
     - 主机：`localhost:3306`
     - 用户名：`root`
     - 密码：`root123`
   
   - **Redis**: 使用 Redis 客户端连接
     - 主机：`localhost:6379`
     - 密码：`redis123`

### 第二步：初始化数据库

数据库会在 Docker 启动时自动创建，包括：

- `power_trading_user`
- `power_trading_product`
- `power_trading_order`
- `power_trading_trade`
- `power_trading_market`
- `seata`
- `nacos_config`

表结构会在各服务首次启动时通过 JPA 自动创建（`ddl-auto: update`）。

### 第三步：编译项目

在 `power-trading-microservices` 目录下执行：

```bash
mvn clean install -DskipTests
```

### 第四步：启动微服务

#### 方式一：使用启动脚本（推荐）

**Windows**:
```powershell
cd power-trading-microservices
.\start-services.ps1
```

脚本会自动：
1. 检查 Docker 服务
2. 启动基础设施服务（如果未运行）
3. 按顺序启动所有微服务

#### 方式二：使用 IDE 启动

1. 使用 IntelliJ IDEA 打开 `power-trading-microservices` 目录
2. 等待 Maven 依赖下载完成
3. 依次运行各服务的 `Application` 主类：
   - `UserServiceApplication`
   - `AuthServiceApplication`
   - `ProductServiceApplication`
   - `OrderServiceApplication`
   - `MatchingServiceApplication`
   - `TradeServiceApplication`
   - `MarketServiceApplication`
   - `GatewayApplication`

#### 方式三：使用 Maven 命令

在各自的模块目录下执行：

```bash
# 终端1
cd power-trading-user-service
mvn spring-boot:run

# 终端2
cd power-trading-auth-service
mvn spring-boot:run

# ... 依次启动其他服务
```

### 第五步：验证服务启动

1. **检查 Nacos 服务注册**：
   - 访问 http://localhost:8848/nacos
   - 进入"服务管理" -> "服务列表"
   - 应该看到所有 8 个服务已注册

2. **测试网关**：
   ```bash
   curl http://localhost:8000/actuator/health
   ```

3. **测试服务健康检查**：
   ```bash
   # 测试用户服务
   curl http://localhost:8001/actuator/health
   ```

## 服务端口说明

| 服务名称 | 端口 | 访问地址 |
|---------|------|---------|
| gateway-service | 8000 | http://localhost:8000 |
| user-service | 8001 | http://localhost:8001 |
| auth-service | 8002 | http://localhost:8002 |
| product-service | 8003 | http://localhost:8003 |
| order-service | 8004 | http://localhost:8004 |
| matching-service | 8005 | http://localhost:8005 |
| trade-service | 8006 | http://localhost:8006 |
| market-service | 8007 | http://localhost:8007 |

## 配置说明

### 数据库配置

所有服务的数据库配置在各自的 `application.yml` 中：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/power_trading_xxx
    username: power_trading
    password: power_trading123
```

### Redis 配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: redis123
```

### Nacos 配置

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: power-trading
```

## 常见问题排查

### 1. 服务无法启动

**问题**：端口被占用
```bash
# Windows
netstat -ano | findstr :8000

# 解决方案：修改服务配置文件中的端口号
```

**问题**：数据库连接失败
- 检查 MySQL 容器是否运行：`docker ps`
- 检查数据库是否创建：`docker exec -it mysql-server mysql -uroot -proot123 -e "SHOW DATABASES;"`
- 检查用户名密码是否正确

### 2. 服务无法注册到 Nacos

- 检查 Nacos 是否正常运行：http://localhost:8848/nacos
- 检查命名空间 `power-trading` 是否存在
- 检查服务配置中的 Nacos 地址是否正确
- 查看服务启动日志中的错误信息

### 3. 依赖下载失败

- 检查网络连接
- 配置 Maven 镜像（推荐使用阿里云镜像）
- 清理 Maven 缓存：`mvn clean`

### 4. Docker 容器启动失败

- 检查 Docker Desktop 是否运行
- 检查端口是否被占用
- 查看容器日志：`docker logs <container-name>`
- 重启 Docker Desktop

## 开发建议

1. **服务启动顺序**：
   - 先启动基础设施服务（Nacos, MySQL, Redis）
   - 再启动基础服务（user, auth, product）
   - 然后启动业务服务（order, trade, market）
   - 最后启动网关服务

2. **日志查看**：
   - 各服务日志输出到控制台
   - 建议使用 IDE 的日志窗口查看
   - 生产环境建议使用 ELK 或 Loki 聚合日志

3. **配置管理**：
   - 开发环境：使用本地 `application.yml`
   - 生产环境：使用 Nacos 配置中心

4. **数据库管理**：
   - 开发环境：使用 JPA 自动创建表（`ddl-auto: update`）
   - 生产环境：使用 Flyway 或 Liquibase 管理数据库版本

## 停止服务

### 停止微服务

**Windows**:
```powershell
.\stop-services.ps1
```

或手动关闭各个服务的运行窗口。

### 停止基础设施服务

```bash
docker-compose down
```

## 下一步

环境搭建完成后，可以：

1. 实现各服务的业务逻辑
2. 配置 Sentinel 限流熔断规则
3. 配置 Seata 分布式事务
4. 集成消息队列（RocketMQ）
5. 配置监控和链路追踪（Prometheus, Grafana, SkyWalking）

## 参考文档

- [微服务架构设计方案](./microservices.md)
- [Spring Cloud Alibaba 文档](https://github.com/alibaba/spring-cloud-alibaba)
- [Nacos 文档](https://nacos.io/docs/latest/what-is-nacos/)
