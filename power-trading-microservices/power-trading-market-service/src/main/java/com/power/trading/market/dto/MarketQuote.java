package com.power.trading.market.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MarketQuote {
    private Long productId;
    private BigDecimal latestPrice;  // 最新价
    private BigDecimal maxPrice;     // 最高价
    private BigDecimal minPrice;     // 最低价
    private BigDecimal volume;       // 成交量
    private BigDecimal amount;       // 成交额
}
