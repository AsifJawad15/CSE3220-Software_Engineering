package com.marketplace.minimarketplace.pattern.strategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
@Component("bulkDiscount")
public class BulkDiscountStrategy implements PricingStrategy {
    private static final int BULK_THRESHOLD = 5;
    private static final BigDecimal BULK_DISCOUNT = new BigDecimal("0.15"); // 15%
    @Override
    public BigDecimal calculatePrice(BigDecimal originalPrice, int quantity) {
        BigDecimal total = originalPrice.multiply(BigDecimal.valueOf(quantity));
        if (quantity >= BULK_THRESHOLD) {
            BigDecimal discount = total.multiply(BULK_DISCOUNT);
            return total.subtract(discount).setScale(2, java.math.RoundingMode.HALF_UP);
        }
        return total;
    }
    @Override
    public String getStrategyName() {
        return "Bulk Discount (15% for 5+ items)";
    }
}
