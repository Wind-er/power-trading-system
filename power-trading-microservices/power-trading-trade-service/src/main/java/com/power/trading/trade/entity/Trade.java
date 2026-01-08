package com.power.trading.trade.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades")
@Data
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buy_order_id", nullable = false)
    private Long buyOrderId;

    @Column(name = "sell_order_id", nullable = false)
    private Long sellOrderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "trade_time", nullable = false, updatable = false)
    private LocalDateTime tradeTime;

    @PrePersist
    protected void onCreate() {
        tradeTime = LocalDateTime.now();
    }
}
