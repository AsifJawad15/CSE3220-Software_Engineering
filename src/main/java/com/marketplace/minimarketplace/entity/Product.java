package com.marketplace.minimarketplace.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    @Column(nullable = false) private int stock;
    private String description;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY) private List<OrderItem> orderItems = new ArrayList<>();
    public Product() {}
    public Product(Long id, String name, BigDecimal price, int stock, String description) {
        this.id = id; this.name = name; this.price = price; this.stock = stock; this.description = description;
    }
    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; } public void setPrice(BigDecimal price) { this.price = price; }
    public int getStock() { return stock; } public void setStock(int stock) { this.stock = stock; }
    public String getDescription() { return description; } public void setDescription(String d) { this.description = d; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public static PBuilder builder() { return new PBuilder(); }
    public static class PBuilder {
        private Long id; private String name; private BigDecimal price; private int stock; private String description;
        public PBuilder id(Long v) { this.id = v; return this; }
        public PBuilder name(String v) { this.name = v; return this; }
        public PBuilder price(BigDecimal v) { this.price = v; return this; }
        public PBuilder stock(int v) { this.stock = v; return this; }
        public PBuilder description(String v) { this.description = v; return this; }
        public Product build() { return new Product(id, name, price, stock, description); }
    }
}