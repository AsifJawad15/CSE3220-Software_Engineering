package com.marketplace.minimarketplace.service;
import com.marketplace.minimarketplace.dto.request.OrderItemRequest;
import com.marketplace.minimarketplace.dto.request.OrderRequest;
import com.marketplace.minimarketplace.dto.response.OrderResponse;
import com.marketplace.minimarketplace.entity.*;
import com.marketplace.minimarketplace.exception.BadRequestException;
import com.marketplace.minimarketplace.exception.InsufficientStockException;
import com.marketplace.minimarketplace.exception.ResourceNotFoundException;
import com.marketplace.minimarketplace.mapper.OrderMapper;
import com.marketplace.minimarketplace.pattern.decorator.BaseProductComponent;
import com.marketplace.minimarketplace.pattern.decorator.ExpressShippingDecorator;
import com.marketplace.minimarketplace.pattern.decorator.GiftWrapDecorator;
import com.marketplace.minimarketplace.pattern.decorator.ProductComponent;
import com.marketplace.minimarketplace.pattern.observer.OrderEvent;
import com.marketplace.minimarketplace.pattern.observer.OrderSubject;
import com.marketplace.minimarketplace.pattern.strategy.PricingStrategy;
import com.marketplace.minimarketplace.pattern.strategy.PricingStrategyFactory;
import com.marketplace.minimarketplace.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
public class OrderService {
    private static final Set<String> VALID_STATUSES = Set.of(
            "PENDING", "PLACED", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PricingStrategyFactory pricingStrategyFactory;
    private final OrderSubject orderSubject;
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, PricingStrategyFactory pricingStrategyFactory, OrderSubject orderSubject) {
        this.orderRepository=orderRepository; this.productRepository=productRepository; this.userRepository=userRepository; this.pricingStrategyFactory=pricingStrategyFactory; this.orderSubject=orderSubject;
    }
    /**
     * Place a new order.
     * Demonstrates Strategy (pricing), Observer (notification), and Decorator (product extras).
     */
    @Transactional
    public OrderResponse placeOrder(String userEmail, OrderRequest request) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        PricingStrategy strategy = pricingStrategyFactory.getStrategy(request.getPricingStrategy());
        Order order = Order.builder().user(user).status("PLACED").totalAmount(BigDecimal.ZERO).build();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemReq.getProductId()));
            if (product.getStock() < itemReq.getQuantity()) throw new InsufficientStockException("Insufficient stock for product: " + product.getName() + " (available: " + product.getStock() + ", requested: " + itemReq.getQuantity() + ")");
            product.setStock(product.getStock() - itemReq.getQuantity());
            productRepository.save(product);
            // --- Decorator pattern: wrap the product with optional extras ---
            ProductComponent component = new BaseProductComponent(product.getName(), product.getDescription(), product.getPrice());
            if (itemReq.isGiftWrap()) { component = new GiftWrapDecorator(component); }
            if (itemReq.isExpressShipping()) { component = new ExpressShippingDecorator(component); }
            // --- Strategy pattern: apply pricing/discount ---
            BigDecimal itemTotal = strategy.calculatePrice(component.getPrice(), itemReq.getQuantity());
            OrderItem orderItem = OrderItem.builder().order(order).product(product).quantity(itemReq.getQuantity()).unitPrice(component.getPrice()).build();
            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(itemTotal);
        }
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        // --- Observer pattern: notify all observers ---
        orderSubject.notifyObservers(OrderEvent.builder().orderId(savedOrder.getId()).userId(user.getId()).totalAmount(totalAmount).status("PLACED").timestamp(LocalDateTime.now()).build());
        OrderResponse response = OrderMapper.toResponse(savedOrder);
        response.setAppliedStrategy(strategy.getStrategyName());
        return response;
    }
    public List<OrderResponse> getOrdersByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId()).stream().map(OrderMapper::toResponse).collect(Collectors.toList());
    }

    /**
     * Update order status (PATCH).
     * Triggers Observer pattern — all observers are notified of the status change.
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String newStatus, String userEmail) {
        String upper = newStatus.toUpperCase();
        if (!VALID_STATUSES.contains(upper)) {
            throw new BadRequestException("Invalid order status: " + newStatus
                    + ". Valid statuses: " + VALID_STATUSES);
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }
        order.setStatus(upper);
        Order saved = orderRepository.save(order);

        // --- Observer: notify all observers of status change ---
        orderSubject.notifyObservers(
                OrderEvent.builder()
                        .orderId(saved.getId())
                        .userId(saved.getUser().getId())
                        .totalAmount(saved.getTotalAmount())
                        .status(upper)
                        .timestamp(LocalDateTime.now())
                        .build());

        return OrderMapper.toResponse(saved);
    }

    public OrderResponse getOrderById(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        if (!order.getUser().getEmail().equals(userEmail)) throw new ResourceNotFoundException("Order not found with id: " + orderId);
        return OrderMapper.toResponse(order);
    }
    public List<OrderResponse> getAllOrders() { return orderRepository.findAll().stream().map(OrderMapper::toResponse).collect(Collectors.toList()); }

    /**
     * Admin-level status update — no ownership check.
     */
    @Transactional
    public OrderResponse adminUpdateOrderStatus(Long orderId, String newStatus) {
        String upper = newStatus.toUpperCase();
        if (!VALID_STATUSES.contains(upper)) {
            throw new BadRequestException("Invalid order status: " + newStatus
                    + ". Valid statuses: " + VALID_STATUSES);
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        order.setStatus(upper);
        Order saved = orderRepository.save(order);

        orderSubject.notifyObservers(
                OrderEvent.builder()
                        .orderId(saved.getId())
                        .userId(saved.getUser().getId())
                        .totalAmount(saved.getTotalAmount())
                        .status(upper)
                        .timestamp(LocalDateTime.now())
                        .build());

        return OrderMapper.toResponse(saved);
    }
}