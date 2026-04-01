package presentation;

import entity.Product;
import entity.Category;
import business.service.ProductService;
import business.service.CategoryService;
import utils.InputUtil;
import utils.FormatUtil;

import java.util.List;
import java.util.Scanner;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ProductCustomerUI {

    private static final ProductService productService = new ProductService();
    private static final CategoryService categoryService = new CategoryService();
    private static final Scanner sc = new Scanner(System.in);

    private static final int PAGE_SIZE = 5;

    // ================= XEM SẢN PHẨM CÒN HÀNG (Chức năng 1) =================
    public static void viewAvailableProducts() {
        List<Product> allProducts = productService.getAvailableProducts();
        if (allProducts.isEmpty()) {
            System.out.println("❌ Hiện tại không có sản phẩm nào còn hàng.");
            return;
        }
        showProductsWithFeatures(allProducts, "SẢN PHẨM CÒN HÀNG");
    }

    // ================= HÀM CHUNG: Tìm kiếm + Phân trang + Sắp xếp =================
    public static void showProductsWithFeatures(List<Product> products, String title) {
        while (true) {
            System.out.println("\n===== " + title + " =====");
            System.out.println("1. Xem tất cả (phân trang + sắp xếp)");
            System.out.println("2. Tìm kiếm theo tên");
            System.out.println("3. Tìm kiếm theo danh mục");
            System.out.println("0. Quay lại");

            int choice = InputUtil.inputInt(sc, "Chọn: ", 0, 3);

            switch (choice) {
                case 1 -> showPaginatedAndSortable(products, title);
                case 2 -> searchByName(products);
                case 3 -> searchByCategory(products);
                case 0 -> { return; }
            }
        }
    }

    private static void searchByName(List<Product> allProducts) {
        String keyword = InputUtil.inputString(sc, "Nhập tên sản phẩm cần tìm: ");
        List<Product> result = allProducts.stream()
                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        showPaginatedAndSortable(result, "KẾT QUẢ TÌM KIẾM: " + keyword);
    }

    private static void searchByCategory(List<Product> allProducts) {
        List<Category> categories = categoryService.getAll();
        System.out.println("\n===== DANH SÁCH DANH MỤC =====");
        for (Category c : categories) {
            System.out.println(c.getId() + ". " + c.getName());
        }

        int catId = InputUtil.inputInt(sc, "Chọn ID danh mục: ", 1, 1000);

        List<Product> result = allProducts.stream()
                .filter(p -> p.getCategoryId() == catId)
                .collect(Collectors.toList());

        String catName = categories.stream()
                .filter(c -> c.getId() == catId)
                .map(Category::getName)
                .findFirst()
                .orElse("Không xác định");

        showPaginatedAndSortable(result, "SẢN PHẨM THUỘC DANH MỤC: " + catName);
    }

    private static void showPaginatedAndSortable(List<Product> products, String title) {
        if (products.isEmpty()) {
            System.out.println("❌ Không tìm thấy sản phẩm nào.");
            return;
        }

        while (true) {
            System.out.println("\nChọn cách sắp xếp:");
            System.out.println("1. ID tăng dần     2. ID giảm dần");
            System.out.println("3. Tên A → Z       4. Tên Z → A");
            System.out.println("5. Giá tăng dần    6. Giá giảm dần");
            System.out.println("7. Stock tăng dần  8. Stock giảm dần");
            System.out.println("0. Quay lại");

            int sortChoice = InputUtil.inputInt(sc, "Chọn sắp xếp: ", 0, 8);
            if (sortChoice == 0) return;

            List<Product> sortedList = sortProducts(products, sortChoice);
            showPaginated(sortedList, title + " (Đã sắp xếp)");
        }
    }

    private static List<Product> sortProducts(List<Product> products, int choice) {
        Comparator<Product> comparator = switch (choice) {
            case 1 -> Comparator.comparingInt(Product::getId);
            case 2 -> Comparator.comparingInt(Product::getId).reversed();
            case 3 -> Comparator.comparing(p -> p.getName() != null ? p.getName().toLowerCase() : "");
            case 4 -> Comparator.comparing((Product p) -> p.getName() != null ? p.getName().toLowerCase() : "").reversed();
            case 5 -> Comparator.comparingDouble(Product::getPrice);
            case 6 -> Comparator.comparingDouble(Product::getPrice).reversed();
            case 7 -> Comparator.comparingInt(Product::getStock);
            case 8 -> Comparator.comparingInt(Product::getStock).reversed();
            default -> Comparator.comparingInt(Product::getId);
        };
        return products.stream().sorted(comparator).collect(Collectors.toList());
    }

    private static void showPaginated(List<Product> allProducts, String title) {
        if (allProducts.isEmpty()) return;

        int total = allProducts.size();
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        int currentPage = 1;

        while (true) {
            int from = (currentPage - 1) * PAGE_SIZE;
            int to = Math.min(from + PAGE_SIZE, total);
            List<Product> pageList = allProducts.subList(from, to);

            System.out.println("\n===== " + title + " =====");
            System.out.printf("Trang %d / %d   |   Tổng: %d sản phẩm\n", currentPage, totalPages, total);
            System.out.println("=".repeat(100));

            printProductTable(pageList);

            System.out.println("\n[1] Trang trước   [2] Trang sau   [3] Chọn trang   [0] Thoát");
            int action = InputUtil.inputInt(sc, "Chọn: ", 0, 3);

            switch (action) {
                case 1 -> { if (currentPage > 1) currentPage--; else System.out.println("❌ Đang ở trang đầu!"); }
                case 2 -> { if (currentPage < totalPages) currentPage++; else System.out.println("❌ Đang ở trang cuối!"); }
                case 3 -> {
                    int chosen = InputUtil.inputInt(sc, "Nhập số trang (1-" + totalPages + "): ", 1, totalPages);
                    currentPage = chosen;
                }
                case 0 -> { return; }
            }
        }
    }

    private static void printProductTable(List<Product> list) {
        System.out.println("+-------+---------------------------+--------------------+--------------+-------------------+-------------------+----------+");
        System.out.printf("| %-5s | %-25s | %-18s | %-12s | %-17s | %17s | %8s |\n",
                "ID", "TÊN SẢN PHẨM", "DANH MỤC", "STORAGE", "COLOR", "GIÁ (VND)", "STOCK");
        System.out.println("+-------+---------------------------+--------------------+--------------+-------------------+-------------------+----------+");

        for (Product p : list) {
            String catName = getCategoryName(p.getCategoryId());
            System.out.printf("| %5d | %-25s | %-18s | %-12s | %-17s | %17s | %8d |\n",
                    p.getId(),
                    truncate(p.getName(), 25),
                    truncate(catName, 18),
                    truncate(p.getStorage(), 12),
                    truncate(p.getColor(), 17),
                    FormatUtil.formatVND(p.getPrice()),
                    p.getStock());
        }
        System.out.println("+-------+---------------------------+--------------------+--------------+-------------------+-------------------+----------+");
    }

    private static String getCategoryName(int categoryId) {
        List<Category> list = categoryService.getAll();
        return list.stream()
                .filter(c -> c.getId() == categoryId)
                .map(Category::getName)
                .findFirst()
                .orElse("Không xác định");
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
    public static Product selectProductFromList(List<Product> products, String title) {
        if (products.isEmpty()) {
            System.out.println("❌ Không có sản phẩm nào.");
            return null;
        }

        while (true) {
            System.out.println("\n===== " + title + " =====");
            System.out.println("1. Xem tất cả sản phẩm (phân trang + sắp xếp)");
            System.out.println("2. Tìm kiếm theo tên");
            System.out.println("3. Tìm kiếm theo danh mục");
            System.out.println("0. Hủy chọn");

            int choice = InputUtil.inputInt(sc, "Chọn: ", 0, 3);

            switch (choice) {
                case 1 -> {
                    return selectFromPaginatedList(products, "DANH SÁCH SẢN PHẨM");
                }
                case 2 -> {
                    String keyword = InputUtil.inputName(sc, "Nhập tên sản phẩm cần tìm: ");
                    List<Product> result = products.stream()
                            .filter(p -> p.getName() != null &&
                                    p.getName().toLowerCase().contains(keyword.toLowerCase()))
                            .collect(Collectors.toList());
                    return selectFromPaginatedList(result, "KẾT QUẢ TÌM KIẾM: " + keyword);
                }
                case 3 -> {
                    List<Category> categories = categoryService.getAll();
                    System.out.println("\n===== DANH SÁCH DANH MỤC =====");
                    for (Category c : categories) {
                        System.out.println(c.getId() + ". " + c.getName());
                    }

                    int catId = InputUtil.inputInt(sc, "Chọn ID danh mục: ", 1, 1000);

                    List<Product> result = products.stream()
                            .filter(p -> p.getCategoryId() == catId)
                            .collect(Collectors.toList());

                    String catName = categories.stream()
                            .filter(c -> c.getId() == catId)
                            .map(Category::getName)
                            .findFirst()
                            .orElse("Không xác định");

                    return selectFromPaginatedList(result, "SẢN PHẨM THUỘC DANH MỤC: " + catName);
                }
                case 0 -> {
                    return null;
                }
            }
        }
    }

    /**
     * Chọn sản phẩm từ danh sách có phân trang
     */
    private static Product selectFromPaginatedList(List<Product> products, String title) {
        if (products.isEmpty()) {
            System.out.println("❌ Không tìm thấy sản phẩm nào.");
            return null;
        }

        while (true) {
            showPaginated(products, title);   // Hiển thị danh sách phân trang

            int selectedId = InputUtil.inputInt(sc, "Nhập ID sản phẩm bạn muốn chọn (0 để quay lại): ", 0, Integer.MAX_VALUE);

            if (selectedId == 0) {
                return null;
            }

            // Tìm sản phẩm theo ID
            Product selected = products.stream()
                    .filter(p -> p.getId() == selectedId)
                    .findFirst()
                    .orElse(null);

            if (selected != null) {
                return selected;
            } else {
                System.out.println("❌ Không tìm thấy sản phẩm với ID này. Vui lòng thử lại.");
            }
        }
    }
}