# 启动基础设施服务指南

## 前置条件

在启动基础设施服务之前，请确保：

1. **Docker Desktop 已安装并正在运行**
   - 检查方法：任务栏是否有 Docker 图标
   - 如果未运行，请启动 Docker Desktop 并等待其完全启动

## 启动步骤

### 1. 启动 Docker Desktop

如果 Docker Desktop 未运行：

1. 在 Windows 开始菜单中搜索 "Docker Desktop"
2. 启动 Docker Desktop
3. 等待 Docker Desktop 完全启动（任务栏图标不再闪烁）

### 2. 验证 Docker 运行状态

打开 PowerShell 或命令提示符，执行：

```powershell
docker ps
```

如果看到类似以下输出，说明 Docker 正常运行：
```
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
```

如果出现错误，请确保 Docker Desktop 已完全启动。

### 3. 启动基础设施服务

在项目根目录执行：

```powershell
docker compose up -d
```

这个命令会启动以下服务：
- **Nacos** (端口 8848) - 服务注册发现和配置中心
- **MySQL** (端口 3306) - 数据库
- **Redis** (端口 6379) - 缓存
- **Seata** (端口 8091) - 分布式事务

### 4. 检查服务状态

等待约 30-60 秒后，检查服务是否正常运行：

```powershell
docker ps
```

应该看到 4 个容器正在运行：
- `nacos-server`
- `mysql-server`
- `redis-server`
- `seata-server`

### 5. 验证服务

#### 验证 Nacos

1. 打开浏览器访问：http://localhost:8848/nacos
2. 默认用户名/密码：`nacos/nacos`
3. 登录后，创建命名空间：`power-trading`

#### 验证 MySQL

```powershell
docker exec -it mysql-server mysql -uroot -proot123 -e "SHOW DATABASES;"
```

应该看到以下数据库：
- `nacos_config`
- `power_trading_user`
- `power_trading_product`
- `power_trading_order`
- `power_trading_trade`
- `power_trading_market`
- `seata`

#### 验证 Redis

```powershell
docker exec -it redis-server redis-cli -a redis123 PING
```

应该返回：`PONG`

## 常见问题

### 问题1：Docker Desktop 无法启动

**解决方案**：
- 检查 Windows 是否启用了虚拟化功能
- 重启计算机
- 重新安装 Docker Desktop

### 问题2：端口被占用

**错误信息**：`Bind for 0.0.0.0:8848 failed: port is already allocated`

**解决方案**：
1. 查找占用端口的进程：
   ```powershell
   netstat -ano | findstr :8848
   ```
2. 停止占用端口的进程，或修改 docker-compose.yml 中的端口映射

### 问题3：容器启动失败

**查看容器日志**：
```powershell
docker logs nacos-server
docker logs mysql-server
docker logs redis-server
docker logs seata-server
```

**常见原因**：
- 端口冲突
- 磁盘空间不足
- 内存不足

### 问题4：MySQL 连接失败

**检查 MySQL 是否完全启动**：
```powershell
docker logs mysql-server
```

MySQL 首次启动需要一些时间初始化数据库。

## 停止服务

如果需要停止基础设施服务：

```powershell
docker compose down
```

如果需要停止并删除数据卷：

```powershell
docker compose down -v
```

**注意**：删除数据卷会清除所有数据，包括数据库中的数据。

## 重启服务

```powershell
docker compose restart
```

## 查看服务日志

```powershell
# 查看所有服务日志
docker compose logs

# 查看特定服务日志
docker compose logs nacos
docker compose logs mysql
docker compose logs redis
docker compose logs seata

# 实时跟踪日志
docker compose logs -f nacos
```
