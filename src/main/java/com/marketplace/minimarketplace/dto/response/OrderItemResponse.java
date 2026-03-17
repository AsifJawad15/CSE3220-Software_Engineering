package com.marketplace.minimarketplace.dto.response;
import java.math.BigDecimal;
public class OrderItemResponse {
    private Long productId; private String productName; private int quantity; private BigDecimal unitPrice; private BigDecimal subtotal;
    public OrderItemResponse() {} public OrderItemResponse(Long productId, String productName, int quantity, BigDecimal unitPrice, BigDecimal subtotal) { this.productId=productId; this.productName=productName; this.quantity=quantity; this.unitPrice=unitPrice; this.subtotal=subtotal; }
    public Long getProductId(){return productId;} public String getProductName(){return productName;} public int getQuantity(){return quantity;} public BigDecimal getUnitPrice(){return unitPrice;} public BigDecimal getSubtotal(){return subtotal;}
    public static OIRBuilder builder(){return new OIRBuilder();}
    public static class OIRBuilder { private Long productId; private String productName; private int quantity; private BigDecimal unitPrice; private BigDecimal subtotal;
        public OIRBuilder productId(Long v){this.productId=v;return this;} public OIRBuilder productName(String v){this.productName=v;return this;}
        public OIRBuilder quantity(int v){this.quantity=v;return this;} public OIRBuilder unitPrice(BigDecimal v){this.unitPrice=v;return this;}
        public OIRBuilder subtotal(BigDecimal v){this.subtotal=v;return this;} public OrderItemResponse build(){return new OrderItemResponse(productId,productName,quantity,unitPrice,subtotal);} }
}