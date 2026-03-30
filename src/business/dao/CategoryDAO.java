package business.dao;

import entity.Category;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    // CREATE
    public boolean addCategory(Category c) {
        String sql = "INSERT INTO categories(name) VALUES (?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getName());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // READ
    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE
    public boolean updateCategory(int id, String name) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE
    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Category findById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public boolean existsByName(String name) {
        String sql = "SELECT * FROM categories WHERE name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}