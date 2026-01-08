# 电力交易系统微服务停止脚本 (Windows PowerShell)

Write-Host "========================================" -ForegroundColor Yellow
Write-Host "停止电力交易系统微服务" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Yellow

# 停止所有 Java 进程（Spring Boot 应用）
Write-Host "`n查找并停止 Spring Boot 服务..." -ForegroundColor Cyan

$javaProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue | Where-Object {
    $_.CommandLine -like "*power-trading*" -or 
    $_.CommandLine -like "*spring-boot*"
}

if ($javaProcesses) {
    foreach ($process in $javaProcesses) {
        Write-Host "停止进程: $($process.ProcessName) (PID: $($process.Id))" -ForegroundColor Yellow
        Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue
    }
    Write-Host "所有服务已停止" -ForegroundColor Green
} else {
    Write-Host "未找到运行中的服务" -ForegroundColor Yellow
}

# 询问是否停止基础设施服务
Write-Host "`n是否停止基础设施服务 (Nacos, MySQL, Redis, Seata)? (Y/N)" -ForegroundColor Cyan
$response = Read-Host

if ($response -eq "Y" -or $response -eq "y") {
    Write-Host "`n停止基础设施服务..." -ForegroundColor Yellow
    docker-compose down
    Write-Host "基础设施服务已停止" -ForegroundColor Green
} else {
    Write-Host "基础设施服务继续运行" -ForegroundColor Yellow
}

Write-Host "`n完成！" -ForegroundColor Green
