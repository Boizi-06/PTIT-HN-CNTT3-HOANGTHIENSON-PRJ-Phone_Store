package business.service;

import business.dao.ProductDAO;
import entity.Product;

import java.util.List;

public class ProductService {
    private ProductDAO dao = new ProductDAO();

    public boolean add(Product p) {
        if (p.getName() == null || p.getName().isEmpty()) {
            System.out.println("❌ Tên không được rỗng");
            return false;
        }
        if (p.getPrice() <= 0) {
            System.out.println("❌ Giá phải > 0");
            return false;
        }
        return dao.add(p);
    }

    public List<Product> getAll() {
        return dao.getAll();
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