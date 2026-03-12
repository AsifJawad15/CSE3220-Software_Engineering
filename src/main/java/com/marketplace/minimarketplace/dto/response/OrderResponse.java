package com.marketplace.minimarketplace.dto.response;
import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.List;
public class OrderResponse {
    private Long id; private String status; private BigDecimal totalAmount; private LocalDateTime orderDate; private List<OrderItemResponse> items; private String appliedStrategy;
    public OrderResponse() {}
    public Long getId(){return id;} public void setId(Long v){this.id=v;}
    public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
    public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal v){this.totalAmount=v;}
    public LocalDateTime getOrderDate(){return orderDate;} public void setOrderDate(LocalDateTime v){this.orderDate=v;}
    public List<OrderItemResponse> getItems(){return items;} public void setItems(List<OrderItemResponse> v){this.items=v;}
    public String getAppliedStrategy(){return appliedStrategy;} public void setAppliedStrategy(String v){this.appliedStrategy=v;}
    public static ORBuilder builder(){return new ORBuilder();}
    public static class ORBuilder { private Long id; private String status; private BigDecimal totalAmount; private LocalDateTime orderDate; private List<OrderItemResponse> items;
        public ORBuilder id(Long v){this.id=v;return this;} public ORBuilder status(String v){this.status=v;return this;}
        public ORBuilder totalAmount(BigDecimal v){this.totalAmount=v;return this;} public ORBuilder orderDate(LocalDateTime v){this.orderDate=v;return this;}
        public ORBuilder items(List<OrderItemResponse> v){this.items=v;return this;}
        public OrderResponse build(){OrderResponse r=new OrderResponse(); r.id=id; r.status=status; r.totalAmount=totalAmount; r.orderDate=orderDate; r.items=items; return r;} }
}