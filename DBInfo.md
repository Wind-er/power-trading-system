# 数据库配置信息

## 📊 数据库概览

本系统支持两种数据库配置，通过 Spring Profile 进行切换：

1. **开发环境（dev）**：使用 H2 内存数据库（无需安装，开箱即用）
2. **生产环境（prod）**：使用 MySQL 数据库（需要安装 MySQL）

---

## 🔧 当前使用的数据库

### 开发环境（默认）

**数据库类型**：H2 内存数据库  
**Profile**：`dev`  
**配置文件**：`application-dev.yml`

#### 连接信息

```
数据库类型: H2 Database (内存数据库)
JDBC URL: jdbc:h2:mem:power_trading
驱动类: org.h2.Driver
用户名: sa
密码: (空)
数据库名称: power_trading (内存)
物理路径: 无（内存数据库，无物理文件）
```

#### 特点

- ✅ **无需安装**：H2 是嵌入式数据库，随应用启动
- ✅ **快速开发**：适合开发和测试环境
- ⚠️ **数据不持久化**：应用重启后数据会丢失
- ⚠️ **无物理文件**：数据存储在内存中，没有磁盘文件
- ✅ **H2 Console 可用**：可通过 Web 界面访问数据库

#### ⚠️ 重要说明：当前使用内存数据库

**当前配置使用的是 H2 内存数据库（`jdbc:h2:mem:power_trading`）**，这意味着：

- ❌ **没有物理文件**：数据完全存储在内存（RAM）中
- ❌ **无物理路径**：不存在任何数据库文件
- ⚠️ **数据易失性**：应用关闭后所有数据都会丢失
- ✅ **适合开发测试**：快速启动，无需管理文件

#### 如何切换到文件数据库（持久化）

如果需要数据持久化，可以修改 `application-dev.yml`：

**修改前（内存数据库）：**
```yaml
datasource:
  url: jdbc:h2:mem:power_trading
```

**修改后（文件数据库）：**
```yaml
datasource:
  url: jdbc:h2:file:./data/power_trading
  # 或者指定绝对路径
  # url: jdbc:h2:file:D:/repo/power-trading-system/power-trading-services/data/power_trading
```

**文件数据库的物理路径：**

1. **相对路径**：`jdbc:h2:file:./data/power_trading`
   - 物理路径：`项目根目录/data/power_trading.mv.db`
   - 例如：`D:\repo\power-trading-system\power-trading-services\data\power_trading.mv.db`

2. **绝对路径**：`jdbc:h2:file:D:/data/power_trading`
   - 物理路径：`D:/data/power_trading.mv.db`

3. **用户目录**：`jdbc:h2:file:~/power_trading`
   - Windows: `C:\Users\用户名\power_trading.mv.db`
   - Linux/Mac: `~/power_trading.mv.db`

**文件数据库特点：**
- ✅ 数据持久化：应用重启后数据保留
- ✅ 有物理文件：`.mv.db` 文件存储数据
- ✅ 可以备份：可以复制 `.mv.db` 文件进行备份

#### H2 Console 访问

- **访问地址**：`http://localhost:8080/api/h2-console`
- **JDBC URL**：`jdbc:h2:mem:power_trading`（内存数据库）
- **用户名**：`sa`
- **密码**：（留空）

**注意**：如果切换到文件数据库，H2 Console 的 JDBC URL 也需要相应修改：
- 文件数据库：`jdbc:h2:file:./data/power_trading`

---

## 🗄️ 生产环境数据库

**数据库类型**：MySQL 8.x  
**Profile**：`prod`  
**配置文件**：`application.yml`

#### 连接信息

```
数据库类型: MySQL 8.x
JDBC URL: jdbc:mysql://localhost:3306/power_trading?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
驱动类: com.mysql.cj.jdbc.Driver
主机: localhost
端口: 3306
数据库名: power_trading
用户名: root
密码: root
字符编码: UTF-8
时区: Asia/Shanghai
SSL: 禁用
```

