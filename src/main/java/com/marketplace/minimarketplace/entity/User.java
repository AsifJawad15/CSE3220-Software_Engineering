package com.marketplace.minimarketplace.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String password;
    @Column(nullable = false) private String role;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserProfile profile;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
    public User() {}
    public User(Long id, String name, String email, String password, String role) {
        this.id = id; this.name = name; this.email = email; this.password = password; this.role = role;
    }
    @PrePersist protected void onCreate() { this.createdAt = LocalDateTime.now(); if (this.role == null) this.role = "ROLE_USER"; }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; } public void setRole(String role) { this.role = role; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public UserProfile getProfile() { return profile; } public void setProfile(UserProfile profile) { this.profile = profile; }
    public List<Order> getOrders() { return orders; } public void setOrders(List<Order> orders) { this.orders = orders; }
    public static UserBuilder builder() { return new UserBuilder(); }
    public static class UserBuilder {
        private Long id; private String name; private String email; private String password; private String role;
        public UserBuilder id(Long v) { this.id = v; return this; }
        public UserBuilder name(String v) { this.name = v; return this; }
        public UserBuilder email(String v) { this.email = v; return this; }
        public UserBuilder password(String v) { this.password = v; return this; }
        public UserBuilder role(String v) { this.role = v; return this; }
        public User build() { return new User(id, name, email, password, role); }
    }
}