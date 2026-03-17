package com.marketplace.minimarketplace.dto.request;

import jakarta.validation.constraints.NotBlank;

public class OrderStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;

    public OrderStatusRequest() {}

    public OrderStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
}

