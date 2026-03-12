package com.marketplace.minimarketplace.controller;
import com.marketplace.minimarketplace.dto.request.OrderRequest;
import com.marketplace.minimarketplace.dto.response.OrderResponse;
import com.marketplace.minimarketplace.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/orders") @Tag(name = "Orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }
    @PostMapping @Operation(summary = "Place a new order")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request, Authentication auth) { return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(auth.getName(), request)); }
    @GetMapping @Operation(summary = "Get my orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication auth) { return ResponseEntity.ok(orderService.getOrdersByUser(auth.getName())); }
    @GetMapping("/{id}") @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id, Authentication auth) { return ResponseEntity.ok(orderService.getOrderById(id, auth.getName())); }
}