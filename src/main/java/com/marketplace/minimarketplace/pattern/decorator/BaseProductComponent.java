package com.marketplace.minimarketplace.pattern.decorator;

import java.math.BigDecimal;

/**
 * Concrete component — wraps a real Product entity as the base component.
 */
public class BaseProductComponent implements ProductComponent {

    private final String name;
    private final String description;
    private final BigDecimal price;

    public BaseProductComponent(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }
}

