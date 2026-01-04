package com.power.trading.service;

import com.power.trading.entity.Product;
import com.power.trading.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByStatus(Product.ProductStatus.ACTIVE);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product createProduct(Product product) {
        product.setStatus(Product.ProductStatus.ACTIVE);
        product.setCreateTime(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (productDetails.getName() != null) {
            product.setName(productDetails.getName());
        }
        if (productDetails.getType() != null) {
            product.setType(productDetails.getType());
        }
        if (productDetails.getDescription() != null) {
            product.setDescription(productDetails.getDescription());
        }
        if (productDetails.getStatus() != null) {
            product.setStatus(productDetails.getStatus());
        }

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 软删除：将状态设置为禁用
        product.setStatus(Product.ProductStatus.INACTIVE);
        productRepository.save(product);
    }
}

