package com.power.trading.market.service;

import com.power.trading.market.dto.MarketQuote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarketService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public MarketQuote getQuote(Long productId) {
        // 从 trade-service 获取该商品的交易记录
        String url = "http://trade-service/trades?productId=" + productId;
        
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && Boolean.TRUE.equals(responseBody.get("success"))) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> trades = (List<Map<String, Object>>) responseBody.get("data");
                
                if (trades == null || trades.isEmpty()) {
                    // 如果没有交易记录，返回默认值
                    return createDefaultQuote(productId);
                }
                
                // 计算行情数据
                BigDecimal latestPrice = BigDecimal.ZERO;
                BigDecimal maxPrice = BigDecimal.ZERO;
                BigDecimal minPrice = new BigDecimal("999999");
                BigDecimal totalVolume = BigDecimal.ZERO;
                BigDecimal totalAmount = BigDecimal.ZERO;
                
                for (Map<String, Object> trade : trades) {
                    BigDecimal price = new BigDecimal(trade.get("price").toString());
                    BigDecimal quantity = new BigDecimal(trade.get("quantity").toString());
                    
                    if (latestPrice.equals(BigDecimal.ZERO)) {
                        latestPrice = price;
                    }
                    
                    if (price.compareTo(maxPrice) > 0) {
                        maxPrice = price;
                    }
                    
                    if (price.compareTo(minPrice) < 0) {
                        minPrice = price;
                    }
                    
                    totalVolume = totalVolume.add(quantity);
                    totalAmount = totalAmount.add(price.multiply(quantity));
                }
                
                // 如果 minPrice 还是初始值，说明没有有效数据
                if (minPrice.compareTo(new BigDecimal("999999")) == 0) {
                    minPrice = BigDecimal.ZERO;
                }
                
                MarketQuote quote = new MarketQuote();
                quote.setProductId(productId);
                quote.setLatestPrice(latestPrice);
                quote.setMaxPrice(maxPrice);
                quote.setMinPrice(minPrice);
                quote.setVolume(totalVolume);
                quote.setAmount(totalAmount);
                
                return quote;
            }
        } catch (Exception e) {
            // 如果调用失败，返回默认值
            return createDefaultQuote(productId);
        }
        
        return createDefaultQuote(productId);
    }
    
    public List<Map<String, Object>> getHistory(Long productId) {
        // 从 trade-service 获取该商品的历史交易记录
        String url = "http://trade-service/trades?productId=" + productId;
        
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && Boolean.TRUE.equals(responseBody.get("success"))) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> trades = (List<Map<String, Object>>) responseBody.get("data");
                return trades != null ? trades : new ArrayList<>();
            }
        } catch (Exception e) {
            // 如果调用失败，返回空列表
        }
        
        return new ArrayList<>();
    }
    
    private MarketQuote createDefaultQuote(Long productId) {
        MarketQuote quote = new MarketQuote();
        quote.setProductId(productId);
        quote.setLatestPrice(BigDecimal.ZERO);
        quote.setMaxPrice(BigDecimal.ZERO);
        quote.setMinPrice(BigDecimal.ZERO);
        quote.setVolume(BigDecimal.ZERO);
        quote.setAmount(BigDecimal.ZERO);
        return quote;
    }
}
