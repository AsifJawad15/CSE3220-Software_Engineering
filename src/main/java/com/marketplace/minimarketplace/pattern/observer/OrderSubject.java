package com.marketplace.minimarketplace.pattern.observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject that maintains a list of {@link OrderStatusObserver}s
 * and notifies them whenever an order event occurs.
 */
@Component
public class OrderSubject {

    private final List<OrderStatusObserver> observers = new ArrayList<>();

    /**
     * Spring auto-injects every bean that implements OrderStatusObserver.
     */
    public OrderSubject(List<OrderStatusObserver> observers) {
        this.observers.addAll(observers);
    }

    public void subscribe(OrderStatusObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(OrderStatusObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notify all registered observers about the order event.
     */
    public void notifyObservers(OrderEvent event) {
        for (OrderStatusObserver observer : observers) {
            observer.update(event);
        }
    }
}

