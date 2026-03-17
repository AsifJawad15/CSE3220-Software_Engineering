package com.marketplace.minimarketplace.repository;
import com.marketplace.minimarketplace.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
