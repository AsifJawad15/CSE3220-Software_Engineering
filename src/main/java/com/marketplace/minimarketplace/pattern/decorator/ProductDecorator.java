package com.marketplace.minimarketplace.pattern.decorator;

import java.math.BigDecimal;

/**
 * Abstract decorator — wraps a {@link ProductComponent} and delegates
 * all calls by default.  Concrete decorators override to add behaviour.
 */
public abstract class ProductDecorator implements ProductComponent {

    protected final ProductComponent wrapped;

    protected ProductDecorator(ProductComponent wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription();
    }

    @Override
    public BigDecimal getPrice() {
        return wrapped.getPrice();
    }
}

