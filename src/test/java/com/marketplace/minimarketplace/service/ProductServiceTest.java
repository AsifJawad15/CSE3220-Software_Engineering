package com.marketplace.minimarketplace.service;
import com.marketplace.minimarketplace.dto.request.ProductRequest;
import com.marketplace.minimarketplace.dto.response.ProductResponse;
import com.marketplace.minimarketplace.entity.Product;
import com.marketplace.minimarketplace.exception.ResourceNotFoundException;
import com.marketplace.minimarketplace.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;
    @Test
    void getProductById_existingProduct_returnsResponse() {
        Product product = Product.builder()
                .id(1L).name("Widget").price(new BigDecimal("19.99")).stock(100).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ProductResponse response = productService.getProductById(1L);
        assertEquals("Widget", response.getName());
        assertEquals(new BigDecimal("19.99"), response.getPrice());
        verify(productRepository).findById(1L);
    }
    @Test
    void getProductById_nonExistingProduct_throwsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }
    @Test
    void createProduct_validRequest_returnsResponse() {
        ProductRequest request = ProductRequest.builder()
                .name("New Product").price(new BigDecimal("29.99")).stock(50).description("desc").build();
        Product saved = Product.builder()
                .id(1L).name("New Product").price(new BigDecimal("29.99")).stock(50).description("desc").build();
        when(productRepository.save(any(Product.class))).thenReturn(saved);
        ProductResponse response = productService.createProduct(request);
        assertEquals(1L, response.getId());
        assertEquals("New Product", response.getName());
        verify(productRepository).save(any(Product.class));
    }
    @Test
    void deleteProduct_existingProduct_deletesSuccessfully() {
        Product product = Product.builder().id(1L).name("Widget").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository).delete(product);
    }
    @Test
    void deleteProduct_nonExistingProduct_throwsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
    }
}