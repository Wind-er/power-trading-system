# 快速修复 Docker 代理问题

## 问题
Docker 尝试通过 `127.0.0.1:17890` 代理连接，但代理未运行。

## 最快解决方法

### 步骤 1：打开 Docker Desktop 设置

1. 右键点击任务栏的 Docker 图标
2. 选择 "Settings"（设置）

### 步骤 2：禁用代理

1. 在左侧菜单找到 **"Resources"** → **"Proxies"**
2. 取消勾选 **"Manual proxy configuration"**（手动代理配置）
3. 或者删除所有代理服务器地址
4. 点击 **"Apply & Restart"**

### 步骤 3：等待重启

等待 Docker Desktop 完全重启（约 30 秒）

### 步骤 4：重新启动服务

在 PowerShell 中执行：

```powershell
docker compose up -d
```

## 如果找不到 Proxies 设置

某些版本的 Docker Desktop 可能将代理设置放在不同位置：

1. **Settings** → **Docker Engine** → 查看 JSON 配置中是否有代理设置
2. **Settings** → **Network** → 检查代理设置
3. **Settings** → **General** → 检查代理相关选项

## 临时解决方案（如果无法修改 Docker Desktop）

如果暂时无法修改 Docker Desktop 设置，可以尝试：

1. **检查是否有代理软件运行**（如 Clash、V2Ray 等）
2. **启动代理软件**（如果端口是 17890）
3. **或者修改代理软件端口为 17890**

## 验证修复

修复后，测试拉取镜像：

```powershell
docker pull redis:7-alpine
```

如果成功，会看到镜像下载进度。

然后启动服务：

```powershell
docker compose up -d
```
