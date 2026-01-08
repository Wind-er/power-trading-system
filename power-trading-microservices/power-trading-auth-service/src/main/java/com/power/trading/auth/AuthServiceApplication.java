package com.power.trading.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 认证服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.power.trading.auth", "com.power.trading.common.core"})
public class AuthServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
