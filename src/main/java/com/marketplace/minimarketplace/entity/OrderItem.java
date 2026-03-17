package com.marketplace.minimarketplace.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false) private Order order;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false) private Product product;
    @Column(nullable = false) private int quantity;
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2) private BigDecimal unitPrice;
    public OrderItem() {}
    public Long getId() { return id; }
    public Order getOrder() { return order; } public void setOrder(Order order) { this.order = order; }
    public Product getProduct() { return product; } public void setProduct(Product product) { this.product = product; }
    public int getQuantity() { return quantity; } public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; } public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public static OIBuilder builder() { return new OIBuilder(); }
    public static class OIBuilder {
        private Order order; private Product product; private int quantity; private BigDecimal unitPrice;
        public OIBuilder order(Order v) { this.order = v; return this; }
        public OIBuilder product(Product v) { this.product = v; return this; }
        public OIBuilder quantity(int v) { this.quantity = v; return this; }
        public OIBuilder unitPrice(BigDecimal v) { this.unitPrice = v; return this; }
        public OrderItem build() { OrderItem i = new OrderItem(); i.order = order; i.product = product; i.quantity = quantity; i.unitPrice = unitPrice; return i; }
    }
}