package com.marketplace.minimarketplace.dto.response;

import java.time.LocalDateTime;

public class CustomerResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createdAt;

    public CustomerResponse() {}

    public CustomerResponse(Long id, String name, String email, String phone,
                            String address, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }

    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }

    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    public static CRBuilder builder() { return new CRBuilder(); }

    public static class CRBuilder {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String address;
        private LocalDateTime createdAt;

        public CRBuilder id(Long v) { this.id = v; return this; }
        public CRBuilder name(String v) { this.name = v; return this; }
        public CRBuilder email(String v) { this.email = v; return this; }
        public CRBuilder phone(String v) { this.phone = v; return this; }
        public CRBuilder address(String v) { this.address = v; return this; }
        public CRBuilder createdAt(LocalDateTime v) { this.createdAt = v; return this; }

        public CustomerResponse build() {
            return new CustomerResponse(id, name, email, phone, address, createdAt);
        }
    }
}

