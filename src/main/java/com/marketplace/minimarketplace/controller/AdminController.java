package com.marketplace.minimarketplace.controller;
import com.marketplace.minimarketplace.dto.request.OrderStatusRequest;
import com.marketplace.minimarketplace.dto.response.OrderResponse;
import com.marketplace.minimarketplace.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/admin") @Tag(name = "Admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final OrderService orderService;
    public AdminController(OrderService orderService) { this.orderService = orderService; }
    @GetMapping("/orders") @Operation(summary = "Get all orders (Admin only)")
    public ResponseEntity<List<OrderResponse>> getAllOrders() { return ResponseEntity.ok(orderService.getAllOrders()); }
    @PatchMapping("/orders/{id}/status") @Operation(summary = "Update any order status (Admin only, triggers Observer)")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusRequest request) {
        return ResponseEntity.ok(orderService.adminUpdateOrderStatus(id, request.getStatus())); }
}