#### MySQL 配置说明

- **主机地址**：`localhost`
- **端口**：`3306`（MySQL 默认端口）
- **数据库名**：`power_trading`
- **字符集**：`UTF-8`（支持中文）
- **时区**：`Asia/Shanghai`（中国时区）

---

## 🔄 环境切换

### 方式一：通过环境变量

**Windows PowerShell:**
```powershell
$env:SPRING_PROFILES_ACTIVE="dev"  # 使用H2
$env:SPRING_PROFILES_ACTIVE="prod" # 使用MySQL
mvn spring-boot:run
```

**Linux/Mac:**
```bash
export SPRING_PROFILES_ACTIVE=dev  # 使用H2
export SPRING_PROFILES_ACTIVE=prod # 使用MySQL
mvn spring-boot:run
```

### 方式二：通过 Maven 参数

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev   # 使用H2
mvn spring-boot:run -Dspring-boot.run.profiles=prod # 使用MySQL
```

### 方式三：通过 IDE 配置

在运行配置中添加 VM options：
```
-Dspring.profiles.active=dev   # 使用H2
-Dspring.profiles.active=prod # 使用MySQL
```

---

## 📦 数据库依赖

### H2 数据库

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

- **版本**：由 Spring Boot 管理
- **作用域**：runtime（运行时）

### MySQL 数据库

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.2.0</version>
</dependency>
```

- **版本**：8.2.0
- **驱动类**：`com.mysql.cj.jdbc.Driver`

---

## 🗃️ JPA/Hibernate 配置

### 开发环境（H2）

```yaml
jpa:
  hibernate:
    ddl-auto: update  # 自动更新表结构
  show-sql: true      # 显示SQL语句
  properties:
    hibernate:
      dialect: org.hibernate.dialect.H2Dialect
      format_sql: true
```

### 生产环境（MySQL）

```yaml
jpa:
  hibernate:
    ddl-auto: update  # 自动更新表结构
  show-sql: true      # 显示SQL语句
  properties:
    hibernate:
      dialect: org.hibernate.dialect.MySQL8Dialect
      format_sql: true
```

### DDL 模式说明

- **`update`**：自动更新表结构，如果表不存在则创建，如果表存在则更新
- **`create`**：每次启动都删除并重新创建表（⚠️ 会丢失数据）
- **`create-drop`**：启动时创建表，关闭时删除表
- **`validate`**：只验证表结构，不修改
- **`none`**：不执行任何操作

---

## 📋 数据库表结构

系统包含以下核心表：

### 1. users（用户表）
- `id`：用户ID（主键）
- `username`：用户名（唯一）
- `password`：密码（BCrypt加密）
- `user_type`：用户类型（BUYER/GENERATOR/ADMIN）
- `company_name`：企业名称
- `contact_info`：联系方式
- `status`：状态（ACTIVE/INACTIVE）
- `create_time`：创建时间
- `update_time`：更新时间

### 2. products（商品表）
- `id`：商品ID（主键）
- `name`：商品名称
- `type`：商品类型（PEAK/VALLEY/FLAT）
- `unit`：单位
- `description`：描述
- `status`：状态（ACTIVE/INACTIVE）
- `create_time`：创建时间
- `update_time`：更新时间

### 3. orders（订单表）
- `id`：订单ID（主键）
- `user_id`：用户ID（外键）
- `product_id`：商品ID（外键）
- `order_type`：订单类型（BUY/SELL）
- `quantity`：电量（MWh）
- `price`：价格（元/MWh）
- `status`：状态（PENDING/PARTIAL/FILLED/CANCELLED）
- `remaining_quantity`：剩余电量
- `create_time`：创建时间
- `update_time`：更新时间

### 4. trades（交易表）
- `id`：交易ID（主键）
- `buy_order_id`：买入订单ID（外键）
- `sell_order_id`：卖出订单ID（外键）
- `product_id`：商品ID（外键）
- `quantity`：成交电量（MWh）
- `price`：成交价格（元/MWh）
- `buyer_id`：买方用户ID（外键）
- `seller_id`：卖方用户ID（外键）
- `trade_time`：成交时间

