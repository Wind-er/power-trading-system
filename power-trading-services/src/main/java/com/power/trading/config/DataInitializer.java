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
        // 等待用户和商品数据初始化完成
        List<User> users = userRepository.findAll();
        List<Product> products = productRepository.findAll();

        if (users.size() < 4 || products.isEmpty()) {
            System.out.println("用户或商品数据不足，跳过历史数据初始化");
            return;
        }

        // 获取用户和商品
        User buyer1 = users.stream().filter(u -> "buyer1".equals(u.getUsername())).findFirst().orElse(null);
        User buyer2 = users.stream().filter(u -> "buyer2".equals(u.getUsername())).findFirst().orElse(null);
        User generator1 = users.stream().filter(u -> "generator1".equals(u.getUsername())).findFirst().orElse(null);
        User generator2 = users.stream().filter(u -> "generator2".equals(u.getUsername())).findFirst().orElse(null);

        if (buyer1 == null || buyer2 == null || generator1 == null || generator2 == null) {
            return;
        }

        Product peakProduct = products.stream().filter(p -> "PEAK".equals(p.getType())).findFirst().orElse(products.get(0));
        Product valleyProduct = products.stream().filter(p -> "VALLEY".equals(p.getType())).findFirst().orElse(products.size() > 1 ? products.get(1) : products.get(0));
        Product flatProduct = products.stream().filter(p -> "FLAT".equals(p.getType())).findFirst().orElse(products.size() > 2 ? products.get(2) : products.get(0));

        // 只初始化一次
        if (orderRepository.count() > 0 || tradeRepository.count() > 0) {
            System.out.println("已有订单或交易数据，跳过历史数据初始化");
            return;
        }

        LocalDateTime baseTime = LocalDateTime.now().minusDays(7);

        // ========== 为每个用户创建订单和交易数据 ==========
        
        // buyer1 的数据：买入订单和交易记录
        createUserOrdersAndTrades(peakProduct, buyer1, generator1, baseTime, 
            new BigDecimal("500"), new BigDecimal("520"), new BigDecimal("100"), "buyer1");
        createUserOrdersAndTrades(flatProduct, buyer1, generator2, baseTime.plusDays(1),
            new BigDecimal("400"), new BigDecimal("420"), new BigDecimal("150"), "buyer1");
        createUserOrdersAndTrades(valleyProduct, buyer1, generator1, baseTime.plusDays(2),
            new BigDecimal("300"), new BigDecimal("320"), new BigDecimal("80"), "buyer1");

        // buyer2 的数据：买入订单和交易记录
        createUserOrdersAndTrades(valleyProduct, buyer2, generator2, baseTime.plusDays(1),
            new BigDecimal("300"), new BigDecimal("320"), new BigDecimal("150"), "buyer2");
        createUserOrdersAndTrades(peakProduct, buyer2, generator1, baseTime.plusDays(3),
            new BigDecimal("510"), new BigDecimal("530"), new BigDecimal("120"), "buyer2");
        createUserOrdersAndTrades(flatProduct, buyer2, generator2, baseTime.plusDays(4),
            new BigDecimal("410"), new BigDecimal("430"), new BigDecimal("90"), "buyer2");

        // generator1 的数据：卖出订单和交易记录
        createUserOrdersAndTrades(peakProduct, buyer1, generator1, baseTime.plusDays(0),
            new BigDecimal("500"), new BigDecimal("520"), new BigDecimal("100"), "generator1");
        createUserOrdersAndTrades(peakProduct, buyer2, generator1, baseTime.plusDays(3),
            new BigDecimal("510"), new BigDecimal("530"), new BigDecimal("120"), "generator1");
        createUserOrdersAndTrades(valleyProduct, buyer1, generator1, baseTime.plusDays(2),
            new BigDecimal("300"), new BigDecimal("320"), new BigDecimal("80"), "generator1");

        // generator2 的数据：卖出订单和交易记录
        createUserOrdersAndTrades(valleyProduct, buyer2, generator2, baseTime.plusDays(1),
            new BigDecimal("300"), new BigDecimal("320"), new BigDecimal("150"), "generator2");
        createUserOrdersAndTrades(flatProduct, buyer1, generator2, baseTime.plusDays(1),
            new BigDecimal("400"), new BigDecimal("420"), new BigDecimal("150"), "generator2");
        createUserOrdersAndTrades(flatProduct, buyer2, generator2, baseTime.plusDays(4),
            new BigDecimal("410"), new BigDecimal("430"), new BigDecimal("90"), "generator2");

        // ========== 创建更多历史交易数据（用于价格走势图）==========
        // 为每个商品创建更多交易记录，确保价格走势图有足够数据
        createMoreTradesForChart(peakProduct, buyer1, buyer2, generator1, generator2, baseTime);
        createMoreTradesForChart(valleyProduct, buyer1, buyer2, generator1, generator2, baseTime.plusDays(1));
        createMoreTradesForChart(flatProduct, buyer1, buyer2, generator1, generator2, baseTime.plusDays(2));

        // ========== 创建待撮合的订单（每个用户都有）==========
        createPendingOrders(peakProduct, buyer1, generator1, baseTime.plusDays(5));
        createPendingOrders(valleyProduct, buyer2, generator2, baseTime.plusDays(6));
        createPendingOrders(flatProduct, buyer1, generator2, baseTime.plusDays(6).plusHours(2));

        // ========== 创建部分成交的订单 ==========
        createPartialFilledOrders(flatProduct, buyer1, generator1, baseTime.plusDays(3));
        createPartialFilledOrders(peakProduct, buyer2, generator2, baseTime.plusDays(4).plusHours(3));

        // ========== 为每个用户创建一些已取消的订单 ==========
        createCancelledOrders(peakProduct, buyer1, baseTime.plusDays(4));
        createCancelledOrders(valleyProduct, buyer2, baseTime.plusDays(5));
        createCancelledOrders(flatProduct, generator1, baseTime.plusDays(5).plusHours(2));

        System.out.println("已初始化历史订单和交易数据");
        System.out.println("  - 每个用户都有订单数据");
        System.out.println("  - 每个用户都有交易记录");
        System.out.println("  - 每个商品都有价格走势数据");
    }

    private void createUserOrdersAndTrades(Product product, User buyer, User seller, 
                                          LocalDateTime baseTime, BigDecimal sellPrice, 
                                          BigDecimal buyPrice, BigDecimal quantity, String forUser) {
        // 创建已成交的卖出订单
        Order sellOrder = new Order();
        sellOrder.setUserId(seller.getId());
        sellOrder.setProductId(product.getId());
        sellOrder.setOrderType(Order.OrderType.SELL);
        sellOrder.setQuantity(quantity);
        sellOrder.setPrice(sellPrice);
        sellOrder.setStatus(Order.OrderStatus.FILLED);
        sellOrder.setRemainingQuantity(BigDecimal.ZERO);
        sellOrder.setCreateTime(baseTime);
        sellOrder.setUpdateTime(baseTime.plusMinutes(5));
        sellOrder = orderRepository.save(sellOrder);

        // 创建已成交的买入订单
        Order buyOrder = new Order();
        buyOrder.setUserId(buyer.getId());
        buyOrder.setProductId(product.getId());
        buyOrder.setOrderType(Order.OrderType.BUY);
        buyOrder.setQuantity(quantity);
        buyOrder.setPrice(buyPrice);
        buyOrder.setStatus(Order.OrderStatus.FILLED);
        buyOrder.setRemainingQuantity(BigDecimal.ZERO);
        buyOrder.setCreateTime(baseTime.plusMinutes(2));
        buyOrder.setUpdateTime(baseTime.plusMinutes(5));
        buyOrder = orderRepository.save(buyOrder);

        // 创建交易记录
        Trade trade = new Trade();
        trade.setBuyOrderId(buyOrder.getId());
        trade.setSellOrderId(sellOrder.getId());
        trade.setProductId(product.getId());
        trade.setQuantity(quantity);
        trade.setPrice(sellPrice); // 使用卖出价作为成交价
        trade.setBuyerId(buyer.getId());
        trade.setSellerId(seller.getId());
        trade.setTradeTime(baseTime.plusMinutes(5));
        tradeRepository.save(trade);
    }

    private void createMoreTradesForChart(Product product, User buyer1, User buyer2, 
                                         User generator1, User generator2, LocalDateTime baseTime) {
        // 为价格走势图创建更多交易数据（每个商品至少20笔交易）
        for (int i = 0; i < 20; i++) {
            BigDecimal basePrice = product.getType().equals("PEAK") ? new BigDecimal("500") :
                                  product.getType().equals("VALLEY") ? new BigDecimal("300") :
                                  new BigDecimal("400");
            
            BigDecimal priceVariation = new BigDecimal((Math.random() * 100 - 50)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal tradePrice = basePrice.add(priceVariation);
            if (tradePrice.compareTo(BigDecimal.ZERO) < 0) {
                tradePrice = basePrice;
            }
            
            BigDecimal qty = new BigDecimal(30 + (int)(Math.random() * 100)).setScale(2, RoundingMode.HALF_UP);
            
            // 随机选择买卖双方
            User buyer = (i % 2 == 0) ? buyer1 : buyer2;
            User seller = (i % 3 == 0) ? generator1 : generator2;
            
            Order sellOrder = new Order();
            sellOrder.setUserId(seller.getId());
            sellOrder.setProductId(product.getId());
            sellOrder.setOrderType(Order.OrderType.SELL);
            sellOrder.setQuantity(qty);
            sellOrder.setPrice(tradePrice);
            sellOrder.setStatus(Order.OrderStatus.FILLED);
            sellOrder.setRemainingQuantity(BigDecimal.ZERO);
            LocalDateTime orderTime = baseTime.plusDays(i / 3).plusHours(i % 24).plusMinutes(i * 5);
            sellOrder.setCreateTime(orderTime);
            sellOrder.setUpdateTime(orderTime.plusMinutes(5));
            sellOrder = orderRepository.save(sellOrder);

            Order buyOrder = new Order();
            buyOrder.setUserId(buyer.getId());
            buyOrder.setProductId(product.getId());
            buyOrder.setOrderType(Order.OrderType.BUY);
            buyOrder.setQuantity(qty);
            buyOrder.setPrice(tradePrice.add(new BigDecimal("10")));
            buyOrder.setStatus(Order.OrderStatus.FILLED);
            buyOrder.setRemainingQuantity(BigDecimal.ZERO);
            buyOrder.setCreateTime(orderTime.plusMinutes(2));
            buyOrder.setUpdateTime(orderTime.plusMinutes(5));
            buyOrder = orderRepository.save(buyOrder);

            Trade trade = new Trade();
            trade.setBuyOrderId(buyOrder.getId());
            trade.setSellOrderId(sellOrder.getId());
            trade.setProductId(product.getId());
            trade.setQuantity(qty);
            trade.setPrice(tradePrice);
            trade.setBuyerId(buyer.getId());
            trade.setSellerId(seller.getId());
            trade.setTradeTime(orderTime.plusMinutes(5));
            tradeRepository.save(trade);
        }
    }

    private void createPendingOrders(Product product, User buyer, User seller, LocalDateTime baseTime) {
        // 创建待撮合的卖出订单
        Order sellOrder = new Order();
        sellOrder.setUserId(seller.getId());
        sellOrder.setProductId(product.getId());
        sellOrder.setOrderType(Order.OrderType.SELL);
        sellOrder.setQuantity(new BigDecimal("200"));
        sellOrder.setPrice(new BigDecimal("480"));
        sellOrder.setStatus(Order.OrderStatus.PENDING);
        sellOrder.setRemainingQuantity(new BigDecimal("200"));
        sellOrder.setCreateTime(baseTime);
        sellOrder.setUpdateTime(baseTime);
        orderRepository.save(sellOrder);

        // 创建待撮合的买入订单（价格不匹配，不会自动撮合）
        Order buyOrder = new Order();
        buyOrder.setUserId(buyer.getId());
        buyOrder.setProductId(product.getId());
        buyOrder.setOrderType(Order.OrderType.BUY);
        buyOrder.setQuantity(new BigDecimal("150"));
        buyOrder.setPrice(new BigDecimal("450")); // 买入价低于卖出价，不会撮合
        buyOrder.setStatus(Order.OrderStatus.PENDING);
        buyOrder.setRemainingQuantity(new BigDecimal("150"));
        buyOrder.setCreateTime(baseTime.plusMinutes(30));
        buyOrder.setUpdateTime(baseTime.plusMinutes(30));
        orderRepository.save(buyOrder);
    }

    private void createPartialFilledOrders(Product product, User buyer, User seller, LocalDateTime baseTime) {
        // 创建部分成交的卖出订单
        Order sellOrder = new Order();
        sellOrder.setUserId(seller.getId());
        sellOrder.setProductId(product.getId());
        sellOrder.setOrderType(Order.OrderType.SELL);
        sellOrder.setQuantity(new BigDecimal("300"));
        sellOrder.setPrice(new BigDecimal("410"));
        sellOrder.setStatus(Order.OrderStatus.PARTIAL);
        sellOrder.setRemainingQuantity(new BigDecimal("100")); // 剩余100
        sellOrder.setCreateTime(baseTime);
        sellOrder.setUpdateTime(baseTime.plusMinutes(15));
        sellOrder = orderRepository.save(sellOrder);

        // 创建已成交的买入订单（部分匹配）
        Order buyOrder = new Order();
        buyOrder.setUserId(buyer.getId());
        buyOrder.setProductId(product.getId());
        buyOrder.setOrderType(Order.OrderType.BUY);
        buyOrder.setQuantity(new BigDecimal("200"));
        buyOrder.setPrice(new BigDecimal("420"));
        buyOrder.setStatus(Order.OrderStatus.FILLED);
        buyOrder.setRemainingQuantity(BigDecimal.ZERO);
        buyOrder.setCreateTime(baseTime.plusMinutes(10));
        buyOrder.setUpdateTime(baseTime.plusMinutes(15));
        buyOrder = orderRepository.save(buyOrder);

        // 创建部分成交的交易记录
        Trade trade = new Trade();
        trade.setBuyOrderId(buyOrder.getId());
        trade.setSellOrderId(sellOrder.getId());
        trade.setProductId(product.getId());
        trade.setQuantity(new BigDecimal("200"));
        trade.setPrice(new BigDecimal("410"));
        trade.setBuyerId(buyer.getId());
        trade.setSellerId(seller.getId());
        trade.setTradeTime(baseTime.plusMinutes(15));
        tradeRepository.save(trade);
    }

    private void createCancelledOrders(Product product, User user, LocalDateTime baseTime) {
        // 创建已取消的订单
        Order cancelledOrder = new Order();
        cancelledOrder.setUserId(user.getId());
        cancelledOrder.setProductId(product.getId());
        cancelledOrder.setOrderType(user.getUserType() == User.UserType.BUYER ? 
                                    Order.OrderType.BUY : Order.OrderType.SELL);
        cancelledOrder.setQuantity(new BigDecimal("100"));
        cancelledOrder.setPrice(new BigDecimal("450"));
        cancelledOrder.setStatus(Order.OrderStatus.CANCELLED);
        cancelledOrder.setRemainingQuantity(new BigDecimal("100"));
        cancelledOrder.setCreateTime(baseTime);
        cancelledOrder.setUpdateTime(baseTime.plusHours(1));
        orderRepository.save(cancelledOrder);
    }
}

