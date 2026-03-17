package com.marketplace.minimarketplace.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", unique = true, nullable = false) private User user;
    private String phone;
    private String address;
    public UserProfile() {}
    public UserProfile(Long id, User user, String phone, String address) { this.id = id; this.user = user; this.phone = phone; this.address = address; }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public User getUser() { return user; } public void setUser(User user) { this.user = user; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; } public void setAddress(String address) { this.address = address; }
    public static UPBuilder builder() { return new UPBuilder(); }
    public static class UPBuilder {
        private Long id; private User user; private String phone; private String address;
        public UPBuilder id(Long v) { this.id = v; return this; }
        public UPBuilder user(User v) { this.user = v; return this; }
        public UPBuilder phone(String v) { this.phone = v; return this; }
        public UPBuilder address(String v) { this.address = v; return this; }
        public UserProfile build() { return new UserProfile(id, user, phone, address); }
    }
}