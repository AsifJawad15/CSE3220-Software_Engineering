package com.marketplace.minimarketplace.service;

import com.marketplace.minimarketplace.dto.request.OrderItemRequest;
import com.marketplace.minimarketplace.dto.request.OrderRequest;
import com.marketplace.minimarketplace.dto.response.OrderResponse;
import com.marketplace.minimarketplace.entity.Order;
import com.marketplace.minimarketplace.entity.Product;
import com.marketplace.minimarketplace.entity.User;
import com.marketplace.minimarketplace.exception.BadRequestException;
import com.marketplace.minimarketplace.exception.InsufficientStockException;
import com.marketplace.minimarketplace.exception.ResourceNotFoundException;
import com.marketplace.minimarketplace.pattern.observer.OrderSubject;
import com.marketplace.minimarketplace.pattern.strategy.RegularPricingStrategy;
import com.marketplace.minimarketplace.pattern.strategy.PricingStrategyFactory;
import com.marketplace.minimarketplace.repository.OrderRepository;
import com.marketplace.minimarketplace.repository.ProductRepository;
import com.marketplace.minimarketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private PricingStrategyFactory pricingStrategyFactory;
    @Mock private OrderSubject orderSubject;

    @InjectMocks
    private OrderService orderService;

    @Test
    void placeOrder_insufficientStock_throwsException() {
        User user = User.builder().id(1L).email("test@test.com").build();
        Product product = Product.builder().id(1L).name("Widget").price(new BigDecimal("10.00")).stock(2).build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(pricingStrategyFactory.getStrategy(null)).thenReturn(new RegularPricingStrategy());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        OrderRequest request = OrderRequest.builder()
                .items(List.of(OrderItemRequest.builder().productId(1L).quantity(5).build()))
                .build();

        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder("test@test.com", request));
    }

    @Test
    void placeOrder_productNotFound_throwsException() {
        User user = User.builder().id(1L).email("test@test.com").build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(pricingStrategyFactory.getStrategy(null)).thenReturn(new RegularPricingStrategy());
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        OrderRequest request = OrderRequest.builder()
                .items(List.of(OrderItemRequest.builder().productId(99L).quantity(1).build()))
                .build();

        assertThrows(ResourceNotFoundException.class, () -> orderService.placeOrder("test@test.com", request));
    }

    @Test
    void placeOrder_userNotFound_throwsException() {
        when(userRepository.findByEmail("nobody@test.com")).thenReturn(Optional.empty());

        OrderRequest request = OrderRequest.builder()
                .items(List.of(OrderItemRequest.builder().productId(1L).quantity(1).build()))
                .build();

        assertThrows(ResourceNotFoundException.class, () -> orderService.placeOrder("nobody@test.com", request));
    }

    @Test
    void updateOrderStatus_invalidStatus_throwsBadRequest() {
        assertThrows(BadRequestException.class,
                () -> orderService.updateOrderStatus(1L, "INVALID_STATUS", "test@test.com"));
    }

    @Test
    void updateOrderStatus_orderNotFound_throwsNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.updateOrderStatus(99L, "SHIPPED", "test@test.com"));
    }

    @Test
    void updateOrderStatus_validStatus_updatesAndNotifies() {
        User user = User.builder().id(1L).email("test@test.com").build();
        Order order = Order.builder().user(user).status("PLACED").totalAmount(new BigDecimal("100.00")).build();
        order.setId(1L);
        order.setItems(new ArrayList<>());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.updateOrderStatus(1L, "SHIPPED", "test@test.com");

        assertEquals("SHIPPED", order.getStatus());
        verify(orderSubject).notifyObservers(any());
        verify(orderRepository).save(order);
    }
}