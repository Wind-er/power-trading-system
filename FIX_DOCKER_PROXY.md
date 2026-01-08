# 修复 Docker 镜像拉取失败问题

## 问题描述

错误信息：
```
failed to resolve reference "docker.io/library/redis:7-alpine": 
failed to do request: Head "https://docker.mirrors.ustc.edu.cn/...": 
connecting to 127.0.0.1:17890: dial tcp 127.0.0.1:17890: 
connectex: No connection could be made because the target machine actively refused it.
```

**原因**：Docker Desktop 配置了代理（127.0.0.1:17890），但该代理服务未运行。

## 解决方案

### 方法一：在 Docker Desktop 中禁用代理（推荐）

1. **打开 Docker Desktop**
2. **点击设置图标**（右上角齿轮图标）
3. **进入 "Resources" -> "Proxies"**
4. **取消勾选 "Manual proxy configuration"** 或删除代理设置
5. **点击 "Apply & Restart"**
6. **等待 Docker Desktop 重启完成**

### 方法二：修改 Docker Desktop 配置文件

1. **关闭 Docker Desktop**

2. **编辑配置文件**：
   - 路径：`%USERPROFILE%\.docker\daemon.json`
   - 或者：`C:\Users\你的用户名\.docker\daemon.json`

3. **确保配置文件中没有代理相关设置**，当前配置应该是：
   ```json
   {
     "builder": {
       "gc": {
         "defaultKeepStorage": "20GB",
         "enabled": true
       }
     },
     "experimental": false,
     "registry-mirrors": [
       "https://docker.mirrors.ustc.edu.cn",
       "https://hub-mirror.c.163.com",
       "https://mirror.baidubce.com",
       "https://ccr.ccs.tencentyun.com"
     ]
   }
   ```

4. **重新启动 Docker Desktop**

### 方法三：临时禁用代理（如果使用系统代理）

如果您的系统设置了 HTTP_PROXY 环境变量：

1. **检查环境变量**：
   ```powershell
   echo $env:HTTP_PROXY
   echo $env:HTTPS_PROXY
   ```

2. **临时取消代理**（仅当前 PowerShell 会话）：
   ```powershell
   $env:HTTP_PROXY = ""
   $env:HTTPS_PROXY = ""
   $env:http_proxy = ""
   $env:https_proxy = ""
   ```

3. **重新尝试拉取镜像**：
   ```powershell
   docker compose up -d
   ```

## 验证修复

修复后，验证 Docker 是否正常工作：

```powershell
# 测试拉取镜像
docker pull redis:7-alpine

# 如果成功，应该看到类似输出：
# 7-alpine: Pulling from library/redis
# ...
# Status: Downloaded newer image for redis:7-alpine
```

## 重新启动基础设施服务

修复后，重新启动基础设施：

```powershell
docker compose up -d
```

等待所有服务启动完成（约 1-2 分钟），然后检查：

```powershell
docker ps
```

应该看到 4 个容器运行：
- `nacos-server`
- `mysql-server`
- `redis-server`
- `seata-server`

## 如果问题仍然存在

1. **检查防火墙设置**：确保防火墙没有阻止 Docker
2. **检查网络连接**：确保可以访问互联网
3. **尝试使用官方源**：临时禁用镜像源，直接使用 Docker Hub
4. **查看 Docker Desktop 日志**：Help -> Troubleshoot -> View logs

## 快速修复脚本

运行以下脚本可以自动检查和修复：

```powershell
.\fix-docker-mirror.ps1
```
