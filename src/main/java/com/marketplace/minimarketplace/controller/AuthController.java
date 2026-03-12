package com.marketplace.minimarketplace.controller;
import com.marketplace.minimarketplace.dto.request.LoginRequest;
import com.marketplace.minimarketplace.dto.request.RegisterRequest;
import com.marketplace.minimarketplace.dto.response.AuthResponse;
import com.marketplace.minimarketplace.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") @Tag(name = "Authentication")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }
    @PostMapping("/register") @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request)); }
    @PostMapping("/login") @Operation(summary = "Login and get JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) { return ResponseEntity.ok(authService.login(request)); }
}