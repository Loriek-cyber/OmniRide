package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int userId;
    private User user;
    private String status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String shippingCity;
    private String shippingPostalCode;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime orderDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;
    private List<OrderItem> orderItems;
    
    // Order status constants
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_SHIPPED = "SHIPPED";
    public static final String STATUS_DELIVERED = "DELIVERED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    
    // Payment status constants
    public static final String PAYMENT_PENDING = "PENDING";
    public static final String PAYMENT_COMPLETED = "COMPLETED";
    public static final String PAYMENT_FAILED = "FAILED";
    
    // Constructors
    public Order() {
        this.orderItems = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
        this.status = STATUS_PENDING;
        this.paymentStatus = PAYMENT_PENDING;
    }
    
    public Order(int userId, String shippingAddress, String shippingCity, String shippingPostalCode) {
        this();
        this.userId = userId;
        this.shippingAddress = shippingAddress;
        this.shippingCity = shippingCity;
        this.shippingPostalCode = shippingPostalCode;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getShippingCity() {
        return shippingCity;
    }
    
    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }
    
    public String getShippingPostalCode() {
        return shippingPostalCode;
    }
    
    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public LocalDateTime getShippedDate() {
        return shippedDate;
    }
    
    public void setShippedDate(LocalDateTime shippedDate) {
        this.shippedDate = shippedDate;
    }
    
    public LocalDateTime getDeliveredDate() {
        return deliveredDate;
    }
    
    public void setDeliveredDate(LocalDateTime deliveredDate) {
        this.deliveredDate = deliveredDate;
    }
    
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    
    // Utility methods
    public String getFullShippingAddress() {
        return shippingAddress + ", " + shippingCity + " " + shippingPostalCode;
    }
    
    public String getFormattedTotalAmount() {
        return "€" + totalAmount.toString();
    }
    
    public void addOrderItem(OrderItem item) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        orderItems.add(item);
    }
    
    public void calculateTotalAmount() {
        if (orderItems != null && !orderItems.isEmpty()) {
            totalAmount = orderItems.stream()
                    .map(OrderItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            totalAmount = BigDecimal.ZERO;
        }
    }
    
    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }
    
    public boolean isConfirmed() {
        return STATUS_CONFIRMED.equals(status);
    }
    
    public boolean isShipped() {
        return STATUS_SHIPPED.equals(status);
    }
    
    public boolean isDelivered() {
        return STATUS_DELIVERED.equals(status);
    }
    
    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(status);
    }
    
    public boolean isPaymentCompleted() {
        return PAYMENT_COMPLETED.equals(paymentStatus);
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", orderDate=" + orderDate +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}