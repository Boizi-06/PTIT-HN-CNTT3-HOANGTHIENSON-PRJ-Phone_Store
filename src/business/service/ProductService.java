package business.service;

import business.dao.ProductDAO;
import entity.Product;

import java.util.List;

public class ProductService {

    private final ProductDAO dao = new ProductDAO();

    public boolean add(Product p) {
        if (p.getName() == null || p.getName().isEmpty()) {
            System.out.println("❌ Tên sản phẩm không được rỗng");
            return false;
        }
        if (p.getPrice() <= 0) {
            System.out.println("❌ Giá sản phẩm phải lớn hơn 0");
            return false;
        }
        return dao.add(p);
    }

    public List<Product> getAll() {
        return dao.getAll();
    }

    /**
     * Lấy danh sách sản phẩm còn hàng (stock > 0)
     */
    public List<Product> getAvailableProducts() {
        return dao.getAvailableProducts();   // Giả sử bạn đã có phương thức này trong ProductDAO
    }

    public List<Product> search(String keyword) {
        return dao.findByName(keyword);
    }

    public boolean update(Product p) {
        return dao.update(p);
    }

    public boolean delete(int id) {
        return dao.delete(id);
    }

    public List<Product> getByCategory(int categoryId) {
        return dao.getByCategory(categoryId);
    }
}