# 测试 Nacos 访问

Write-Host "等待 Nacos 启动..." -ForegroundColor Yellow

$maxRetries = 20
$retryCount = 0
$success = $false

while (-not $success -and $retryCount -lt $maxRetries) {
    $retryCount++
    Start-Sleep -Seconds 3
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8848/nacos" -TimeoutSec 5 -UseBasicParsing -ErrorAction Stop
        Write-Host "`n========================================" -ForegroundColor Green
        Write-Host "✅ Nacos 访问成功！" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "HTTP状态码: $($response.StatusCode)" -ForegroundColor White
        Write-Host "`n访问地址: http://localhost:8848/nacos" -ForegroundColor Cyan
        Write-Host "默认用户名/密码: nacos/nacos" -ForegroundColor Cyan
        $success = $true
        break
    } catch {
        Write-Host "尝试 $retryCount/$maxRetries: 等待中..." -ForegroundColor Yellow
    }
}

if (-not $success) {
    Write-Host "`n========================================" -ForegroundColor Red
    Write-Host "❌ Nacos 仍未就绪" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "`n检查日志..." -ForegroundColor Yellow
    docker logs nacos-server --tail 20
}
