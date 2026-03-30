package business.service;

import business.dao.CategoryDAO;
import business.dao.ProductDAO;
import entity.Category;

import java.util.List;

public class CategoryService {
    private CategoryDAO dao = new CategoryDAO();
    private ProductDAO productDAO = new ProductDAO();

    // ===== ADD =====
    public boolean add(Category c) {
        if (c.getName() == null || c.getName().trim().isEmpty()) {
            System.out.println("❌ Tên category không được rỗng");
            return false;
        }

        if (dao.existsByName(c.getName())) {
            System.out.println("❌ Category đã tồn tại!");
            return false;
        }

        return dao.addCategory(c);
    }

    // ===== GET ALL =====
    public List<Category> getAll() {
        return dao.getAll();
    }

    // ===== UPDATE =====
    public boolean update(int id, String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("❌ Tên không được rỗng");
            return false;
        }

        Category old = dao.findById(id);
        if (old == null) {
            System.out.println("❌ Category không tồn tại");
            return false;
        }

        return dao.updateCategory(id, name);
    }

    // ===== DELETE =====
    public boolean delete(int id) {

        Category c = dao.findById(id);
        if (c == null) {
            System.out.println("❌ Category không tồn tại");
            return false;
        }

        // 🔥 tối ưu: dùng COUNT thay vì load list
        if (productDAO.countByCategory(id) > 0) {
            System.out.println("❌ Category đang có sản phẩm, không thể xóa!");
            return false;
        }

        return dao.deleteCategory(id);
    }

    // ===== FIND =====
    public Category findById(int id) {
        return dao.findById(id);
    }
}