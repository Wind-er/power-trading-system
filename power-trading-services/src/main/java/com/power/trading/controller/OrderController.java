package com.power.trading.controller;

import com.power.trading.dto.ApiResponse;
import com.power.trading.entity.Order;
import com.power.trading.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ApiResponse<?> createOrder(@RequestBody Order order) {
        try {
            return ApiResponse.success(orderService.createOrder(order));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<?> getAllOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long productId) {
        List<Order> orders;
        if (userId != null) {
            orders = orderService.getOrdersByUserId(userId);
        } else if (productId != null) {
            orders = orderService.getOrdersByProductId(productId);
        } else {
            orders = orderService.getAllOrders();
        }
        return ApiResponse.success(orders);
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(order -> ApiResponse.success(order))
                .orElse(ApiResponse.error("订单不存在"));
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        try {
            return ApiResponse.success(orderService.updateOrder(id, orderDetails));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ApiResponse.success("订单已撤销");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/match")
    public ApiResponse<?> matchOrder(@PathVariable Long id, @RequestBody Order newOrder) {
        try {
            return ApiResponse.success(orderService.matchWithOrder(id, newOrder));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public ApiResponse<?> getOrderStatistics(@RequestParam Long userId) {
        try {
            return ApiResponse.success(orderService.getOrderStatistics(userId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/available-quantity")
    public ApiResponse<?> getAvailableQuantityForSale(@RequestParam Long userId) {
        try {
            return ApiResponse.success(orderService.getAvailableQuantityForSale(userId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

