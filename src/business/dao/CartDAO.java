package business.dao;

import entity.CartItem;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    /**
     * Thêm sản phẩm vào giỏ hàng + kiểm tra stock + TRỪ STOCK ngay lập tức
     */
    public boolean addToCart(int userId, int productId, int quantity) {
        if (quantity <= 0) return false;

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Kiểm tra tồn kho
            String checkStockSql = "SELECT stock FROM products WHERE id = ? AND stock >= ?";
            try (PreparedStatement ps = conn.prepareStatement(checkStockSql)) {
                ps.setInt(1, productId);
                ps.setInt(2, quantity);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("❌ Sản phẩm không tồn tại hoặc không đủ số lượng trong kho.");
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Lấy hoặc tạo giỏ hàng
            int cartId = getOrCreateCart(conn, userId);
            if (cartId == -1) {
                conn.rollback();
                return false;
            }

            // 3. Thêm hoặc cập nhật cart_items
            String insertSql = """
                INSERT INTO cart_items (cart_id, product_id, quantity)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)
                """;

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, cartId);
                ps.setInt(2, productId);
                ps.setInt(3, quantity);
                ps.executeUpdate();
            }

            // 4. Trừ stock
            String updateStockSql = "UPDATE products SET stock = stock - ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateStockSql)) {
                ps.setInt(1, quantity);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi database khi thêm vào giỏ hàng: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int getOrCreateCart(Connection conn, int userId) throws SQLException {
        String sql = "SELECT id FROM cart WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        String insertSql = "INSERT INTO cart (user_id) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng + điều chỉnh stock
     */
    public boolean updateCartItemQuantity(int userId, int cartItemId, int newQuantity) {
        if (newQuantity <= 0) return false;

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String getOldSql = """
                SELECT ci.quantity, ci.product_id 
                FROM cart_items ci 
                JOIN cart ca ON ci.cart_id = ca.id 
                WHERE ca.user_id = ? AND ci.id = ?
                """;

            int oldQuantity = 0;
            int productId = 0;

            try (PreparedStatement ps = conn.prepareStatement(getOldSql)) {
                ps.setInt(1, userId);
                ps.setInt(2, cartItemId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        oldQuantity = rs.getInt("quantity");
                        productId = rs.getInt("product_id");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            int diff = newQuantity - oldQuantity;

            if (diff > 0) {
                String checkStockSql = "SELECT stock FROM products WHERE id = ? AND stock >= ?";
                try (PreparedStatement ps = conn.prepareStatement(checkStockSql)) {
                    ps.setInt(1, productId);
                    ps.setInt(2, diff);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            System.out.println("❌ Không đủ hàng để tăng số lượng.");
                            conn.rollback();
                            return false;
                        }
                    }
                }
            }

            String updateQtySql = "UPDATE cart_items SET quantity = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateQtySql)) {
                ps.setInt(1, newQuantity);
                ps.setInt(2, cartItemId);
                ps.executeUpdate();
            }

            if (diff != 0) {
                String updateStockSql = "UPDATE products SET stock = stock - ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateStockSql)) {
                    ps.setInt(1, diff);
                    ps.setInt(2, productId);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật số lượng giỏ hàng: " + e.getMessage());
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng + HOÀN LẠI stock
     */
    public boolean removeFromCart(int userId, int cartItemId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String getInfoSql = """
                SELECT ci.quantity, ci.product_id 
                FROM cart_items ci 
                JOIN cart ca ON ci.cart_id = ca.id 
                WHERE ca.user_id = ? AND ci.id = ?
                """;

            int quantity = 0;
            int productId = 0;

            try (PreparedStatement ps = conn.prepareStatement(getInfoSql)) {
                ps.setInt(1, userId);
                ps.setInt(2, cartItemId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        quantity = rs.getInt("quantity");
                        productId = rs.getInt("product_id");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            String deleteSql = "DELETE FROM cart_items WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, cartItemId);
                ps.executeUpdate();
            }

            String restoreStockSql = "UPDATE products SET stock = stock + ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(restoreStockSql)) {
                ps.setInt(1, quantity);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa sản phẩm khỏi giỏ hàng: " + e.getMessage());
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * XÓA TOÀN BỘ GIỎ HÀNG + HOÀN STOCK TẤT CẢ (Chỉ giữ 1 lần duy nhất)
     */
    public boolean clearAllCart(int userId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Hoàn stock cho tất cả sản phẩm
            String getItemsSql = """
                SELECT ci.product_id, ci.quantity 
                FROM cart_items ci
                JOIN cart ca ON ci.cart_id = ca.id
                WHERE ca.user_id = ?
                """;

            try (PreparedStatement ps = conn.prepareStatement(getItemsSql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int productId = rs.getInt("product_id");
                        int quantity = rs.getInt("quantity");

                        String restoreStockSql = "UPDATE products SET stock = stock + ? WHERE id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(restoreStockSql)) {
                            stmt.setInt(1, quantity);
                            stmt.setInt(2, productId);
                            stmt.executeUpdate();
                        }
                    }
                }
            }

            // Xóa tất cả cart_items
            String deleteItemsSql = """
                DELETE ci FROM cart_items ci
                JOIN cart ca ON ci.cart_id = ca.id
                WHERE ca.user_id = ?
                """;
            try (PreparedStatement ps = conn.prepareStatement(deleteItemsSql)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa toàn bộ giỏ hàng: " + e.getMessage());
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Lấy danh sách sản phẩm trong giỏ hàng
     */
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> list = new ArrayList<>();

        String sql = """
        SELECT ci.id as cart_item_id, 
               ci.product_id, 
               p.name as product_name,
               COALESCE(c.name, 'Không xác định') as category_name,
               p.storage,
               p.color,
               p.price,
               ci.quantity
        FROM cart_items ci
        JOIN cart ca ON ci.cart_id = ca.id
        JOIN products p ON ci.product_id = p.id
        LEFT JOIN categories c ON p.category_id = c.id
        WHERE ca.user_id = ?
        ORDER BY ci.id DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem(
                            rs.getInt("cart_item_id"),
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getString("category_name"),
                            rs.getString("storage"),
                            rs.getString("color"),
                            rs.getDouble("price"),
                            rs.getInt("quantity")
                    );
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy giỏ hàng: " + e.getMessage());
        }
        return list;
    }
}