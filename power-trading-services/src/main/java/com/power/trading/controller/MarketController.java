package com.power.trading.controller;

import com.power.trading.dto.ApiResponse;
import com.power.trading.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/market")
public class MarketController {
    @Autowired
    private MarketService marketService;

    @GetMapping("/quote/{productId}")
    public ApiResponse<?> getQuote(@PathVariable Long productId) {
        return ApiResponse.success(marketService.getQuote(productId));
    }

    @GetMapping("/history/{productId}")
    public ApiResponse<?> getHistory(@PathVariable Long productId) {
        return ApiResponse.success(marketService.getHistory(productId));
    }
}

