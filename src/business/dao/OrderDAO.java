package business.dao;

import entity.Order;
import entity.OrderDetail;
import entity.TopSellingProduct;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // ==================== TẠO ĐƠN HÀNG ====================
    public int createOrder(Connection conn, int userId, double totalPrice) throws SQLException {
        String sql = "INSERT INTO orders(user_id, total_price, status) VALUES (?, ?, 'PENDING')";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setDouble(2, totalPrice);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    // ==================== CẬP NHẬT TRẠNG THÁI (Hỗ trợ transaction) ====================
    public boolean updateOrderStatus(Connection conn, int orderId, String newStatus) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    // ==================== LẤY TRẠNG THÁI HIỆN TẠI ====================
    public String getCurrentStatus(int orderId) {
        String sql = "SELECT status FROM orders WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy trạng thái: " + e.getMessage());
        }
        return null;
    }

    // ==================== HOÀN STOCK KHI HỦY ĐƠN ====================
    public void restoreStockForOrder(Connection conn, int orderId) throws SQLException {
        String detailSql = """
            SELECT product_id, quantity 
            FROM order_details 
            WHERE order_id = ?
            """;

        try (PreparedStatement psDetail = conn.prepareStatement(detailSql)) {
            psDetail.setInt(1, orderId);

            try (ResultSet rs = psDetail.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    int quantity = rs.getInt("quantity");

                    String updateStockSql = "UPDATE products SET stock = stock + ? WHERE id = ?";
                    try (PreparedStatement psStock = conn.prepareStatement(updateStockSql)) {
                        psStock.setInt(1, quantity);
                        psStock.setInt(2, productId);
                        psStock.executeUpdate();
                    }
                }
            }
        }
    }

    // ==================== CÁC PHƯƠNG THỨC KHÁC (giữ nguyên) ====================
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT id, user_id, total_price, status, created_at 
            FROM orders 
            WHERE user_id = ? 
            ORDER BY created_at DESC
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setTotalPrice(rs.getDouble("total_price"));
                    order.setStatus(rs.getString("status"));
                    order.setCreatedAt(rs.getTimestamp("created_at"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách đơn hàng: " + e.getMessage());
        }
        return orders;
    }

    public List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql = """
            SELECT od.id, od.order_id, od.product_id, od.quantity, od.price,
                   p.name AS product_name
            FROM order_details od
            JOIN products p ON od.product_id = p.id
            WHERE od.order_id = ?
            ORDER BY od.id
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setId(rs.getInt("id"));
                    detail.setOrderId(rs.getInt("order_id"));
                    detail.setProductId(rs.getInt("product_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setPrice(rs.getDouble("price"));
                    detail.setProductName(rs.getString("product_name"));
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy chi tiết đơn hàng: " + e.getMessage());
        }
        return details;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT o.id, o.user_id, o.total_price, o.status, o.created_at,
                   u.name AS customer_name
            FROM orders o
            JOIN users u ON o.user_id = u.id
            ORDER BY o.created_at DESC
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setStatus(rs.getString("status"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                order.setCustomerName(rs.getString("customer_name"));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy tất cả đơn hàng: " + e.getMessage());
        }
        return orders;
    }
    // ==================== THỐNG KÊ ====================

    /**
     * Tổng doanh thu từ các đơn hàng đã giao thành công
     */
    public double getTotalRevenue() {
        String sql = """
            SELECT COALESCE(SUM(total_price), 0) AS total_revenue 
            FROM orders 
            WHERE status = 'DELIVERED'
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tính tổng doanh thu: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Lấy Top N sản phẩm bán chạy nhất theo số lượng
     */
    public List<TopSellingProduct> getTopSellingProducts(int limit) {
        List<TopSellingProduct> topList = new ArrayList<>();
        String sql = """
            SELECT 
                p.id AS product_id,
                p.name AS product_name,
                SUM(od.quantity) AS total_sold,
                SUM(od.quantity * od.price) AS revenue
            FROM order_details od
            JOIN products p ON od.product_id = p.id
            JOIN orders o ON od.order_id = o.id
            WHERE o.status = 'DELIVERED'
            GROUP BY p.id, p.name
            ORDER BY total_sold DESC
            LIMIT ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TopSellingProduct top = new TopSellingProduct(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getLong("total_sold"),
                            rs.getDouble("revenue")
                    );
                    topList.add(top);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy top sản phẩm bán chạy: " + e.getMessage());
        }
        return topList;
    }

    /**
     * Số lượng đã bán của một sản phẩm
     */
    public long getQuantitySold(int productId) {
        String sql = """
            SELECT COALESCE(SUM(od.quantity), 0) AS total_sold
            FROM order_details od
            JOIN orders o ON od.order_id = o.id
            WHERE od.product_id = ? AND o.status = 'DELIVERED'
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total_sold");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy số lượng bán: " + e.getMessage());
        }
        return 0;
    }
}