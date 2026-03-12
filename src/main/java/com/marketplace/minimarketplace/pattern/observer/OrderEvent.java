package com.marketplace.minimarketplace.pattern.observer;
import java.math.BigDecimal; import java.time.LocalDateTime;
public class OrderEvent {
    private Long orderId; private Long userId; private BigDecimal totalAmount; private String status; private LocalDateTime timestamp;
    public OrderEvent() {}
    public Long getOrderId(){return orderId;} public Long getUserId(){return userId;} public BigDecimal getTotalAmount(){return totalAmount;} public String getStatus(){return status;} public LocalDateTime getTimestamp(){return timestamp;}
    public static OEBuilder builder(){return new OEBuilder();}
    public static class OEBuilder { private Long orderId,userId; private BigDecimal totalAmount; private String status; private LocalDateTime timestamp;
        public OEBuilder orderId(Long v){this.orderId=v;return this;} public OEBuilder userId(Long v){this.userId=v;return this;}
        public OEBuilder totalAmount(BigDecimal v){this.totalAmount=v;return this;} public OEBuilder status(String v){this.status=v;return this;}
        public OEBuilder timestamp(LocalDateTime v){this.timestamp=v;return this;}
        public OrderEvent build(){OrderEvent e=new OrderEvent(); e.orderId=orderId; e.userId=userId; e.totalAmount=totalAmount; e.status=status; e.timestamp=timestamp; return e;} }
}