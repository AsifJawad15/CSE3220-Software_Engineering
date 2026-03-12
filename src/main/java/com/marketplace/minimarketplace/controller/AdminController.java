package com.marketplace.minimarketplace.controller;
import com.marketplace.minimarketplace.dto.response.OrderResponse;
import com.marketplace.minimarketplace.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/admin") @Tag(name = "Admin")
public class AdminController {
    private final OrderService orderService;
    public AdminController(OrderService orderService) { this.orderService = orderService; }
    @GetMapping("/orders") @Operation(summary = "Get all orders (Admin only)")
    public ResponseEntity<List<OrderResponse>> getAllOrders() { return ResponseEntity.ok(orderService.getAllOrders()); }
}