package com.marketplace.minimarketplace.pattern.strategy;
import org.springframework.stereotype.Component;
import java.util.Map;
@Component
public class PricingStrategyFactory {
    private final Map<String, PricingStrategy> strategies;
    public PricingStrategyFactory(Map<String, PricingStrategy> strategies) {
        this.strategies = strategies;
    }
    public PricingStrategy getStrategy(String strategyName) {
        if (strategyName == null || strategyName.isBlank()) {
            return strategies.get("regularPricing");
        }
        PricingStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            return strategies.get("regularPricing");
        }
        return strategy;
    }
}
