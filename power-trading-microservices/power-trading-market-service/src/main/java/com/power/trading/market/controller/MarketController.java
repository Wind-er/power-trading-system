package com.power.trading.market.controller;

import com.power.trading.market.dto.MarketQuote;
import com.power.trading.market.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/market")
public class MarketController {
    
    @Autowired
    private MarketService marketService;
    
    @GetMapping("/quote/{productId}")
    public Map<String, Object> getQuote(@PathVariable Long productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            MarketQuote quote = marketService.getQuote(productId);
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", quote);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    @GetMapping("/history/{productId}")
    public Map<String, Object> getHistory(@PathVariable Long productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> history = marketService.getHistory(productId);
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", history);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
}
