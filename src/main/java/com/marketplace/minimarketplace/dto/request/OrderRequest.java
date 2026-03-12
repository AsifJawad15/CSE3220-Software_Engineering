package com.marketplace.minimarketplace.dto.request;
import jakarta.validation.Valid; import jakarta.validation.constraints.NotEmpty; import java.util.List;
public class OrderRequest {
    @NotEmpty(message = "Order must contain at least one item") @Valid private List<OrderItemRequest> items;
    private String pricingStrategy;
    public OrderRequest() {} public OrderRequest(List<OrderItemRequest> items, String pricingStrategy) { this.items=items; this.pricingStrategy=pricingStrategy; }
    public List<OrderItemRequest> getItems() { return items; } public void setItems(List<OrderItemRequest> v) { this.items=v; }
    public String getPricingStrategy() { return pricingStrategy; } public void setPricingStrategy(String v) { this.pricingStrategy=v; }
    public static ORBuilder builder() { return new ORBuilder(); }
    public static class ORBuilder { private List<OrderItemRequest> items; private String pricingStrategy;
        public ORBuilder items(List<OrderItemRequest> v){this.items=v;return this;} public ORBuilder pricingStrategy(String v){this.pricingStrategy=v;return this;}
        public OrderRequest build(){return new OrderRequest(items,pricingStrategy);} }
}