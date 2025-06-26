package model;

import java.math.BigDecimal;

public class CartItem {
    private int productId;
    private Product product;
    private int quantity;
    private BigDecimal unitPrice;
    
    // Constructors
    public CartItem() {}
    
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.productId = product.getId();
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }
    
    public CartItem(int productId, int quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    // Getters and Setters
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productId = product.getId();
            this.unitPrice = product.getPrice();
        }
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantità non può essere negativa");
        }
        this.quantity = quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    // Utility methods
    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    public String getFormattedTotalPrice() {
        return "€" + getTotalPrice().toString();
    }
    
    public String getFormattedUnitPrice() {
        return "€" + unitPrice.toString();
    }
    
    public void increaseQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("L'incremento non può essere negativo");
        }
        this.quantity += amount;
    }
    
    public void decreaseQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Il decremento non può essere negativo");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException("Quantità insufficiente nel carrello");
        }
        this.quantity -= amount;
    }
    
    public boolean isValid() {
        return productId > 0 && quantity > 0 && unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) > 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        CartItem cartItem = (CartItem) obj;
        return productId == cartItem.productId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(productId);
    }
    
    @Override
    public String toString() {
        return "CartItem{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}