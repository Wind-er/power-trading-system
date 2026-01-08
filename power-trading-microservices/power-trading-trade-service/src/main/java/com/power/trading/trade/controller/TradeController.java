package com.power.trading.trade.controller;

import com.power.trading.trade.entity.Trade;
import com.power.trading.trade.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trades")
public class TradeController {
    
    @Autowired
    private TradeRepository tradeRepository;
    
    @GetMapping
    public Map<String, Object> getAll(@RequestParam(required = false) Long productId,
                                      @RequestParam(required = false) Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Trade> trades;
            
            if (productId != null) {
                trades = tradeRepository.findByProductId(productId);
            } else if (userId != null) {
                trades = tradeRepository.findByBuyerIdOrSellerId(userId, userId);
            } else {
                trades = tradeRepository.findAll();
            }
            
            // 转换为 Map 格式以便 JSON 序列化
            List<Map<String, Object>> tradeList = trades.stream().map(trade -> {
                Map<String, Object> tradeMap = new HashMap<>();
                tradeMap.put("id", trade.getId());
                tradeMap.put("buyOrderId", trade.getBuyOrderId());
                tradeMap.put("sellOrderId", trade.getSellOrderId());
                tradeMap.put("productId", trade.getProductId());
                tradeMap.put("quantity", trade.getQuantity());
                tradeMap.put("price", trade.getPrice());
                tradeMap.put("buyerId", trade.getBuyerId());
                tradeMap.put("sellerId", trade.getSellerId());
                tradeMap.put("tradeTime", trade.getTradeTime());
                return tradeMap;
            }).collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", tradeList);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Trade trade = tradeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("交易记录不存在"));
            
            Map<String, Object> tradeMap = new HashMap<>();
            tradeMap.put("id", trade.getId());
            tradeMap.put("buyOrderId", trade.getBuyOrderId());
            tradeMap.put("sellOrderId", trade.getSellOrderId());
            tradeMap.put("productId", trade.getProductId());
            tradeMap.put("quantity", trade.getQuantity());
            tradeMap.put("price", trade.getPrice());
            tradeMap.put("buyerId", trade.getBuyerId());
            tradeMap.put("sellerId", trade.getSellerId());
            tradeMap.put("tradeTime", trade.getTradeTime());
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", tradeMap);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
}
