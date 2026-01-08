package com.power.trading.trade.repository;

import com.power.trading.trade.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByProductId(Long productId);
    List<Trade> findByBuyerIdOrSellerId(Long buyerId, Long sellerId);
    
    @Query("SELECT t FROM Trade t WHERE t.productId = :productId ORDER BY t.tradeTime DESC")
    List<Trade> findByProductIdOrderByTradeTimeDesc(@Param("productId") Long productId);
    
    @Query("SELECT t FROM Trade t WHERE t.tradeTime >= :startTime ORDER BY t.tradeTime DESC")
    List<Trade> findRecentTrades(@Param("startTime") java.time.LocalDateTime startTime);
}
