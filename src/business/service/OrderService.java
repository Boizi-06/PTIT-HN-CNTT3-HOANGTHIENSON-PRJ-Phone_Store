package business.service;

import business.dao.OrderDAO;
import business.dao.OrderDetailDAO;
import entity.CartItem;
import entity.Order;
import entity.OrderDetail;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO detailDAO = new OrderDetailDAO();

    // ==================== TẠO ĐƠN HÀNG ====================
    public boolean createOrder(int userId, List<CartItem> items) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            double total = items.stream().mapToDouble(CartItem::getSubtotal).sum();

            int orderId = orderDAO.createOrder(conn, userId, total);
            if (orderId == -1) throw new Exception("Tạo order thất bại");

            for (CartItem item : items) {
                detailDAO.addDetail(conn, orderId, item.getProductId(),
                        item.getQuantity(), item.getPrice());
            }

            String deleteSql = "DELETE FROM cart_items WHERE id = ?";
            for (CartItem item : items) {
                try (var ps = conn.prepareStatement(deleteSql)) {
                    ps.setInt(1, item.getCartItemId());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            rollback(conn);
            System.out.println("❌ Lỗi tạo đơn hàng: " + e.getMessage());
            return false;
        } finally {
            closeConnection(conn);
        }
    }

    // ==================== CẬP NHẬT TRẠNG THÁI ====================
    public boolean updateOrderStatus(int orderId, String newStatus) {
        if (newStatus == null) return false;
        newStatus = newStatus.toUpperCase().trim();

        String currentStatus = orderDAO.getCurrentStatus(orderId);
        if (currentStatus == null) {
            System.out.println("❌ Không tìm thấy đơn hàng #" + orderId);
            return false;
        }

        if (!isValidTransition(currentStatus, newStatus)) {
            System.out.println("❌ Không được phép chuyển từ " + currentStatus + " sang " + newStatus);
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            boolean success = orderDAO.updateOrderStatus(conn, orderId, newStatus);

            if (success && "CANCELLED".equals(newStatus)) {
                orderDAO.restoreStockForOrder(conn, orderId);
            }

            if (success) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (Exception e) {
            rollback(conn);
            System.out.println("❌ Lỗi cập nhật trạng thái: " + e.getMessage());
            return false;
        } finally {
            closeConnection(conn);
        }
    }

    private boolean isValidTransition(String current, String next) {
        current = current.toUpperCase().trim();
        next = next.toUpperCase().trim();
        return switch (current) {
            case "PENDING" -> next.equals("SHIPPING") || next.equals("CANCELLED");
            case "SHIPPING" -> next.equals("DELIVERED");
            case "DELIVERED", "CANCELLED" -> false;
            default -> false;
        };
    }

    // ==================== LẤY DỮ LIỆU ====================
    public List<Order> getOrderHistory(int userId) {
        List<Order> orders = orderDAO.getOrdersByUserId(userId);
        for (Order order : orders) {
            order.setOrderDetails(orderDAO.getOrderDetails(order.getId()));
        }
        return orders;
    }

    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    /**
     * LẤY ĐẦY ĐỦ THÔNG TIN ĐƠN HÀNG (customerName + orderDetails)
     * Dùng cho chức năng cập nhật trạng thái
     */
    public Order getFullOrderById(int orderId) {
        List<Order> allOrders = orderDAO.getAllOrders();
        Order order = allOrders.stream()
                .filter(o -> o.getId() == orderId)
                .findFirst()
                .orElse(null);

        if (order != null) {
            List<OrderDetail> details = orderDAO.getOrderDetails(orderId);
            order.setOrderDetails(details);
        }
        return order;
    }

    // ==================== HỖ TRỢ TRANSACTION ====================
    private void rollback(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ignored) {}
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }
    // Load chi tiết sản phẩm cho danh sách đơn hàng (dùng cho viewAllOrders)
    public List<Order> getAllOrdersWithDetails() {
        List<Order> orders = orderDAO.getAllOrders();
        for (Order order : orders) {
            List<OrderDetail> details = orderDAO.getOrderDetails(order.getId());
            order.setOrderDetails(details);
        }
        return orders;
    }
}