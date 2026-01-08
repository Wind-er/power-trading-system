# 数据初始化说明

## 概述

系统启动时会自动初始化测试数据，包括用户、商品、订单和交易记录。

## 初始化数据详情

### 1. 用户数据（Auth Service）

**测试账号**（密码均为：`123456`）：

| 用户名 | 密码 | 用户类型 | 说明 |
|--------|------|----------|------|
| buyer1 | 123456 | 购电商 | 测试购电公司A |
| buyer2 | 123456 | 购电商 | 测试购电公司B |
| generator1 | 123456 | 发电商 | 测试发电公司A |
| generator2 | 123456 | 发电商 | 测试发电公司B |
| admin | 123456 | 管理员 | 系统管理员 |

**初始化位置**：`power-trading-auth-service/src/main/java/com/power/trading/auth/config/DataInitializer.java`

### 2. 商品数据（Product Service）

**商品列表**：

| ID | 名称 | 类型 | 单位 | 说明 |
|----|------|------|------|------|
| 1 | 峰时电力 | PEAK | MWh | 用电高峰时段的电力 |
| 2 | 谷时电力 | VALLEY | MWh | 用电低谷时段的电力 |
| 3 | 平时电力 | FLAT | MWh | 正常时段的电力 |

**初始化位置**：`power-trading-product-service/src/main/java/com/power/trading/product/config/DataInitializer.java`

### 3. 订单数据（Order Service）

**订单类型**：
- **待撮合订单**：8条（4条买入订单，4条卖出订单）
- **已成交订单**：4条（历史成交记录）
- **部分成交订单**：1条（已成交100 MWh，剩余100 MWh）

**订单分布**：
- buyer1 (用户ID: 1): 买入订单
- buyer2 (用户ID: 2): 买入订单
- generator1 (用户ID: 3): 卖出订单
- generator2 (用户ID: 4): 卖出订单

**价格范围**：
- 峰时电力：480-550 元/MWh
- 谷时电力：320-350 元/MWh
- 平时电力：440-480 元/MWh

**初始化位置**：`power-trading-order-service/src/main/java/com/power/trading/order/config/DataInitializer.java`

### 4. 交易数据（Trade Service）

**交易记录**：
- **历史交易**：约 62 条交易记录
- **分布**：每个商品约 20 条历史交易记录
- **时间跨度**：过去 7 天的交易数据
- **价格波动**：模拟真实市场价格波动

**用途**：
- 用于首页价格走势图展示
- 用于市场行情分析
- 用于交易历史查询

**初始化位置**：`power-trading-trade-service/src/main/java/com/power/trading/trade/config/DataInitializer.java`

## 初始化顺序

由于微服务架构中服务之间存在依赖关系，初始化顺序如下：

1. **Auth Service** → 创建用户数据（用户ID: 1-5）
2. **Product Service** → 创建商品数据（商品ID: 1-3）
3. **Order Service** → 创建订单数据（依赖用户ID和商品ID）
4. **Trade Service** → 创建交易数据（依赖订单ID）

**注意**：Order Service 和 Trade Service 的初始化器包含延迟（5-10秒），以确保依赖的数据已创建完成。

## 数据验证

### 检查用户数据
```sql
USE power_trading_user;
SELECT id, username, user_type, status FROM users;
```

### 检查商品数据
```sql
USE power_trading_product;
SELECT id, name, type, status FROM products;
```

### 检查订单数据
```sql
USE power_trading_order;
SELECT COUNT(*) as order_count FROM orders;
SELECT id, user_id, product_id, order_type, quantity, price, status FROM orders LIMIT 10;
```

### 检查交易数据
```sql
USE power_trading_trade;
SELECT COUNT(*) as trade_count FROM trades;
SELECT id, product_id, quantity, price, trade_time FROM trades ORDER BY trade_time DESC LIMIT 10;
```

## 使用 Docker 命令检查

```bash
# 检查用户
docker exec mysql-server mysql -uroot -proot123 power_trading_user -e "SELECT * FROM users;"

# 检查商品
docker exec mysql-server mysql -uroot -proot123 power_trading_product -e "SELECT * FROM products;"

# 检查订单
docker exec mysql-server mysql -uroot -proot123 power_trading_order -e "SELECT COUNT(*) FROM orders;"

# 检查交易
docker exec mysql-server mysql -uroot -proot123 power_trading_trade -e "SELECT COUNT(*) FROM trades;"
```

## 数据特点

1. **真实性**：价格有合理波动，模拟真实市场
2. **完整性**：覆盖所有商品类型和订单状态
3. **多样性**：包含待撮合、已成交、部分成交等不同状态的订单
4. **时间分布**：数据分布在过去7天内，可用于时间序列分析

## 注意事项

1. **首次启动**：数据只在首次启动时初始化（如果数据已存在则跳过）
2. **数据持久化**：使用 MySQL 数据库，数据会持久保存
3. **服务依赖**：确保服务按正确顺序启动，以便数据初始化器正常工作
4. **用户ID映射**：
   - buyer1 → 用户ID: 1
   - buyer2 → 用户ID: 2
   - generator1 → 用户ID: 3
   - generator2 → 用户ID: 4
   - admin → 用户ID: 5

## 测试场景

### 场景1：查看商品列表
1. 使用任意账号登录
2. 访问商品页面
3. 应该看到3个商品（峰时、谷时、平时电力）

### 场景2：查看订单列表
1. 使用 buyer1 登录
2. 访问订单页面
3. 应该看到该用户的买入订单

### 场景3：查看交易记录
1. 使用任意账号登录
2. 访问交易记录页面
3. 应该看到历史交易记录

### 场景4：查看价格走势
1. 访问首页
2. 选择商品查看价格走势图
3. 应该看到过去7天的价格波动数据

## 文件位置

- **Auth Service DataInitializer**: `power-trading-auth-service/src/main/java/com/power/trading/auth/config/DataInitializer.java`
- **Product Service DataInitializer**: `power-trading-product-service/src/main/java/com/power/trading/product/config/DataInitializer.java`
- **Order Service DataInitializer**: `power-trading-order-service/src/main/java/com/power/trading/order/config/DataInitializer.java`
- **Trade Service DataInitializer**: `power-trading-trade-service/src/main/java/com/power/trading/trade/config/DataInitializer.java`
