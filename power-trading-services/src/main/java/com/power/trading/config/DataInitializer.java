package com.power.trading.config;

import com.power.trading.entity.Order;
import com.power.trading.entity.Product;
import com.power.trading.entity.Trade;
import com.power.trading.entity.User;
import com.power.trading.repository.OrderRepository;
import com.power.trading.repository.ProductRepository;
import com.power.trading.repository.TradeRepository;
import com.power.trading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 初始化测试用户
        initTestUsers();
        
        // 初始化商品数据
        initProducts();
        
        // 初始化历史订单和交易数据
        initHistoricalData();
    }

    private void initTestUsers() {
        // 创建购电商测试账号
        if (!userRepository.existsByUsername("buyer1")) {
            User buyer1 = new User();
            buyer1.setUsername("buyer1");
            buyer1.setPassword(passwordEncoder.encode("123456"));
            buyer1.setUserType(User.UserType.BUYER);
            buyer1.setCompanyName("测试购电公司A");
            buyer1.setContactInfo("13800138000");
            buyer1.setStatus(User.UserStatus.ACTIVE);
            buyer1.setCreateTime(LocalDateTime.now());
            buyer1.setUpdateTime(LocalDateTime.now());
            userRepository.save(buyer1);
            System.out.println("已创建测试账号: buyer1 / 123456 (购电商)");
        }

        // 创建购电商测试账号2
        if (!userRepository.existsByUsername("buyer2")) {
            User buyer2 = new User();
            buyer2.setUsername("buyer2");
            buyer2.setPassword(passwordEncoder.encode("123456"));
            buyer2.setUserType(User.UserType.BUYER);
            buyer2.setCompanyName("测试购电公司B");
            buyer2.setContactInfo("13900139000");
            buyer2.setStatus(User.UserStatus.ACTIVE);
            buyer2.setCreateTime(LocalDateTime.now());
            buyer2.setUpdateTime(LocalDateTime.now());
            userRepository.save(buyer2);
            System.out.println("已创建测试账号: buyer2 / 123456 (购电商)");
        }

        // 创建发电商测试账号
        if (!userRepository.existsByUsername("generator1")) {
            User generator1 = new User();
            generator1.setUsername("generator1");
            generator1.setPassword(passwordEncoder.encode("123456"));
            generator1.setUserType(User.UserType.GENERATOR);
            generator1.setCompanyName("测试发电公司A");
            generator1.setContactInfo("13700137000");
            generator1.setStatus(User.UserStatus.ACTIVE);
            generator1.setCreateTime(LocalDateTime.now());
            generator1.setUpdateTime(LocalDateTime.now());
            userRepository.save(generator1);
            System.out.println("已创建测试账号: generator1 / 123456 (发电商)");
        }

        // 创建发电商测试账号2
        if (!userRepository.existsByUsername("generator2")) {
            User generator2 = new User();
            generator2.setUsername("generator2");
            generator2.setPassword(passwordEncoder.encode("123456"));
            generator2.setUserType(User.UserType.GENERATOR);
            generator2.setCompanyName("测试发电公司B");
            generator2.setContactInfo("13600136000");
            generator2.setStatus(User.UserStatus.ACTIVE);
            generator2.setCreateTime(LocalDateTime.now());
            generator2.setUpdateTime(LocalDateTime.now());
            userRepository.save(generator2);
            System.out.println("已创建测试账号: generator2 / 123456 (发电商)");
        }

        // 创建管理员测试账号
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setUserType(User.UserType.ADMIN);
            admin.setCompanyName("系统管理员");
            admin.setContactInfo("admin@power-trading.com");
            admin.setStatus(User.UserStatus.ACTIVE);
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("已创建测试账号: admin / 123456 (管理员)");
        }
    }

    private void initProducts() {
        // 创建商品数据
        if (productRepository.count() == 0) {
            Product peak = new Product();
            peak.setName("峰时电力");
            peak.setType("PEAK");
            peak.setUnit("MWh");
            peak.setDescription("用电高峰时段的电力");
            peak.setStatus(Product.ProductStatus.ACTIVE);
            peak.setCreateTime(LocalDateTime.now());
            productRepository.save(peak);

            Product valley = new Product();
            valley.setName("谷时电力");
            valley.setType("VALLEY");
            valley.setUnit("MWh");
            valley.setDescription("用电低谷时段的电力");
            valley.setStatus(Product.ProductStatus.ACTIVE);
            valley.setCreateTime(LocalDateTime.now());
            productRepository.save(valley);

            Product flat = new Product();
            flat.setName("平时电力");
            flat.setType("FLAT");
            flat.setUnit("MWh");
            flat.setDescription("正常时段的电力");
            flat.setStatus(Product.ProductStatus.ACTIVE);
            flat.setCreateTime(LocalDateTime.now());
            productRepository.save(flat);

            System.out.println("已初始化商品数据");
        }
    }

    private void initHistoricalData() {
        // 清空所有订单和交易数据，只保留测试账号和商品
        System.out.println("清空所有订单和交易测试数据...");
        tradeRepository.deleteAll();
        orderRepository.deleteAll();
        System.out.println("已清空所有订单和交易数据");
    }

}

