# 电力交易系统微服务启动脚本 (Windows PowerShell)

Write-Host "========================================" -ForegroundColor Green
Write-Host "电力交易系统微服务启动脚本" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# 检查 Docker 是否运行
Write-Host "`n检查 Docker 服务状态..." -ForegroundColor Yellow
$dockerRunning = docker ps 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "错误: Docker 未运行，请先启动 Docker Desktop" -ForegroundColor Red
    exit 1
}

# 检查基础设施服务
Write-Host "`n检查基础设施服务..." -ForegroundColor Yellow
$nacosRunning = docker ps --filter "name=nacos-server" --format "{{.Names}}"
if (-not $nacosRunning) {
    Write-Host "Starting infrastructure services (Nacos, MySQL, Redis, Seata)..." -ForegroundColor Yellow
    docker-compose up -d
    Write-Host "等待服务启动..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30
} else {
    Write-Host "基础设施服务已运行" -ForegroundColor Green
}

# 检查 Nacos 是否可用
Write-Host "`n检查 Nacos 服务..." -ForegroundColor Yellow
$maxRetries = 30
$retryCount = 0
while ($retryCount -lt $maxRetries) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8848/nacos" -TimeoutSec 2 -UseBasicParsing -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            Write-Host "Nacos 服务已就绪" -ForegroundColor Green
            break
        }
    } catch {
        $retryCount++
        Write-Host "等待 Nacos 启动... ($retryCount/$maxRetries)" -ForegroundColor Yellow
        Start-Sleep -Seconds 2
    }
}

if ($retryCount -eq $maxRetries) {
    Write-Host "警告: Nacos 服务可能未完全启动，请手动检查" -ForegroundColor Yellow
}

# 设置 Maven 路径（如果不在 PATH 中）
$mavenCmd = "mvn"
if (-not (Get-Command $mavenCmd -ErrorAction SilentlyContinue)) {
    Write-Host "错误: 未找到 Maven，请确保 Maven 已安装并添加到 PATH" -ForegroundColor Red
    exit 1
}

# 获取脚本所在目录
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = $scriptDir

Write-Host "`n项目根目录: $projectRoot" -ForegroundColor Cyan

# 定义服务列表和启动顺序
$services = @(
    @{Name="user-service"; Port=8001; Path="power-trading-user-service"},
    @{Name="auth-service"; Port=8002; Path="power-trading-auth-service"},
    @{Name="product-service"; Port=8003; Path="power-trading-product-service"},
    @{Name="order-service"; Port=8004; Path="power-trading-order-service"},
    @{Name="matching-service"; Port=8005; Path="power-trading-matching-service"},
    @{Name="trade-service"; Port=8006; Path="power-trading-trade-service"},
    @{Name="market-service"; Port=8007; Path="power-trading-market-service"},
    @{Name="gateway-service"; Port=8000; Path="power-trading-gateway"}
)

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "开始启动微服务" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# 启动服务
foreach ($service in $services) {
    $servicePath = Join-Path $projectRoot $service.Path
    
    if (-not (Test-Path $servicePath)) {
        Write-Host "`n警告: 服务路径不存在: $servicePath" -ForegroundColor Yellow
        continue
    }
    
    Write-Host "`n启动 $($service.Name) (端口: $($service.Port))..." -ForegroundColor Cyan
    
    # 在新窗口中启动服务
    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.FileName = "powershell"
    $startInfo.Arguments = "-NoExit -Command `"cd '$servicePath'; Write-Host '启动 $($service.Name)...' -ForegroundColor Green; mvn spring-boot:run`""
    $startInfo.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Normal
    $process = [System.Diagnostics.Process]::Start($startInfo)
    
    # 等待服务启动
    Start-Sleep -Seconds 5
}

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "所有服务启动完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "`n服务访问地址:" -ForegroundColor Cyan
Write-Host "  - API网关: http://localhost:8000" -ForegroundColor White
Write-Host "  - Nacos控制台: http://localhost:8848/nacos (nacos/nacos)" -ForegroundColor White
Write-Host "`n提示: 每个服务都在独立的 PowerShell 窗口中运行" -ForegroundColor Yellow
Write-Host "      Close the window to stop the corresponding service" -ForegroundColor Yellow
