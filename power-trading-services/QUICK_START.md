# 快速启动指南

## 方案一：使用 H2 内存数据库（推荐 - 无需配置 MySQL）

### 优点
- ✅ 无需安装和配置 MySQL
- ✅ 开箱即用
- ✅ 适合开发和测试

### 缺点
- ⚠️ 数据在应用重启后会丢失（内存数据库）
- ⚠️ 不适合生产环境

### 启动步骤

1. **使用开发配置启动后端**
```bash
cd power-trading-services
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

2. **访问 H2 控制台**（可选）
   - 地址：http://localhost:8080/api/h2-console
   - JDBC URL: `jdbc:h2:mem:power_trading`
   - 用户名: `sa`
   - 密码: （留空）

3. **启动前端**
```bash
cd power-trading-ui
npm run dev
```

## 方案二：使用 MySQL（生产环境推荐）

### 配置步骤

1. **完成 MySQL 安装配置**
   - 参考 `MYSQL_SETUP.md` 文件
   - 启动 MySQL 服务
   - 设置 root 密码
   - 创建数据库

2. **修改配置文件**
   - 编辑 `src/main/resources/application.yml`
   - 修改数据库连接信息

3. **启动后端**
```bash
cd power-trading-services
mvn spring-boot:run
```

## 测试账号

系统启动时会自动创建以下测试账号：

- **buyer1** / 123456 (购电商)
- **buyer2** / 123456 (购电商)
- **generator1** / 123456 (发电商)
- **generator2** / 123456 (发电商)
- **admin** / 123456 (管理员)

## 访问地址

- 前端：http://localhost:3000
- 后端 API：http://localhost:8080/api
- H2 控制台（仅开发模式）：http://localhost:8080/api/h2-console

