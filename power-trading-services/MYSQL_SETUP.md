# MySQL 安装和配置说明

## MySQL 已安装

MySQL 8.4.6 已通过 winget 成功安装到您的系统。

## 配置步骤

### 1. 启动 MySQL 服务

MySQL 安装后需要启动服务。可以通过以下方式：

**方式一：通过服务管理器**
1. 按 `Win + R`，输入 `services.msc` 回车
2. 找到 `MySQL80` 或 `MySQL` 服务
3. 右键点击，选择"启动"
4. 设置启动类型为"自动"（可选）

**方式二：通过命令行（需要管理员权限）**
```powershell
# 启动 MySQL 服务
net start MySQL80

# 或者
Start-Service MySQL80
```

### 2. 设置 root 密码

MySQL 安装后，root 用户可能没有密码或使用默认密码。需要设置密码：

**方式一：通过 MySQL 命令行**
```bash
# 登录 MySQL（如果还没有密码）
mysql -u root

# 或者如果有密码
mysql -u root -p
```

然后在 MySQL 中执行：
```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
```

**方式二：如果无法登录，可能需要重置密码**
1. 停止 MySQL 服务
2. 使用 `--skip-grant-tables` 模式启动 MySQL
3. 重置密码

### 3. 创建数据库

登录 MySQL 后，执行以下命令创建数据库：

```sql
CREATE DATABASE power_trading CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 验证连接

测试数据库连接：
```bash
mysql -u root -p -e "SHOW DATABASES;"
```

## 快速配置脚本

如果 MySQL 服务已启动且 root 密码已设置，可以执行以下 SQL 脚本：

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS power_trading CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 验证
SHOW DATABASES;
USE power_trading;
```

## 应用配置

确保 `application.yml` 中的数据库配置正确：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/power_trading?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root  # 请根据您设置的密码修改
```

## 常见问题

### 问题1：找不到 MySQL 服务
- 检查服务名称：可能是 `MySQL80` 或 `MySQL`
- 重新安装 MySQL 或手动启动服务

### 问题2：无法连接数据库
- 检查 MySQL 服务是否运行
- 检查端口 3306 是否被占用
- 检查防火墙设置

### 问题3：忘记 root 密码
参考 MySQL 官方文档重置密码流程

## 下一步

1. 启动 MySQL 服务
2. 设置 root 密码
3. 创建 `power_trading` 数据库
4. 更新 `application.yml` 中的密码配置
5. 启动后端服务

