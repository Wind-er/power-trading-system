package com.power.trading.trade.config;

import com.power.trading.trade.entity.Trade;
import com.power.trading.trade.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * 交易数据初始化器
 * 注意：需要先启动 order-service 创建订单数据
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TradeRepository tradeRepository;

    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        // 延迟初始化，等待订单数据创建完成
        Thread.sleep(10000);
        initTrades();
    }

    private void initTrades() {
        if (tradeRepository.count() == 0) {
            System.out.println("开始初始化交易数据...");
            
            // 创建一些历史交易记录（对应已成交的订单）
            // 假设：buyer1(1) 和 generator1(3) 的交易
            createTrade(1L, 3L, 1L, new BigDecimal("50"), new BigDecimal("500"), 1L, 3L);
            
            // buyer2(2) 和 generator2(4) 的交易
            createTrade(2L, 4L, 2L, new BigDecimal("60"), new BigDecimal("320"), 2L, 4L);
            
            // 创建更多历史交易数据（用于价格走势图）
            createHistoricalTrades(1L, 1L, 3L); // 峰时电力
            createHistoricalTrades(2L, 1L, 3L); // 谷时电力
            createHistoricalTrades(3L, 1L, 3L); // 平时电力
            
            System.out.println("交易数据初始化完成，共创建 " + tradeRepository.count() + " 条交易记录");
        } else {
            System.out.println("交易数据已存在，跳过初始化");
        }
    }

    private void createTrade(Long buyOrderId, Long sellOrderId, Long productId, 
                            BigDecimal quantity, BigDecimal price, Long buyerId, Long sellerId) {
        Trade trade = new Trade();
        trade.setBuyOrderId(buyOrderId);
        trade.setSellOrderId(sellOrderId);
        trade.setProductId(productId);
        trade.setQuantity(quantity);
        trade.setPrice(price);
        trade.setBuyerId(buyerId);
        trade.setSellerId(sellerId);
        trade.setTradeTime(LocalDateTime.now().minusDays(random.nextInt(7)));
        tradeRepository.save(trade);
    }

    private void createHistoricalTrades(Long productId, Long buyerId, Long sellerId) {
        // 为每个商品创建20条历史交易记录，价格有波动
        BigDecimal basePrice = new BigDecimal("400").add(new BigDecimal(productId).multiply(new BigDecimal("50")));
        
        for (int i = 0; i < 20; i++) {
            BigDecimal price = basePrice.add(new BigDecimal(random.nextInt(100) - 50));
            BigDecimal quantity = new BigDecimal(50 + random.nextInt(150));
            LocalDateTime tradeTime = LocalDateTime.now().minusDays(7 - i / 3).minusHours(random.nextInt(24));
            
            Trade trade = new Trade();
            trade.setBuyOrderId((long) (100 + i));
            trade.setSellOrderId((long) (200 + i));
            trade.setProductId(productId);
            trade.setQuantity(quantity);
            trade.setPrice(price);
            trade.setBuyerId(buyerId);
            trade.setSellerId(sellerId);
            trade.setTradeTime(tradeTime);
            tradeRepository.save(trade);
        }
    }
}
