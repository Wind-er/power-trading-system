# 电力交易系统

一个简易的电力交易平台，支持发电商和购电商之间的电力商品在线交易撮合。

## 项目结构

```
power-trading-system/
├── power-trading-services/    # 后端服务 (Spring Boot)
├── power-trading-ui/          # 前端应用 (React + TypeScript)
└── requirement.md             # 功能需求文档
```

## 技术栈

### 后端
- Spring Boot 2.7.14
- Spring Data JPA
- MySQL 8.0
- JWT 认证
- Maven

### 前端
- React 18
- TypeScript
- React Router
- Axios
- Vite

## 快速开始

### 1. 数据库准备

创建MySQL数据库：
```sql
CREATE DATABASE power_trading CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 后端启动

**方式一：使用 H2 内存数据库（推荐 - 无需配置 MySQL）**
```bash
cd power-trading-services
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
> 注意：H2 是内存数据库，重启后数据会丢失，适合开发和测试

**方式二：使用 MySQL（生产环境）**
```bash
cd power-trading-services
# 1. 先完成 MySQL 配置（参考 power-trading-services/MYSQL_SETUP.md）
# 2. 修改 src/main/resources/application.yml 中的数据库连接信息
mvn spring-boot:run
```

后端服务将在 http://localhost:8080/api 启动

### 3. 前端启动

```bash
cd power-trading-ui
npm install
npm run dev
```

前端应用将在 http://localhost:3000 启动

## 测试账号

系统启动时会自动创建以下测试账号，可以直接使用：

### 购电商账号
- **buyer1** / 123456 - 测试购电公司A
- **buyer2** / 123456 - 测试购电公司B

### 发电商账号
- **generator1** / 123456 - 测试发电公司A
- **generator2** / 123456 - 测试发电公司B

### 管理员账号
- **admin** / 123456 - 系统管理员

> 详细测试账号说明请参考 `power-trading-services/TEST_ACCOUNTS.md`

## 核心功能

### 用户管理
- 用户注册/登录
- 支持三种用户类型：发电商、购电商、管理员
- JWT 身份认证

### 商品管理
- 商品列表查询
- 商品详情查看
- 支持峰时、谷时、平时电力等商品类型

### 订单管理
- 发布买入/卖出订单
- 订单列表查询
- 订单修改和撤销
- 订单状态实时更新

### 交易撮合
- 自动撮合引擎
- 价格优先、时间优先的撮合规则
- 支持部分成交和完全成交

### 交易记录
- 交易记录查询
- 交易统计信息
- 支持按商品、用户筛选

### 市场行情
- 实时价格行情
- 最高价/最低价
- 成交量/成交额统计

## API 接口

详细接口文档请参考 `power-trading-services/README.md`

## 开发说明

详细的功能需求请参考 `requirement.md`

## 注意事项

1. 确保MySQL服务已启动
2. 修改后端配置文件中的数据库连接信息
3. 前端通过代理访问后端API（已配置在 vite.config.ts 中）
4. 首次运行会自动创建数据库表结构和测试账号
5. 所有测试账号密码均为：**123456**
