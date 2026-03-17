package com.marketplace.minimarketplace.service;
import com.marketplace.minimarketplace.dto.request.ProductRequest;
import com.marketplace.minimarketplace.dto.response.ProductResponse;
import com.marketplace.minimarketplace.entity.Product;
import com.marketplace.minimarketplace.exception.ResourceNotFoundException;
import com.marketplace.minimarketplace.mapper.ProductMapper;
import com.marketplace.minimarketplace.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) { this.productRepository = productRepository; }
    public Page<ProductResponse> getAllProducts(Pageable pageable) { return productRepository.findAll(pageable).map(ProductMapper::toResponse); }
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) { return productRepository.findByNameContainingIgnoreCase(keyword, pageable).map(ProductMapper::toResponse); }
    public ProductResponse getProductById(Long id) { return ProductMapper.toResponse(findProductOrThrow(id)); }
    @Transactional public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder().name(request.getName()).price(request.getPrice()).stock(request.getStock()).description(request.getDescription()).build();
        return ProductMapper.toResponse(productRepository.save(product)); }
    @Transactional public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProductOrThrow(id); product.setName(request.getName()); product.setPrice(request.getPrice()); product.setStock(request.getStock()); product.setDescription(request.getDescription());
        return ProductMapper.toResponse(productRepository.save(product)); }
    @Transactional public void deleteProduct(Long id) { productRepository.delete(findProductOrThrow(id)); }
    private Product findProductOrThrow(Long id) { return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id)); }
}