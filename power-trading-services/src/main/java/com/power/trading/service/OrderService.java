package com.power.trading.service;

import com.power.trading.entity.Order;
import com.power.trading.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMatchingService matchingService;

    @Transactional
    public Order createOrder(Order order) {
        order.setStatus(Order.OrderStatus.PENDING);
        order.setRemainingQuantity(order.getQuantity());
        Order savedOrder = orderRepository.save(order);
        
        // 触发撮合
        matchingService.matchOrder(savedOrder);
        
        return orderRepository.findById(savedOrder.getId()).orElse(savedOrder);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreateTimeDesc();
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public List<Order> getOrdersByProductId(Long productId) {
        return orderRepository.findByProductIdOrderByCreateTimeDesc(productId);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order updateOrder(Long id, Order orderDetails) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("只能修改待撮合状态的订单");
        }

        order.setQuantity(orderDetails.getQuantity());
        order.setPrice(orderDetails.getPrice());
        order.setRemainingQuantity(orderDetails.getQuantity());

        Order updatedOrder = orderRepository.save(order);
        matchingService.matchOrder(updatedOrder);
        
        return orderRepository.findById(updatedOrder.getId()).orElse(updatedOrder);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() == Order.OrderStatus.FILLED) {
            throw new RuntimeException("已成交的订单不能撤销");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional
    public Order matchWithOrder(Long targetOrderId, Order newOrder) {
        // 获取目标订单
        Order targetOrder = orderRepository.findById(targetOrderId)
                .orElseThrow(() -> new RuntimeException("目标订单不存在"));

        // 检查订单状态
        if (targetOrder.getStatus() != Order.OrderStatus.PENDING && 
            targetOrder.getStatus() != Order.OrderStatus.PARTIAL) {
            throw new RuntimeException("只能撮合待撮合或部分成交的订单");
        }

        // 检查订单类型是否匹配
        if (targetOrder.getOrderType() == newOrder.getOrderType()) {
            throw new RuntimeException("不能撮合相同类型的订单");
        }

        // 检查商品是否匹配
        if (!targetOrder.getProductId().equals(newOrder.getProductId())) {
            throw new RuntimeException("商品不匹配");
        }

        // 检查价格是否匹配
        boolean canMatch = false;
        if (targetOrder.getOrderType() == Order.OrderType.SELL && newOrder.getOrderType() == Order.OrderType.BUY) {
            // 买入价 >= 卖出价
            canMatch = newOrder.getPrice().compareTo(targetOrder.getPrice()) >= 0;
        } else if (targetOrder.getOrderType() == Order.OrderType.BUY && newOrder.getOrderType() == Order.OrderType.SELL) {
            // 卖出价 <= 买入价
            canMatch = newOrder.getPrice().compareTo(targetOrder.getPrice()) <= 0;
        }

        if (!canMatch) {
            throw new RuntimeException("价格不匹配，无法撮合");
        }

        // 设置新订单的状态和剩余数量
        newOrder.setStatus(Order.OrderStatus.PENDING);
        newOrder.setRemainingQuantity(newOrder.getQuantity());
        
        // 保存新订单
        Order savedOrder = orderRepository.save(newOrder);
        
        // 触发撮合（会自动匹配目标订单）
        matchingService.matchOrder(savedOrder);
        
        // 返回更新后的订单
        return orderRepository.findById(savedOrder.getId()).orElse(savedOrder);
    }

    /**
     * 获取用户的可售电量统计（仅对发电商有效）
     * @param userId 用户ID
     * @return 可售电量统计信息
     */
    public BigDecimal getAvailableQuantityForSale(Long userId) {
        // 获取用户所有待撮合和部分成交的卖出订单
        List<Order> sellOrders = orderRepository.findByUserIdOrderByCreateTimeDesc(userId)
                .stream()
                .filter(order -> order.getOrderType() == Order.OrderType.SELL)
                .filter(order -> order.getStatus() == Order.OrderStatus.PENDING || 
                                order.getStatus() == Order.OrderStatus.PARTIAL)
                .toList();
        
        // 计算总剩余电量
        BigDecimal totalAvailable = sellOrders.stream()
                .map(Order::getRemainingQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalAvailable;
    }

    /**
     * 获取用户的订单统计信息
     * @param userId 用户ID
     * @return 订单统计信息
     */
    public java.util.Map<String, Object> getOrderStatistics(Long userId) {
        List<Order> allOrders = orderRepository.findByUserIdOrderByCreateTimeDesc(userId);
        
        // 卖出订单统计
        List<Order> sellOrders = allOrders.stream()
                .filter(order -> order.getOrderType() == Order.OrderType.SELL)
                .toList();
        
        BigDecimal totalSellQuantity = sellOrders.stream()
                .map(Order::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal availableSellQuantity = sellOrders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.PENDING || 
                                order.getStatus() == Order.OrderStatus.PARTIAL)
                .map(Order::getRemainingQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal soldQuantity = sellOrders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.FILLED || 
                                order.getStatus() == Order.OrderStatus.PARTIAL)
                .map(order -> order.getQuantity().subtract(order.getRemainingQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 买入订单统计
        List<Order> buyOrders = allOrders.stream()
                .filter(order -> order.getOrderType() == Order.OrderType.BUY)
                .toList();
        
        BigDecimal totalBuyQuantity = buyOrders.stream()
                .map(Order::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal availableBuyQuantity = buyOrders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.PENDING || 
                                order.getStatus() == Order.OrderStatus.PARTIAL)
                .map(Order::getRemainingQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal boughtQuantity = buyOrders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.FILLED || 
                                order.getStatus() == Order.OrderStatus.PARTIAL)
                .map(order -> order.getQuantity().subtract(order.getRemainingQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalSellQuantity", totalSellQuantity);
        stats.put("availableSellQuantity", availableSellQuantity);
        stats.put("soldQuantity", soldQuantity);
        stats.put("totalBuyQuantity", totalBuyQuantity);
        stats.put("availableBuyQuantity", availableBuyQuantity);
        stats.put("boughtQuantity", boughtQuantity);
        stats.put("totalOrders", allOrders.size());
        stats.put("sellOrdersCount", sellOrders.size());
        stats.put("buyOrdersCount", buyOrders.size());
        
        return stats;
    }
}

