package business.service;

import business.dao.UserDAO;
import entity.User;

public class AuthService {
    private UserDAO userDAO = new UserDAO();

    public boolean register(User user) {
        if (userDAO.checkEmailExists(user.getEmail())) {
            return false;
        }
        return userDAO.register(user);
    }

    public User login(String email, String password) {
        return userDAO.login(email, password);
    }
}