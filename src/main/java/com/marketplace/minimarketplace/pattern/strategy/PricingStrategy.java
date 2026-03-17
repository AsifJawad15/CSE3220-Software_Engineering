package com.marketplace.minimarketplace.pattern.strategy;
import java.math.BigDecimal;
public interface PricingStrategy {
    BigDecimal calculatePrice(BigDecimal originalPrice, int quantity);
    String getStrategyName();
}
