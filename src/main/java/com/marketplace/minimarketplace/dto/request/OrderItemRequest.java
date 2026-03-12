package com.marketplace.minimarketplace.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderItemRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private boolean giftWrap;
    private boolean expressShipping;

    public OrderItemRequest() {}

    public OrderItemRequest(Long productId, int quantity, boolean giftWrap, boolean expressShipping) {
        this.productId = productId;
        this.quantity = quantity;
        this.giftWrap = giftWrap;
        this.expressShipping = expressShipping;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long v) { this.productId = v; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int v) { this.quantity = v; }

    public boolean isGiftWrap() { return giftWrap; }
    public void setGiftWrap(boolean v) { this.giftWrap = v; }

    public boolean isExpressShipping() { return expressShipping; }
    public void setExpressShipping(boolean v) { this.expressShipping = v; }

    public static OIRBuilder builder() { return new OIRBuilder(); }

    public static class OIRBuilder {
        private Long productId;
        private int quantity;
        private boolean giftWrap;
        private boolean expressShipping;

        public OIRBuilder productId(Long v) { this.productId = v; return this; }
        public OIRBuilder quantity(int v) { this.quantity = v; return this; }
        public OIRBuilder giftWrap(boolean v) { this.giftWrap = v; return this; }
        public OIRBuilder expressShipping(boolean v) { this.expressShipping = v; return this; }

        public OrderItemRequest build() {
            return new OrderItemRequest(productId, quantity, giftWrap, expressShipping);
        }
    }
}