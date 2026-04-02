package business.service;

import business.dao.UserDAO;
import entity.User;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public boolean register(User user) {
        if (userDAO.checkEmailExists(user.getEmail())) {
            return false;
        }
        return userDAO.register(user);
    }

    /**
     * Login với kiểm tra email và password PHẢI GIỐNG HỆT (case-sensitive)
     */
    public User login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        // Trim email để tránh lỗi khoảng trắng thừa
        email = email.trim();

        // Gọi DAO với kiểm tra nghiêm ngặt
        return userDAO.login(email, password);   // password KHÔNG trim để giữ nguyên ký tự
    }
}