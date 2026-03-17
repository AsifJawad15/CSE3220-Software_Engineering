package com.marketplace.minimarketplace.controller;
import com.marketplace.minimarketplace.dto.request.ProductRequest;
import com.marketplace.minimarketplace.dto.response.ProductResponse;
import com.marketplace.minimarketplace.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/products") @Tag(name = "Products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) { this.productService = productService; }
    @GetMapping @Operation(summary = "List all products (paginated)")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(@RequestParam(required = false) String search, @PageableDefault(size = 10) Pageable pageable) {
        if (search != null && !search.isBlank()) return ResponseEntity.ok(productService.searchProducts(search, pageable));
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }
    @GetMapping("/{id}") @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) { return ResponseEntity.ok(productService.getProductById(id)); }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping @Operation(summary = "Create product (Admin)")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request)); }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}") @Operation(summary = "Update product (Admin)")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) { return ResponseEntity.ok(productService.updateProduct(id, request)); }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}") @Operation(summary = "Delete product (Admin)")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) { productService.deleteProduct(id); return ResponseEntity.noContent().build(); }
}