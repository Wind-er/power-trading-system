# 修复 Docker 镜像源问题

Write-Host "========================================" -ForegroundColor Yellow
Write-Host "Docker 镜像源配置修复脚本" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Yellow

Write-Host "`n问题诊断：" -ForegroundColor Cyan
Write-Host "Docker 配置了镜像源，但代理设置有问题（127.0.0.1:17890）" -ForegroundColor Yellow

Write-Host "`n解决方案：" -ForegroundColor Cyan
Write-Host "1. 打开 Docker Desktop" -ForegroundColor White
Write-Host "2. 点击设置图标（齿轮）" -ForegroundColor White
Write-Host "3. 进入 'Docker Engine' 或 'Resources' -> 'Proxies'" -ForegroundColor White
Write-Host "4. 检查代理设置，如果不需要代理，请禁用或删除代理配置" -ForegroundColor White
Write-Host "5. 或者修改镜像源配置" -ForegroundColor White

Write-Host "`n或者，您可以手动编辑 Docker Desktop 配置文件：" -ForegroundColor Cyan
$dockerConfigPath = "$env:USERPROFILE\.docker\daemon.json"
Write-Host "配置文件路径: $dockerConfigPath" -ForegroundColor Yellow

if (Test-Path $dockerConfigPath) {
    Write-Host "`n当前配置文件内容：" -ForegroundColor Cyan
    Get-Content $dockerConfigPath | Write-Host
} else {
    Write-Host "`n配置文件不存在，将创建新配置" -ForegroundColor Yellow
}

Write-Host "`n推荐的 Docker 配置（无代理，使用国内镜像源）：" -ForegroundColor Green
$recommendedConfig = @{
    "registry-mirrors" = @(
        "https://docker.mirrors.ustc.edu.cn",
        "https://hub-mirror.c.163.com",
        "https://mirror.baidubce.com"
    )
} | ConvertTo-Json -Depth 10

Write-Host $recommendedConfig -ForegroundColor White

Write-Host "`n是否要应用推荐配置？(Y/N)" -ForegroundColor Cyan
$response = Read-Host

if ($response -eq "Y" -or $response -eq "y") {
    try {
        $recommendedConfig | Out-File -FilePath $dockerConfigPath -Encoding UTF8 -Force
        Write-Host "`n配置已更新！请重启 Docker Desktop 使配置生效。" -ForegroundColor Green
    } catch {
        Write-Host "`n错误: 无法写入配置文件 - $_" -ForegroundColor Red
        Write-Host "请手动编辑配置文件或通过 Docker Desktop GUI 修改" -ForegroundColor Yellow
    }
} else {
    Write-Host "`n请手动修改 Docker Desktop 设置：" -ForegroundColor Yellow
    Write-Host "1. 打开 Docker Desktop" -ForegroundColor White
    Write-Host "2. Settings -> Docker Engine" -ForegroundColor White
    Write-Host "3. 删除或注释掉代理相关配置" -ForegroundColor White
    Write-Host "4. 应用并重启 Docker Desktop" -ForegroundColor White
}

Write-Host "`n完成！" -ForegroundColor Green
