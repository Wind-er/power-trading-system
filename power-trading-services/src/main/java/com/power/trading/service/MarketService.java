package com.power.trading.service;

import com.power.trading.entity.Trade;
import com.power.trading.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarketService {
    @Autowired
    private TradeRepository tradeRepository;

    public Map<String, Object> getQuote(Long productId) {
        List<Trade> recentTrades = tradeRepository.findByProductIdOrderByTradeTimeDesc(productId);
        
        Map<String, Object> quote = new HashMap<>();
        
        if (recentTrades.isEmpty()) {
            quote.put("latestPrice", BigDecimal.ZERO);
            quote.put("maxPrice", BigDecimal.ZERO);
            quote.put("minPrice", BigDecimal.ZERO);
            quote.put("volume", BigDecimal.ZERO);
            quote.put("amount", BigDecimal.ZERO);
            quote.put("changePercent", BigDecimal.ZERO);
        } else {
            Trade latestTrade = recentTrades.get(0);
            quote.put("latestPrice", latestTrade.getPrice());
            
            BigDecimal maxPrice = recentTrades.stream()
                    .map(Trade::getPrice)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            BigDecimal minPrice = recentTrades.stream()
                    .map(Trade::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            
            BigDecimal volume = recentTrades.stream()
                    .map(Trade::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal amount = recentTrades.stream()
                    .map(t -> t.getQuantity().multiply(t.getPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            quote.put("maxPrice", maxPrice);
            quote.put("minPrice", minPrice);
            quote.put("volume", volume);
            quote.put("amount", amount);
            quote.put("changePercent", BigDecimal.ZERO); // 简化处理
        }
        
        return quote;
    }

    public List<Trade> getHistory(Long productId) {
        return tradeRepository.findByProductIdOrderByTradeTimeDesc(productId);
    }
}

