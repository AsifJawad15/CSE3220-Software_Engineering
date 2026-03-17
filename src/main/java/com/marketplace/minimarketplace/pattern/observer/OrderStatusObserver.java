package com.marketplace.minimarketplace.pattern.observer;

/**
 * Observer interface for order status changes.
 * Implementations are notified whenever an order event occurs.
 */
public interface OrderStatusObserver {
    void update(OrderEvent event);
}

