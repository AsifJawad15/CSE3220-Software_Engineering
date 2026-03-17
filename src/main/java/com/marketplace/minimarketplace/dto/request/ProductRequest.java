package com.marketplace.minimarketplace.dto.request;
import jakarta.validation.constraints.*; import java.math.BigDecimal;
public class ProductRequest {
    @NotBlank(message = "Product name is required") private String name;
    @NotNull(message = "Price is required") @Positive(message = "Price must be positive") private BigDecimal price;
    @Min(value = 0, message = "Stock cannot be negative") private int stock;
    private String description;
    public ProductRequest() {} public ProductRequest(String name, BigDecimal price, int stock, String description) { this.name=name; this.price=price; this.stock=stock; this.description=description; }
    public String getName() { return name; } public void setName(String v) { this.name=v; }
    public BigDecimal getPrice() { return price; } public void setPrice(BigDecimal v) { this.price=v; }
    public int getStock() { return stock; } public void setStock(int v) { this.stock=v; }
    public String getDescription() { return description; } public void setDescription(String v) { this.description=v; }
    public static PRBuilder builder() { return new PRBuilder(); }
    public static class PRBuilder { private String name; private BigDecimal price; private int stock; private String description;
        public PRBuilder name(String v){this.name=v;return this;} public PRBuilder price(BigDecimal v){this.price=v;return this;}
        public PRBuilder stock(int v){this.stock=v;return this;} public PRBuilder description(String v){this.description=v;return this;}
        public ProductRequest build(){return new ProductRequest(name,price,stock,description);} }
}