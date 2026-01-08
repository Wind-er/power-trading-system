package com.power.trading.product.config;

import com.power.trading.product.entity.Product;
import com.power.trading.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 商品数据初始化器
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        initProducts();
    }

    private void initProducts() {
        if (productRepository.count() == 0) {
            // 创建峰时电力
            Product peak = new Product();
            peak.setName("峰时电力");
            peak.setType("PEAK");
            peak.setUnit("MWh");
            peak.setDescription("用电高峰时段的电力（通常为白天工作时间）");
            peak.setStatus(Product.ProductStatus.ACTIVE);
            peak.setCreateTime(LocalDateTime.now());
            productRepository.save(peak);
            System.out.println("已创建商品: 峰时电力 (PEAK)");

            // 创建谷时电力
            Product valley = new Product();
            valley.setName("谷时电力");
            valley.setType("VALLEY");
            valley.setUnit("MWh");
            valley.setDescription("用电低谷时段的电力（通常为夜间）");
            valley.setStatus(Product.ProductStatus.ACTIVE);
            valley.setCreateTime(LocalDateTime.now());
            productRepository.save(valley);
            System.out.println("已创建商品: 谷时电力 (VALLEY)");

            // 创建平时电力
            Product flat = new Product();
            flat.setName("平时电力");
            flat.setType("FLAT");
            flat.setUnit("MWh");
            flat.setDescription("正常时段的电力（除峰时和谷时外的时段）");
            flat.setStatus(Product.ProductStatus.ACTIVE);
            flat.setCreateTime(LocalDateTime.now());
            productRepository.save(flat);
            System.out.println("已创建商品: 平时电力 (FLAT)");

            System.out.println("商品数据初始化完成");
        } else {
            System.out.println("商品数据已存在，跳过初始化");
        }
    }
}
