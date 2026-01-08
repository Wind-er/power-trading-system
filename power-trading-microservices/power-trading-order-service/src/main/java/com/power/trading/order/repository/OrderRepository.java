package com.power.trading.order.repository;

import com.power.trading.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreateTimeDesc(Long userId);
    List<Order> findByProductIdOrderByCreateTimeDesc(Long productId);
    List<Order> findByStatusOrderByCreateTimeDesc(Order.OrderStatus status);
    List<Order> findAllByOrderByCreateTimeDesc();
    
    @Query("SELECT o FROM Order o WHERE o.productId = :productId AND o.orderType = :orderType " +
           "AND o.status IN ('PENDING', 'PARTIAL') ORDER BY " +
           "CASE WHEN o.orderType = 'BUY' THEN o.price END DESC, " +
           "CASE WHEN o.orderType = 'SELL' THEN o.price END ASC, " +
           "o.createTime ASC")
    List<Order> findMatchingOrders(@Param("productId") Long productId, 
                                   @Param("orderType") Order.OrderType orderType);
}
