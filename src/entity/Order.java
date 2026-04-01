package entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int userId;
    private double totalPrice;
    private String status;
    private Timestamp createdAt;

    // Danh sách chi tiết đơn hàng (dùng để hiển thị lịch sử)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public Order() {}

    public Order(int userId, double totalPrice) {
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = "PENDING";
    }

    // ==================== GETTER & SETTER ====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails != null ? orderDetails : new ArrayList<>();
    }

    // Helper method tiện lợi
    public void addOrderDetail(OrderDetail detail) {
        this.orderDetails.add(detail);
    }
    // Thêm vào class Order.java
    private String customerName;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}