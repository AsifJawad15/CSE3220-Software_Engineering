package com.marketplace.minimarketplace.pattern.strategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
@Component("percentageDiscount")
public class PercentageDiscountStrategy implements PricingStrategy {
    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10"); // 10%
    @Override
    public BigDecimal calculatePrice(BigDecimal originalPrice, int quantity) {
        BigDecimal total = originalPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal discount = total.multiply(DISCOUNT_RATE);
        return total.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }
    @Override
    public String getStrategyName() {
        return "10% Discount";
    }
}
