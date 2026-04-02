package business.dao;

import entity.User;
import utils.DBConnection;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {
    public boolean checkEmailExists(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // REGISTER
    public boolean register(User user) {
        String sql = "INSERT INTO users(name, email, password, phone, address, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());
            ps.setString(6, user.getRole());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // LOGIN
    // ==================== LOGIN ====================
    // ==================== LOGIN (Email và Password phải giống hệt) ====================
    // ==================== LOGIN - Phân biệt hoa thường ====================
    public User login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        String sql = "SELECT id, name, email, password, phone, address, role, created_at " +
                "FROM users " +
                "WHERE email = ? AND password = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim());
            ps.setString(2, password);        // Không trim password để giữ nguyên ký tự

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setRole(rs.getString("role"));



                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi đăng nhập: " + e.getMessage());
        }
        return null;
    }
}