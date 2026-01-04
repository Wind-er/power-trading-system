package com.power.trading.controller;

import com.power.trading.dto.ApiResponse;
import com.power.trading.entity.Product;
import com.power.trading.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ApiResponse<?> getAllProducts() {
        return ApiResponse.success(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ApiResponse.success(product))
                .orElse(ApiResponse.error("商品不存在"));
    }

    @PostMapping
    public ApiResponse<?> createProduct(@RequestBody Product product) {
        try {
            return ApiResponse.success(productService.createProduct(product));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        try {
            return ApiResponse.success(productService.updateProduct(id, productDetails));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ApiResponse.success("商品已删除");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

