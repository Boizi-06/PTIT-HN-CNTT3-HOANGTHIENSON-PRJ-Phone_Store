package business.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class OrderDetailDAO {

    public void addDetail(Connection conn, int orderId, int productId, int quantity, double price) throws Exception {
        String sql = "INSERT INTO order_details(order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, orderId);
        ps.setInt(2, productId);
        ps.setInt(3, quantity);
        ps.setDouble(4, price);

        ps.executeUpdate();
    }
}