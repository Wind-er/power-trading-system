# 网络问题解决方案

## 当前问题

DNS 解析失败，无法连接到 Docker 镜像源 `docker.mirrors.ustc.edu.cn`。

## 解决方案

### 方案一：重启 Docker Desktop（推荐）

我已经临时禁用了镜像源配置。请按以下步骤操作：

1. **重启 Docker Desktop**
   - 右键点击任务栏的 Docker 图标
   - 选择 "Quit Docker Desktop"
   - 等待完全退出后，重新启动 Docker Desktop

2. **等待 Docker Desktop 完全启动**（约 30 秒）

3. **重新尝试启动服务**：
   ```powershell
   docker compose up -d
   ```

### 方案二：检查网络连接

如果重启后仍然失败，可能是网络问题：

1. **检查网络连接**：
   ```powershell
   ping docker.mirrors.ustc.edu.cn
   ```

2. **如果无法 ping 通**，可能是：
   - 网络连接问题
   - DNS 服务器问题
   - 防火墙阻止

3. **临时解决方案**：使用其他镜像源或直接使用 Docker Hub

### 方案三：使用其他镜像源

如果中科大镜像源不可用，可以修改 Docker 配置使用其他镜像源：

1. 打开 Docker Desktop
2. Settings → Docker Engine
3. 修改 `registry-mirrors` 配置：

```json
{
  "registry-mirrors": [
    "https://hub-mirror.c.163.com",
    "https://mirror.baidubce.com"
  ]
}
```

4. 点击 "Apply & Restart"

### 方案四：直接使用 Docker Hub（最慢但最可靠）

如果所有镜像源都不可用，可以完全禁用镜像源：

1. 打开 Docker Desktop
2. Settings → Docker Engine
3. 删除或注释掉 `registry-mirrors` 配置
4. 点击 "Apply & Restart"

**注意**：直接使用 Docker Hub 可能会很慢，但最可靠。

## 当前状态

- ✅ Redis 镜像：已下载
- ✅ Seata 镜像：已下载
- ❌ MySQL 镜像：下载失败（DNS 解析失败）
- ❌ Nacos 镜像：下载失败（DNS 解析失败）

## 下一步

1. **重启 Docker Desktop**
2. **等待完全启动**
3. **重新执行**：`docker compose up -d`

如果问题持续，请检查：
- 网络连接是否正常
- DNS 服务器是否可用
- 防火墙设置
