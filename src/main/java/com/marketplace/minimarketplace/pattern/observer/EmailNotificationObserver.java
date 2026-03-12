package com.marketplace.minimarketplace.pattern.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Concrete observer — simulates sending an email notification
 * when an order status changes.
 */
@Component
public class EmailNotificationObserver implements OrderStatusObserver {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationObserver.class);

    @Override
    public void update(OrderEvent event) {
        log.info("[EMAIL] Notification sent for order #{} to user #{} — status: {}, amount: {}",
                event.getOrderId(), event.getUserId(), event.getStatus(), event.getTotalAmount());
    }
}

