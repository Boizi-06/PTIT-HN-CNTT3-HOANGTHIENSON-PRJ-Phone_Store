package presentation;

import utils.DBConnection;
import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Kết nối thành công!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}