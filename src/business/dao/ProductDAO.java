package business.dao;

import entity.Product;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // ===== ADD =====
    public boolean add(Product p) {
        String sql = "INSERT INTO products(name, category_id, storage, color, price, stock, description) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getStorage());
            ps.setString(4, p.getColor());
            ps.setDouble(5, p.getPrice());
            ps.setInt(6, p.getStock());
            ps.setString(7, p.getDescription());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== GET ALL =====
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== FIND BY NAME =====
    public List<Product> findByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== UPDATE =====
    public boolean update(Product p) {
        String sql = "UPDATE products SET name=?, category_id=?, storage=?, color=?, price=?, stock=?, description=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getStorage());
            ps.setString(4, p.getColor());
            ps.setDouble(5, p.getPrice());
            ps.setInt(6, p.getStock());
            ps.setString(7, p.getDescription());
            ps.setInt(8, p.getId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== DELETE =====
    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== FILTER BY CATEGORY =====
    public List<Product> getByCategory(int categoryId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== MAP =====
    private Product map(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("category_id"),
                rs.getString("storage"),
                rs.getString("color"),
                rs.getDouble("price"),
                rs.getInt("stock"),
                rs.getString("description")
        );
    }
    public int countByCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM products WHERE category_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    // ================= SỬA LẠI METHOD NÀY =================
    public List<Product> getAvailableProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name " +
                "FROM products p LEFT JOIN categories c ON p.category_id = c.id " +
                "WHERE p.stock > 0 ORDER BY p.id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCategoryId(rs.getInt("category_id"));
                p.setStorage(rs.getString("storage"));
                p.setColor(rs.getString("color"));
                p.setPrice(rs.getDouble("price"));
                p.setStock(rs.getInt("stock"));
                p.setDescription(rs.getString("description"));
                list.add(p);
            }
        } catch (Exception e) {   // ← Sửa thành Exception để bao quát hết
            System.err.println("❌ Lỗi khi lấy danh sách sản phẩm còn hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}