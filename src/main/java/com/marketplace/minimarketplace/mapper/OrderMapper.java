package com.marketplace.minimarketplace.mapper;
import com.marketplace.minimarketplace.dto.response.OrderItemResponse;
import com.marketplace.minimarketplace.dto.response.OrderResponse;
import com.marketplace.minimarketplace.entity.Order;
import java.math.BigDecimal;
import java.util.stream.Collectors;
public class OrderMapper {
    private OrderMapper() {}
    public static OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .items(order.getItems().stream()
                        .map(item -> OrderItemResponse.builder()
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