---

## 🔐 安全注意事项

### 开发环境（H2）

- ✅ 仅用于开发和测试
- ⚠️ 数据不持久化，重启后丢失
- ✅ 无需配置，开箱即用

### 生产环境（MySQL）

⚠️ **重要安全建议**：

1. **修改默认密码**：
   - 当前配置：用户名 `root`，密码 `root`
   - **建议**：修改为强密码

2. **创建专用数据库用户**：
   ```sql
   CREATE USER 'power_trading'@'localhost' IDENTIFIED BY 'your_strong_password';
   GRANT ALL PRIVILEGES ON power_trading.* TO 'power_trading'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **使用环境变量存储敏感信息**：
   ```yaml
   spring:
     datasource:
       username: ${DB_USERNAME:root}
       password: ${DB_PASSWORD:root}
   ```

4. **启用 SSL 连接**（生产环境）：
   ```yaml
   url: jdbc:mysql://localhost:3306/power_trading?useSSL=true&requireSSL=true
   ```

---

## 🚀 数据库初始化

### 自动初始化

系统启动时会自动执行 `DataInitializer` 类，初始化以下数据：

1. **测试用户**（5个）：
   - buyer1 / 123456（购电商）
   - buyer2 / 123456（购电商）
   - generator1 / 123456（发电商）
   - generator2 / 123456（发电商）
   - admin / 123456（管理员）

2. **商品数据**（3个）：
   - 峰时电力（PEAK）
   - 谷时电力（VALLEY）
   - 平时电力（FLAT）

3. **历史订单和交易数据**：
   - 每个用户约5-6个订单
   - 每个商品约60笔交易记录
   - 覆盖过去7天的数据

### 初始化条件

- **H2 数据库**：每次启动都会初始化（因为数据不持久化）
- **MySQL 数据库**：只在首次启动时初始化（如果表中已有数据则跳过）

---

## 📝 数据库迁移

### H2 数据库

- 使用 JPA `ddl-auto: update` 自动管理表结构
- 无需手动执行 SQL 脚本

### MySQL 数据库

- 使用 JPA `ddl-auto: update` 自动管理表结构
- 首次启动会自动创建表
- 后续启动会自动更新表结构（如果实体类有变化）

---

## 🔍 查看数据库内容

### H2 Console（开发环境）

1. 启动应用（使用 dev profile）
2. 访问：`http://localhost:8080/api/h2-console`
3. 输入连接信息：
   - JDBC URL: `jdbc:h2:mem:power_trading`
   - 用户名: `sa`
   - 密码: （留空）
4. 点击 "Connect" 连接数据库
5. 可以执行 SQL 查询，例如：
   ```sql
   SELECT * FROM users;
   SELECT * FROM products;
   SELECT * FROM orders;
   SELECT * FROM trades;
   ```

### MySQL（生产环境）

使用 MySQL 客户端工具连接：
- **命令行**：`mysql -u root -p`
- **图形工具**：MySQL Workbench, Navicat, DBeaver 等
- **连接信息**：见上方"生产环境数据库"部分

---

## 📊 当前运行状态

### 检查当前使用的数据库

查看应用启动日志，会显示：
- H2: `H2 database available at '/h2-console'`
- MySQL: `DataSource initialized using driver class 'com.mysql.cj.jdbc.Driver'`

### 检查数据库连接

**H2:**
```bash
curl http://localhost:8080/api/h2-console
```

**MySQL:**
```bash
mysql -u root -p -e "USE power_trading; SHOW TABLES;"
```

---

## 🛠️ 故障排查

### 问题1：H2 Console 无法访问

**原因**：可能被 Security 拦截  
**解决**：检查 `SecurityConfig` 中是否允许 `/h2-console/**` 路径

### 问题2：MySQL 连接失败

