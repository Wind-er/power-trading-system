package com.power.trading.order.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "order_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "remaining_quantity", nullable = false, precision = 18, scale = 2)
    private BigDecimal remainingQuantity;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (remainingQuantity == null) {
            remainingQuantity = quantity;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    public enum OrderType {
        BUY,         // 买入
        SELL         // 卖出
    }

    public enum OrderStatus {
        PENDING,     // 待撮合
        PARTIAL,     // 部分成交
        FILLED,      // 已成交
        CANCELLED    // 已取消
    }
}
