package com.marketplace.minimarketplace.pattern.decorator;

import java.math.BigDecimal;

/**
 * Component interface for the Decorator pattern.
 * Represents the common contract for a product (or decorated product).
 */
public interface ProductComponent {

    String getName();

    String getDescription();

    BigDecimal getPrice();
}

