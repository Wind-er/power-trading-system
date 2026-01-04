package com.power.trading.controller;

import com.power.trading.dto.ApiResponse;
import com.power.trading.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trades")
public class TradeController {
    @Autowired
    private TradeService tradeService;

    @GetMapping
    public ApiResponse<?> getAllTrades(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long userId) {
        if (productId != null) {
            return ApiResponse.success(tradeService.getTradesByProductId(productId));
        } else if (userId != null) {
            return ApiResponse.success(tradeService.getTradesByUserId(userId));
        } else {
            return ApiResponse.success(tradeService.getAllTrades());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getTradeById(@PathVariable Long id) {
        return tradeService.getTradeById(id)
                .map(trade -> ApiResponse.success(trade))
                .orElse(ApiResponse.error("交易记录不存在"));
    }

    @GetMapping("/statistics")
    public ApiResponse<?> getStatistics(@RequestParam(required = false) Long productId) {
        return ApiResponse.success(tradeService.getTradeStatistics(productId));
    }
}

