package com.power.trading.order.config;

import com.power.trading.order.entity.Order;
import com.power.trading.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * 订单数据初始化器
 * 注意：需要先启动 auth-service 创建用户，product-service 创建商品
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private OrderRepository orderRepository;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        // 延迟初始化，等待用户和商品数据创建完成
        Thread.sleep(5000);
        initOrders();
    }

    private void initOrders() {
        if (orderRepository.count() == 0) {
            System.out.println("开始初始化订单数据...");
            
            // 注意：用户ID和商品ID需要根据实际数据库中的数据调整
            // 默认假设：用户ID 1-5 (buyer1, buyer2, generator1, generator2, admin)
            // 商品ID 1-3 (峰时电力, 谷时电力, 平时电力)
            
            // 创建一些待撮合的买入订单（购电商）
            createOrder(1L, 1L, Order.OrderType.BUY, new BigDecimal("100"), new BigDecimal("550"), Order.OrderStatus.PENDING);
            createOrder(1L, 2L, Order.OrderType.BUY, new BigDecimal("80"), new BigDecimal("350"), Order.OrderStatus.PENDING);
            createOrder(2L, 1L, Order.OrderType.BUY, new BigDecimal("120"), new BigDecimal("540"), Order.OrderStatus.PENDING);
            createOrder(2L, 3L, Order.OrderType.BUY, new BigDecimal("90"), new BigDecimal("450"), Order.OrderStatus.PENDING);
            
            // 创建一些待撮合的卖出订单（发电商）
            createOrder(3L, 1L, Order.OrderType.SELL, new BigDecimal("100"), new BigDecimal("520"), Order.OrderStatus.PENDING);
            createOrder(3L, 2L, Order.OrderType.SELL, new BigDecimal("80"), new BigDecimal("340"), Order.OrderStatus.PENDING);
            createOrder(4L, 1L, Order.OrderType.SELL, new BigDecimal("120"), new BigDecimal("530"), Order.OrderStatus.PENDING);
            createOrder(4L, 3L, Order.OrderType.SELL, new BigDecimal("90"), new BigDecimal("440"), Order.OrderStatus.PENDING);
            
            // 创建一些已成交的订单（用于历史数据展示）
            createFilledOrder(1L, 1L, Order.OrderType.BUY, new BigDecimal("50"), new BigDecimal("500"));
            createFilledOrder(3L, 1L, Order.OrderType.SELL, new BigDecimal("50"), new BigDecimal("500"));
            createFilledOrder(2L, 2L, Order.OrderType.BUY, new BigDecimal("60"), new BigDecimal("320"));
            createFilledOrder(4L, 2L, Order.OrderType.SELL, new BigDecimal("60"), new BigDecimal("320"));
            
            // 创建部分成交的订单
            Order partialOrder = createOrder(1L, 3L, Order.OrderType.BUY, new BigDecimal("200"), new BigDecimal("480"), Order.OrderStatus.PARTIAL);
            partialOrder.setRemainingQuantity(new BigDecimal("100")); // 已成交100，剩余100
            orderRepository.save(partialOrder);
            
            System.out.println("订单数据初始化完成，共创建 " + orderRepository.count() + " 条订单");
        } else {
            System.out.println("订单数据已存在，跳过初始化");
        }
    }

    private Order createOrder(Long userId, Long productId, Order.OrderType orderType, 
                             BigDecimal quantity, BigDecimal price, Order.OrderStatus status) {
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setOrderType(orderType);
        order.setQuantity(quantity);
        order.setPrice(price);
        order.setStatus(status);
        order.setRemainingQuantity(quantity);
        order.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(7)));
        order.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(7)));
        return orderRepository.save(order);
    }

    private void createFilledOrder(Long userId, Long productId, Order.OrderType orderType, 
                                  BigDecimal quantity, BigDecimal price) {
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setOrderType(orderType);
        order.setQuantity(quantity);
        order.setPrice(price);
        order.setStatus(Order.OrderStatus.FILLED);
        order.setRemainingQuantity(BigDecimal.ZERO);
        order.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(7) + 1));
        order.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(7) + 1));
        orderRepository.save(order);
    }
}
