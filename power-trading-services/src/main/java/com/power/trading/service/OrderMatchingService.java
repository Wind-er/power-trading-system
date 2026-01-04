package com.power.trading.service;

import com.power.trading.entity.Order;
import com.power.trading.entity.Trade;
import com.power.trading.repository.OrderRepository;
import com.power.trading.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderMatchingService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Transactional
    public void matchOrder(Order newOrder) {
        Order.OrderType oppositeType = newOrder.getOrderType() == Order.OrderType.BUY 
                ? Order.OrderType.SELL 
                : Order.OrderType.BUY;

        List<Order> matchingOrders = orderRepository.findMatchingOrders(
                newOrder.getProductId(), oppositeType);

        for (Order matchingOrder : matchingOrders) {
            if (newOrder.getRemainingQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            if (canMatch(newOrder, matchingOrder)) {
                executeTrade(newOrder, matchingOrder);
            }
        }
    }

    private boolean canMatch(Order order1, Order order2) {
        if (order1.getOrderType() == Order.OrderType.BUY) {
            // 买入价 >= 卖出价
            return order1.getPrice().compareTo(order2.getPrice()) >= 0;
        } else {
            // 卖出价 <= 买入价
            return order1.getPrice().compareTo(order2.getPrice()) <= 0;
        }
    }

    private void executeTrade(Order buyOrder, Order sellOrder) {
        // 确定成交价格（使用先提交订单的价格）
        BigDecimal tradePrice = buyOrder.getCreateTime().isBefore(sellOrder.getCreateTime()) 
                ? buyOrder.getPrice() 
                : sellOrder.getPrice();

        // 确定成交数量
        BigDecimal tradeQuantity = buyOrder.getRemainingQuantity()
                .min(sellOrder.getRemainingQuantity());

        // 创建交易记录
        Trade trade = new Trade();
        trade.setBuyOrderId(buyOrder.getId());
        trade.setSellOrderId(sellOrder.getId());
        trade.setProductId(buyOrder.getProductId());
        trade.setQuantity(tradeQuantity);
        trade.setPrice(tradePrice);
        trade.setBuyerId(buyOrder.getUserId());
        trade.setSellerId(sellOrder.getUserId());
        tradeRepository.save(trade);

        // 更新订单
        updateOrderStatus(buyOrder, tradeQuantity);
        updateOrderStatus(sellOrder, tradeQuantity);
    }

    private void updateOrderStatus(Order order, BigDecimal tradeQuantity) {
        BigDecimal newRemaining = order.getRemainingQuantity().subtract(tradeQuantity);
        order.setRemainingQuantity(newRemaining);

        if (newRemaining.compareTo(BigDecimal.ZERO) == 0) {
            order.setStatus(Order.OrderStatus.FILLED);
        } else {
            order.setStatus(Order.OrderStatus.PARTIAL);
        }

        orderRepository.save(order);
    }
}

