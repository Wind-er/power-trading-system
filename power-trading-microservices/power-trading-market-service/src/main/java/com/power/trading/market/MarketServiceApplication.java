package com.power.trading.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages = {"com.power.trading.market", "com.power.trading.common.core"})
public class MarketServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketServiceApplication.class, args);
    }
}
