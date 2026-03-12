package com.marketplace.minimarketplace.pattern.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Default (regular) pricing — no discount applied.
 */
@Component("regularPricing")
public class RegularPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(BigDecimal originalPrice, int quantity) {
        return originalPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String getStrategyName() {
        return "Regular Pricing (No Discount)";
    }
}

