package com.power.trading.common.feign;

import com.power.trading.common.core.result.Result;
import com.power.trading.common.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "user-service", path = "/api/users")
public interface UserServiceClient {
    
    @GetMapping("/{id}")
    Result<UserDTO> getUserById(@PathVariable Long id);
    
    @GetMapping("/validate/{id}")
    Result<Boolean> validateUser(@PathVariable Long id);
}
