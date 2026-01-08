package com.power.trading.product.controller;

import com.power.trading.product.entity.Product;
import com.power.trading.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping
    public Map<String, Object> getAll() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Product> products = productRepository.findAll();
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", products);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("商品不存在"));
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", product);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
}
