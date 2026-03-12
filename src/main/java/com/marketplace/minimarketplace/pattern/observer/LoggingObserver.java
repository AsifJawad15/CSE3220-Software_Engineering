package com.marketplace.minimarketplace.pattern.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Concrete observer — logs every order status change
 * for auditing / analytics purposes.
 */
@Component
public class LoggingObserver implements OrderStatusObserver {

    private static final Logger log = LoggerFactory.getLogger(LoggingObserver.class);

    @Override
    public void update(OrderEvent event) {
        log.info("[LOG] Order #{} | status: {} | amount: {} | user: {} | time: {}",
                event.getOrderId(), event.getStatus(), event.getTotalAmount(),
                event.getUserId(), event.getTimestamp());
    }
}

