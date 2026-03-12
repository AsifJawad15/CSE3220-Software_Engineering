package com.marketplace.minimarketplace.pattern.decorator;

import java.math.BigDecimal;

/**
 * Concrete decorator — adds an express-shipping surcharge to a product.
 */
public class ExpressShippingDecorator extends ProductDecorator {

    private static final BigDecimal EXPRESS_COST = new BigDecimal("15.00");

    public ExpressShippingDecorator(ProductComponent wrapped) {
        super(wrapped);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Express Shipping";
    }

    @Override
    public BigDecimal getPrice() {
        return wrapped.getPrice().add(EXPRESS_COST);
    }
}

