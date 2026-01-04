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
import java.util.Optional;

@Service
public class TradeService {
    @Autowired
    private TradeRepository tradeRepository;

    public List<Trade> getAllTrades() {
        return tradeRepository.findAll();
    }

    public List<Trade> getTradesByProductId(Long productId) {
        return tradeRepository.findByProductId(productId);
    }

    public List<Trade> getTradesByUserId(Long userId) {
        return tradeRepository.findByBuyerIdOrSellerId(userId, userId);
    }

    public Optional<Trade> getTradeById(Long id) {
        return tradeRepository.findById(id);
    }

    public Map<String, Object> getTradeStatistics(Long productId) {
        List<Trade> trades = productId != null 
                ? tradeRepository.findByProductId(productId)
                : tradeRepository.findAll();

        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal maxPrice = BigDecimal.ZERO;
        BigDecimal minPrice = trades.isEmpty() ? BigDecimal.ZERO : trades.get(0).getPrice();

        for (Trade trade : trades) {
            totalQuantity = totalQuantity.add(trade.getQuantity());
            BigDecimal amount = trade.getQuantity().multiply(trade.getPrice());
            totalAmount = totalAmount.add(amount);

            if (trade.getPrice().compareTo(maxPrice) > 0) {
                maxPrice = trade.getPrice();
            }
            if (trade.getPrice().compareTo(minPrice) < 0) {
                minPrice = trade.getPrice();
            }
        }

        BigDecimal avgPrice = totalQuantity.compareTo(BigDecimal.ZERO) > 0
                ? totalAmount.divide(totalQuantity, 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalQuantity", totalQuantity);
        stats.put("totalAmount", totalAmount);
        stats.put("maxPrice", maxPrice);
        stats.put("minPrice", minPrice);
        stats.put("avgPrice", avgPrice);
        stats.put("tradeCount", trades.size());
        return stats;
    }
}

