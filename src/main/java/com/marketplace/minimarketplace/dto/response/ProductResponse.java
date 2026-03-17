package com.marketplace.minimarketplace.dto.response;
import java.math.BigDecimal;
public class ProductResponse {
    private Long id; private String name; private BigDecimal price; private int stock; private String description;
    public ProductResponse() {} public ProductResponse(Long id, String name, BigDecimal price, int stock, String description) { this.id=id; this.name=name; this.price=price; this.stock=stock; this.description=description; }
    public Long getId(){return id;} public String getName(){return name;} public BigDecimal getPrice(){return price;} public int getStock(){return stock;} public String getDescription(){return description;}
    public static PRBuilder builder(){return new PRBuilder();}
    public static class PRBuilder { private Long id; private String name; private BigDecimal price; private int stock; private String description;
        public PRBuilder id(Long v){this.id=v;return this;} public PRBuilder name(String v){this.name=v;return this;}
        public PRBuilder price(BigDecimal v){this.price=v;return this;} public PRBuilder stock(int v){this.stock=v;return this;}
        public PRBuilder description(String v){this.description=v;return this;} public ProductResponse build(){return new ProductResponse(id,name,price,stock,description);} }
}