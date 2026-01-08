# CORS 问题修复说明

## 问题描述

登录时出现"网络错误，请检查网络连接"的错误。

## 问题原因

Spring Cloud Gateway 默认没有配置 CORS（跨域资源共享），导致浏览器阻止前端应用（运行在 `http://localhost:3000`）访问网关服务（运行在 `http://localhost:8000`）。

## 解决方案

### 1. 添加 CORS 配置类

创建了 `CorsConfig.java`，配置了 Spring Cloud Gateway 的 CORS 支持：

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // 允许的源（前端地址）
        corsConfig.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:5173"
        ));
        
        // 允许的HTTP方法
        corsConfig.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // 允许的请求头
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));
        
        // 允许携带凭证
        corsConfig.setAllowCredentials(true);
        
        // 预检请求的缓存时间
        corsConfig.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}
```

### 2. 更新网关配置

在 `application.yml` 中添加了全局 CORS 配置：

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins:
              - "http://localhost:3000"
              - "http://localhost:5173"
              - "http://127.0.0.1:3000"
              - "http://127.0.0.1:5173"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
              - PATCH
            allowedHeaders: "*"
            allowCredentials: true
            maxAge: 3600
```

## 验证

### CORS 预检请求测试

```powershell
# 测试 OPTIONS 请求（CORS 预检）
Invoke-WebRequest -Uri "http://localhost:8000/api/auth/login" `
  -Method OPTIONS `
  -Headers @{ "Origin" = "http://localhost:3000" }
```

**预期结果**:
- `Access-Control-Allow-Origin: http://localhost:3000`
- `Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS,PATCH`

### 登录端点测试

```powershell
# 测试登录端点
Invoke-WebRequest -Uri "http://localhost:8000/api/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"test","password":"test"}'
```

**预期结果**: 返回 400 或 401（认证失败，但端点可访问）

## 修复后的状态

✅ **CORS 配置已生效**
- 网关正确处理 CORS 预检请求（OPTIONS）
- 允许来自 `http://localhost:3000` 的跨域请求
- 支持所有必要的 HTTP 方法
- 允许携带认证凭证（Authorization 头）

✅ **网关服务正常运行**
- 端口 8000 正在监听
- 路由配置正确
- 可以访问所有微服务端点

## 下一步

1. **重启前端应用**（如果正在运行）：
   ```bash
   cd power-trading-ui
   npm run dev
   ```

2. **测试登录功能**：
   - 访问 http://localhost:3000
   - 尝试登录
   - 检查浏览器控制台，确认没有 CORS 错误

3. **验证 API 调用**：
   - 打开浏览器开发者工具
   - 查看 Network 标签
   - 确认请求成功发送到网关
   - 检查响应头中包含 CORS 相关头信息

## 常见问题

### Q: 仍然出现 CORS 错误？

**A**: 检查以下几点：
1. 确保网关服务已重启并加载新配置
2. 检查浏览器控制台的具体错误信息
3. 确认前端地址在允许的源列表中
4. 清除浏览器缓存并刷新页面

### Q: 登录仍然失败？

**A**: CORS 问题已解决，如果登录失败可能是：
1. 用户名或密码错误
2. 认证服务未正常运行
3. 数据库中没有对应的用户数据
4. JWT token 生成或验证问题

### Q: 如何添加新的前端地址？

**A**: 在 `CorsConfig.java` 和 `application.yml` 中的 `allowedOrigins` 列表中添加新地址。

## 相关文件

- `power-trading-microservices/power-trading-gateway/src/main/java/com/power/trading/gateway/config/CorsConfig.java`
- `power-trading-microservices/power-trading-gateway/src/main/resources/application.yml`