**检查项**：
1. MySQL 服务是否启动
2. 数据库 `power_trading` 是否存在
3. 用户名密码是否正确
4. 端口 3306 是否开放

**创建数据库**：
```sql
CREATE DATABASE power_trading CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 问题3：表结构不更新

**原因**：`ddl-auto` 设置为 `validate` 或 `none`  
**解决**：改为 `update` 或 `create`

---

## 📚 相关文档

- [H2 Database 官方文档](https://www.h2database.com/html/main.html)
- [MySQL 官方文档](https://dev.mysql.com/doc/)
- [Spring Data JPA 文档](https://spring.io/projects/spring-data-jpa)
- [Hibernate 文档](https://hibernate.org/orm/documentation/)

---

## 📝 配置总结

| 配置项 | 开发环境（dev） | 生产环境（prod） |
|--------|----------------|------------------|
| 数据库类型 | H2 内存数据库 | MySQL 8.x |
| JDBC URL | `jdbc:h2:mem:power_trading` | `jdbc:mysql://localhost:3306/power_trading` |
| 用户名 | `sa` | `root` |
| 密码 | （空） | `root` |
| **物理路径** | **❌ 无（内存数据库）** | **MySQL 数据目录** |
| 数据持久化 | ❌ 否 | ✅ 是 |
| H2 Console | ✅ 可用 | ❌ 不可用 |
| 需要安装 | ❌ 否 | ✅ 是 |

---

---

## 📁 H2 数据库物理路径详解

### ⚠️ 当前配置：内存数据库（无物理路径）

**当前使用的配置**：`jdbc:h2:mem:power_trading`

**物理路径**：**❌ 无（数据存储在内存中，不存在物理文件）**

这意味着：
- 数据完全存储在 RAM（内存）中
- 应用关闭后所有数据都会丢失
- 不会在磁盘上创建任何文件
- 适合快速开发和测试

### 🔄 切换到文件数据库（持久化）

如果需要数据持久化，可以修改 `application-dev.yml`：

#### 配置方式

**方案1：项目目录下（推荐）**
```yaml
datasource:
  url: jdbc:h2:file:./data/power_trading
```

**物理路径**：
- Windows: `D:\repo\power-trading-system\power-trading-services\data\power_trading.mv.db`
- Linux/Mac: `项目目录/data/power_trading.mv.db`

**方案2：绝对路径**
```yaml
datasource:
  url: jdbc:h2:file:D:/data/power_trading
```

**物理路径**：`D:/data/power_trading.mv.db`

**方案3：用户目录**
```yaml
datasource:
  url: jdbc:h2:file:~/power_trading
```

**物理路径**：
- Windows: `C:\Users\你的用户名\power_trading.mv.db`
- Linux/Mac: `~/power_trading.mv.db`

### 📄 H2 数据库文件说明

使用文件数据库时，H2 会创建以下文件：

1. **`power_trading.mv.db`**：主数据库文件（包含所有数据）
2. **`power_trading.trace.db`**：跟踪日志文件（可选，用于调试）

### 🔍 如何检查当前数据库类型

**方法1：查看配置文件**
```bash
# 查看 application-dev.yml
cat power-trading-services/src/main/resources/application-dev.yml | grep "jdbc:h2"
```

**方法2：查看应用启动日志**
- 内存数据库：`H2 database available at '/h2-console'`
- 文件数据库：会显示文件路径信息

**方法3：检查文件系统**
```bash
# 如果使用文件数据库，会在指定路径找到 .mv.db 文件
# 如果使用内存数据库，不会有任何文件
```

**方法4：通过 H2 Console 查看**
访问 `http://localhost:8080/api/h2-console`，JDBC URL 会显示：
- 内存数据库：`jdbc:h2:mem:power_trading`
- 文件数据库：`jdbc:h2:file:./data/power_trading`（或你配置的路径）

---

**最后更新**：2026-01-04  
**维护者**：Power Trading System Team

