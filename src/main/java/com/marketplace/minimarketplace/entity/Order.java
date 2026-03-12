package com.marketplace.minimarketplace.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(name = "order_date", nullable = false) private LocalDateTime orderDate;
    @Column(nullable = false) private String status;
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2) private BigDecimal totalAmount;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) private List<OrderItem> items = new ArrayList<>();
    public Order() {}
    @PrePersist protected void onCreate() { this.orderDate = LocalDateTime.now(); if (this.status == null) this.status = "PENDING"; }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public User getUser() { return user; } public void setUser(User user) { this.user = user; }
    public LocalDateTime getOrderDate() { return orderDate; } public void setOrderDate(LocalDateTime v) { this.orderDate = v; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; } public void setTotalAmount(BigDecimal v) { this.totalAmount = v; }
    public List<OrderItem> getItems() { return items; } public void setItems(List<OrderItem> items) { this.items = items; }
    public static OBuilder builder() { return new OBuilder(); }
    public static class OBuilder {
        private User user; private String status; private BigDecimal totalAmount;
        public OBuilder user(User v) { this.user = v; return this; }
        public OBuilder status(String v) { this.status = v; return this; }
        public OBuilder totalAmount(BigDecimal v) { this.totalAmount = v; return this; }
        public Order build() { Order o = new Order(); o.user = user; o.status = status; o.totalAmount = totalAmount; return o; }
    }
}