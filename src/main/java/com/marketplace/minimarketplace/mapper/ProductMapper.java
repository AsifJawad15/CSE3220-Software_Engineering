package com.marketplace.minimarketplace.mapper;
import com.marketplace.minimarketplace.dto.response.ProductResponse;
import com.marketplace.minimarketplace.entity.Product;
public class ProductMapper {
    private ProductMapper() {}
    public static ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .description(product.getDescription())
                .build();
    }
}
