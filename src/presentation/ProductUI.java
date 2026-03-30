package presentation;

import entity.Product;
import entity.Category;
import business.service.ProductService;
import business.service.CategoryService;
import utils.FormatUtil;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ProductUI {
    private static final ProductService service = new ProductService();
    private static final CategoryService categoryService = new CategoryService();
    private static final Scanner sc = new Scanner(System.in);

    private static final int PAGE_SIZE = 10;   // ←←← THÊM DÒNG NÀY (quan trọng nhất)

    // Cache danh mục
    private static Map<Integer, String> categoryMap = null;

    public static void menu() {
        while (true) {
            try {
                System.out.println("\n===== PRODUCT MANAGEMENT =====");
                System.out.println("1. Thêm sản phẩm");
                System.out.println("2. Hiển thị tất cả");
                System.out.println("3. Tìm theo tên");
                System.out.println("4. Tìm theo danh mục");
                System.out.println("5. Cập nhật");
                System.out.println("6. Xóa");
                System.out.println("0. Thoát");

                int choice = Integer.parseInt(sc.nextLine().trim());

                switch (choice) {
                    case 1 -> add();
                    case 2 -> show(service.getAll());
                    case 3 -> search();
                    case 4 -> searchByCategory();
                    case 5 -> update();
                    case 6 -> delete();
                    case 0 -> {
                        System.out.println("👋 Thoát chương trình.");
                        return;
                    }
                    default -> System.out.println("❌ Chọn sai! Vui lòng chọn lại.");
                }
            } catch (Exception e) {
                System.out.println("❌ Lỗi nhập liệu! Vui lòng nhập số từ 0 đến 6.");
            }
        }
    }

    // ================= LOAD CATEGORY MAP =================
    private static void loadCategoryMap() {
        if (categoryMap == null) {
            List<Category> categories = categoryService.getAll();
            categoryMap = categories.stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
        }
    }

    private static String getCategoryName(int categoryId) {
        loadCategoryMap();
        return categoryMap.getOrDefault(categoryId, "Không xác định");
    }

    // ================= PHÂN TRANG =================
    private static void showPaginated(List<Product> allProducts, String title) {
        if (allProducts.isEmpty()) {
            System.out.println("❌ Không có dữ liệu.");
            return;
        }

        int totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);
        int currentPage = 1;

        while (true) {
            int fromIndex = (currentPage - 1) * PAGE_SIZE;
            int toIndex = Math.min(fromIndex + PAGE_SIZE, totalProducts);
            List<Product> currentPageList = allProducts.subList(fromIndex, toIndex);

            System.out.println("\n===== " + title + " =====");
            System.out.printf("Trang %d / %d  |  Tổng sản phẩm: %d%n", currentPage, totalPages, totalProducts);
            System.out.println("=".repeat(100));

            printTableHeader();
            for (Product p : currentPageList) {
                printTableRow(p);
            }
            printTableSeparator();

            System.out.println("\n[1] Trang trước    [2] Trang sau    [3] Chọn trang    [0] Thoát");
            System.out.print("Chọn hành động: ");

            try {
                int action = Integer.parseInt(sc.nextLine().trim());

                switch (action) {
                    case 1 -> {
                        if (currentPage > 1) currentPage--;
                        else System.out.println("❌ Bạn đang ở trang đầu tiên!");
                    }
                    case 2 -> {
                        if (currentPage < totalPages) currentPage++;
                        else System.out.println("❌ Bạn đang ở trang cuối cùng!");
                    }
                    case 3 -> {
                        System.out.print("Nhập số trang (1-" + totalPages + "): ");
                        try {
                            int chosen = Integer.parseInt(sc.nextLine().trim());
                            if (chosen >= 1 && chosen <= totalPages) {
                                currentPage = chosen;
                            } else {
                                System.out.println("❌ Số trang không hợp lệ!");
                            }
                        } catch (Exception e) {
                            System.out.println("❌ Vui lòng nhập số!");
                        }
                    }
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("❌ Lựa chọn không hợp lệ!");
                }
            } catch (Exception e) {
                System.out.println("❌ Vui lòng nhập số!");
            }
        }
    }

    // ================= SHOW =================
    private static void show(List<Product> list) {
        showPaginated(list, "DANH SÁCH SẢN PHẨM");
    }

    // ================= IN BẢNG =================
    private static void printTableHeader() {
        printTableSeparator();
        System.out.printf("| %-5s | %-25s | %-18s | %-12s | %-17s | %17s | %8s | %-25s |%n",
                "ID", "TÊN SẢN PHẨM", "DANH MỤC", "STORAGE", "COLOR", "GIÁ (VND)", "STOCK", "MÔ TẢ");
        printTableSeparator();
    }

    private static void printTableRow(Product p) {
        String categoryName = getCategoryName(p.getCategoryId());
        String desc = p.getDescription() != null ? p.getDescription() : "";

        System.out.printf("| %5d | %-25s | %-18s | %-12s | %-17s | %17s | %8d | %-25s |%n",
                p.getId(),
                truncate(p.getName(), 25),
                truncate(categoryName, 18),
                truncate(p.getStorage(), 12),
                truncate(p.getColor(), 17),
                FormatUtil.formatVND(p.getPrice()),
                p.getStock(),
                truncate(desc, 25));
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    private static void printTableSeparator() {
        System.out.println("+-------+---------------------------+--------------------+--------------+-------------------+-------------------+----------+---------------------------+");
    }

    // ================= ADD =================
    private static void add() {
        try {
            Product p = new Product();

            System.out.print("Tên sản phẩm: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("❌ Tên không được để trống!");
                return;
            }
            p.setName(name);

            p.setCategoryId(chooseCategory());

            System.out.print("Storage: ");
            p.setStorage(sc.nextLine().trim());

            System.out.print("Color: ");
            p.setColor(sc.nextLine().trim());

            System.out.print("Giá (VND): ");
            double price = Double.parseDouble(sc.nextLine().trim());
            if (price <= 0) {
                System.out.println("❌ Giá phải lớn hơn 0!");
                return;
            }
            p.setPrice(price);

            System.out.print("Stock: ");
            int stock = Integer.parseInt(sc.nextLine().trim());
            if (stock < 0) {
                System.out.println("❌ Stock không được âm!");
                return;
            }
            p.setStock(stock);

            System.out.print("Mô tả: ");
            p.setDescription(sc.nextLine().trim());

            if (service.add(p)) {
                System.out.println("✅ Thêm sản phẩm thành công!");
                categoryMap = null;
            } else {
                System.out.println("❌ Thêm sản phẩm thất bại!");
            }

        } catch (Exception e) {
            System.out.println("❌ Lỗi nhập dữ liệu! Vui lòng kiểm tra lại thông tin.");
        }
    }

    // ================= SEARCH =================
    private static void search() {
        try {
            System.out.print("Nhập tên sản phẩm cần tìm: ");
            String keyword = sc.nextLine().trim();
            List<Product> result = service.search(keyword);
            showPaginated(result, "KẾT QUẢ TÌM KIẾM: " + keyword.toUpperCase());
        } catch (Exception e) {
            System.out.println("❌ Có lỗi xảy ra khi tìm kiếm.");
        }
    }

    private static void searchByCategory() {
        try {
            int categoryId = chooseCategory();
            List<Product> result = service.getByCategory(categoryId);
            String categoryName = getCategoryName(categoryId);
            showPaginated(result, "SẢN PHẨM THUỘC DANH MỤC: " + categoryName);
        } catch (Exception e) {
            System.out.println("❌ Có lỗi xảy ra khi tìm theo danh mục.");
        }
    }

    // ================= UPDATE =================
    private static void update() {
        try {
            System.out.print("Nhập ID sản phẩm cần cập nhật: ");
            int id = Integer.parseInt(sc.nextLine().trim());

            Product oldProduct = service.getAll().stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (oldProduct == null) {
                System.out.println("❌ Không tìm thấy sản phẩm với ID = " + id);
                return;
            }

            System.out.println("\n===== THÔNG TIN CŨ =====");
            show(List.of(oldProduct));

            Product newProduct = new Product();
            newProduct.setId(id);

            System.out.print("\nTên mới (Enter giữ nguyên): ");
            String name = sc.nextLine().trim();
            newProduct.setName(name.isEmpty() ? oldProduct.getName() : name);

            System.out.print("Category ID mới (Enter giữ nguyên): ");
            String catStr = sc.nextLine().trim();
            newProduct.setCategoryId(catStr.isEmpty() ? oldProduct.getCategoryId() : Integer.parseInt(catStr));

            System.out.print("Storage mới (Enter giữ nguyên): ");
            String storage = sc.nextLine().trim();
            newProduct.setStorage(storage.isEmpty() ? oldProduct.getStorage() : storage);

            System.out.print("Color mới (Enter giữ nguyên): ");
            String color = sc.nextLine().trim();
            newProduct.setColor(color.isEmpty() ? oldProduct.getColor() : color);

            System.out.print("Giá mới (Enter giữ nguyên): ");
            String priceStr = sc.nextLine().trim();
            newProduct.setPrice(priceStr.isEmpty() ? oldProduct.getPrice() : Double.parseDouble(priceStr));

            System.out.print("Stock mới (Enter giữ nguyên): ");
            String stockStr = sc.nextLine().trim();
            newProduct.setStock(stockStr.isEmpty() ? oldProduct.getStock() : Integer.parseInt(stockStr));

            System.out.print("Mô tả mới (Enter giữ nguyên): ");
            String desc = sc.nextLine().trim();
            newProduct.setDescription(desc.isEmpty() ? oldProduct.getDescription() : desc);

            System.out.println("\n===== THÔNG TIN SẼ CẬP NHẬT =====");
            show(List.of(newProduct));

            int confirm = getConfirmChoice("Bạn có chắc chắn muốn cập nhật không?");

            if (confirm == 1) {
                if (service.update(newProduct)) {
                    System.out.println("✅ Cập nhật thành công!");
                    categoryMap = null;
                } else {
                    System.out.println("❌ Cập nhật thất bại!");
                }
            } else {
                System.out.println("ℹ️  Đã hủy cập nhật.");
            }

        } catch (Exception e) {
            System.out.println("❌ Lỗi nhập liệu! Vui lòng kiểm tra lại dữ liệu.");
        }
    }

    // ================= DELETE =================
    private static void delete() {
        try {
            System.out.print("Nhập ID sản phẩm cần xóa: ");
            int id = Integer.parseInt(sc.nextLine().trim());

            Product productToDelete = service.getAll().stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (productToDelete == null) {
                System.out.println("❌ Không tìm thấy sản phẩm với ID = " + id);
                return;
            }

            System.out.println("\n===== THÔNG TIN SẢN PHẨM SẼ XÓA =====");
            show(List.of(productToDelete));

            int confirm = getConfirmChoice("Bạn có chắc chắn muốn xóa sản phẩm này không?");

            if (confirm == 1) {
                if (service.delete(id)) {
                    System.out.println("✅ Xóa thành công!");
                } else {
                    System.out.println("❌ Xóa thất bại!");
                }
            } else {
                System.out.println("ℹ️  Đã hủy thao tác xóa.");
            }

        } catch (Exception e) {
            System.out.println("❌ Lỗi nhập liệu! Vui lòng nhập ID là số.");
        }
    }

    // ================= CONFIRM =================
    private static int getConfirmChoice(String message) {
        while (true) {
            try {
                System.out.print("\n" + message + " (1: Có - 0: Không): ");
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice == 0 || choice == 1) return choice;
                System.out.println("❌ Chỉ được nhập 0 hoặc 1!");
            } catch (Exception e) {
                System.out.println("❌ Vui lòng nhập số 0 hoặc 1!");
            }
        }
    }

    // ================= CHOOSE CATEGORY =================
    private static int chooseCategory() {
        List<Category> list = categoryService.getAll();

        while (true) {
            try {
                System.out.println("\n===== DANH MỤC =====");
                for (Category c : list) {
                    System.out.println(c.getId() + ". " + c.getName());
                }

                System.out.print("Chọn category ID: ");
                int categoryId = Integer.parseInt(sc.nextLine().trim());

                if (list.stream().anyMatch(c -> c.getId() == categoryId)) {
                    return categoryId;
                } else {
                    System.out.println("❌ Category không tồn tại! Vui lòng chọn lại.");
                }
            } catch (Exception e) {
                System.out.println("❌ Vui lòng nhập số hợp lệ!");
            }
        }
    }
}