$body = @{
    username = "buyer1"
    password = "123456"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $body -ContentType "application/json"
    Write-Host "登录成功！"
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "登录失败: $($_.Exception.Message)"
    if ($_.ErrorDetails) {
        Write-Host "错误详情: $($_.ErrorDetails.Message)"
    }
}

