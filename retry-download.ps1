# 持续重试下载镜像直到成功

$maxRetries = 100
$retryCount = 0
$success = $false

Write-Host "========================================" -ForegroundColor Green
Write-Host "开始持续重试下载镜像" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

while (-not $success -and $retryCount -lt $maxRetries) {
    $retryCount++
    Write-Host "`n=== 尝试 $retryCount/$maxRetries ===" -ForegroundColor Cyan
    Write-Host "执行: docker compose up -d" -ForegroundColor White
    
    $result = docker compose up -d 2>&1
    $exitCode = $LASTEXITCODE
    
    if ($exitCode -eq 0) {
        Write-Host "`n========================================" -ForegroundColor Green
        Write-Host "✅ 所有服务启动成功！" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        
        Write-Host "`n服务状态：" -ForegroundColor Cyan
        docker compose ps
        
        $success = $true
        break
    } else {
        Write-Host "`n❌ 下载失败 (退出码: $exitCode)" -ForegroundColor Red
        Write-Host "等待 10 秒后重试..." -ForegroundColor Yellow
        Start-Sleep -Seconds 10
    }
}

if (-not $success) {
    Write-Host "`n========================================" -ForegroundColor Red
    Write-Host "❌ 达到最大重试次数 ($maxRetries)" -ForegroundColor Red
    Write-Host "请检查网络连接或手动重试" -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Red
}
