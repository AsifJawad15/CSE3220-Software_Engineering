package com.marketplace.minimarketplace.pattern.decorator;

import java.math.BigDecimal;

/**
 * Concrete decorator — adds a gift-wrap surcharge and label to a product.
 */
public class GiftWrapDecorator extends ProductDecorator {

    private static final BigDecimal GIFT_WRAP_COST = new BigDecimal("5.00");

    public GiftWrapDecorator(ProductComponent wrapped) {
        super(wrapped);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Gift Wrap";
    }

    @Override
    public BigDecimal getPrice() {
        return wrapped.getPrice().add(GIFT_WRAP_COST);
    }
}

