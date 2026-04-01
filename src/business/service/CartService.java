//package business.service;
//
//import business.dao.CartDAO;
//import entity.CartItem;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CartService {
//
//    private final CartDAO cartDAO = new CartDAO();
//
//    /**
//     * Thêm sản phẩm vào giỏ hàng với kiểm tra stock
//     */
//    public boolean addToCart(int userId, int productId, int quantity) {
//        try {
//            return cartDAO.addToCart(userId, productId, quantity);
//        } catch (Exception e) {
//            System.err.println("Lỗi service thêm vào giỏ hàng: " + e.getMessage());
//            return false;
//        }
//    }
//    public List<CartItem> getCartItems(int userId) {
//        try {
//            return cartDAO.getCartItems(userId);
//        } catch (Exception e) {
//            System.err.println("Lỗi service lấy giỏ hàng: " + e.getMessage());
//            return new ArrayList<>();
//        }
//    }
//}










package business.service;

import business.dao.CartDAO;
import entity.CartItem;
import java.util.ArrayList;
import java.util.List;

public class CartService {

    private final CartDAO cartDAO = new CartDAO();

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public boolean addToCart(int userId, int productId, int quantity) {
        try {
            return cartDAO.addToCart(userId, productId, quantity);
        } catch (Exception e) {
            System.err.println("Lỗi service thêm vào giỏ hàng: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    public boolean updateCartItemQuantity(int userId, int cartItemId, int newQuantity) {
        try {
            return cartDAO.updateCartItemQuantity(userId, cartItemId, newQuantity);
        } catch (Exception e) {
            System.err.println("Lỗi service cập nhật số lượng: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    public boolean removeFromCart(int userId, int cartItemId) {
        try {
            return cartDAO.removeFromCart(userId, cartItemId);
        } catch (Exception e) {
            System.err.println("Lỗi service xóa khỏi giỏ hàng: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách sản phẩm trong giỏ hàng
     */
    public List<CartItem> getCartItems(int userId) {
        try {
            return cartDAO.getCartItems(userId);
        } catch (Exception e) {
            System.err.println("Lỗi service lấy giỏ hàng: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    /**
     * Xóa toàn bộ giỏ hàng + hoàn lại stock
     */
    public boolean clearAllCart(int userId) {
        try {
            return cartDAO.clearAllCart(userId);
        } catch (Exception e) {
            System.err.println("Lỗi service xóa toàn bộ giỏ hàng: " + e.getMessage());
            return false;
        }
    }
}
