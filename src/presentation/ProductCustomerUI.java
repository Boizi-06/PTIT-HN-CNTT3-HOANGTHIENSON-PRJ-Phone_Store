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

    // ==================== ANSI COLORS ====================
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String BLUE = "\u001B[34m";

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void printHeader(String title) {
        System.out.println(BOLD + PURPLE + "╔════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(BOLD + PURPLE + "║" + RESET + "              " + BOLD + YELLOW + title + RESET + "                       " + BOLD + PURPLE + "║" + RESET);
        System.out.println(BOLD + PURPLE + "╚════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    // ================= XEM SẢN PHẨM CÒN HÀNG =================
    public static void viewAvailableProducts() {
        clearScreen();


        List<Product> allProducts = productService.getAvailableProducts();

        if (allProducts.isEmpty()) {
            System.out.println(YELLOW + "⚠️  Hiện tại không có sản phẩm nào còn hàng." + RESET);
            return;
        }

        showProductsWithFeatures(allProducts, "SẢN PHẨM CÒN HÀNG      ");
    }

    // ================= HÀM CHUNG: Tìm kiếm + Phân trang + Sắp xếp =================
    public static void showProductsWithFeatures(List<Product> products, String title) {
        while (true) {
            clearScreen();


            System.out.println(BOLD + "Chọn chức năng:" + RESET);
            System.out.println("  " + BLUE + "1." + RESET + " " + YELLOW + "📋 Xem tất cả (phân trang + sắp xếp)" + RESET);
            System.out.println("  " + BLUE + "2." + RESET + " " + YELLOW + "🔍 Tìm kiếm theo tên" + RESET);
            System.out.println("  " + BLUE + "3." + RESET + " " + YELLOW + "📂 Tìm kiếm theo danh mục" + RESET);
            System.out.println("  " + BLUE + "0." + RESET + " " + RED + "← Quay lại" + RESET);

            int choice = InputUtil.inputInt(sc, YELLOW + "Chọn → " + RESET, 0, 3);

            switch (choice) {
                case 1 -> showPaginatedAndSortable(products, title);
                case 2 -> searchByName(products);
                case 3 -> searchByCategory(products);
                case 0 -> { return; }
            }
        }
    }

    private static void searchByName(List<Product> allProducts) {
        clearScreen();
        printHeader("🔍 TÌM KIẾM THEO TÊN");

        String keyword = InputUtil.inputString(sc, YELLOW + "Nhập tên sản phẩm: " + RESET);
        List<Product> result = allProducts.stream()
                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());

        showPaginatedAndSortable(result, "KẾT QUẢ TÌM KIẾM: " + keyword.toUpperCase());
    }

    private static void searchByCategory(List<Product> allProducts) {
        clearScreen();
        printHeader("📂 TÌM THEO DANH MỤC");

        List<Category> categories = categoryService.getAll();

        System.out.println(BOLD + BLUE + "DANH SÁCH DANH MỤC:" + RESET);
        for (Category c : categories) {
            System.out.println("   " + c.getId() + ". " + c.getName());
        }
        System.out.println();

        int catId = InputUtil.inputInt(sc, YELLOW + "Chọn ID danh mục: " + RESET, 1, 1000);

        List<Product> result = allProducts.stream()
                .filter(p -> p.getCategoryId() == catId)
                .collect(Collectors.toList());

        String catName = categories.stream()
                .filter(c -> c.getId() == catId)
                .map(Category::getName)
                .findFirst()
                .orElse("Không xác định");

        showPaginatedAndSortable(result, "SẢN PHẨM THUỘC: " + catName);
    }

    private static void showPaginatedAndSortable(List<Product> products, String title) {
        if (products.isEmpty()) {
            System.out.println(YELLOW + "⚠️  Không tìm thấy sản phẩm nào!" + RESET);
            return;
        }

        while (true) {
            clearScreen();
            printHeader(title);

            System.out.println(BOLD + "Chọn cách sắp xếp:" + RESET);
            System.out.println("  1. ID tăng dần      2. ID giảm dần");
            System.out.println("  3. Tên A → Z        4. Tên Z → A");
            System.out.println("  5. Giá tăng dần     6. Giá giảm dần");
            System.out.println("  7. Stock tăng dần   8. Stock giảm dần");
            System.out.println("  0. Quay lại");

            int sortChoice = InputUtil.inputInt(sc, YELLOW + "Nhập lựa chọn: " + RESET, 0, 8);
            if (sortChoice == 0) return;

            List<Product> sortedList = sortProducts(products, sortChoice);
            showPaginated(sortedList, title);
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
            clearScreen();
            printHeader(title);

            int from = (currentPage - 1) * PAGE_SIZE;
            int to = Math.min(from + PAGE_SIZE, total);
            List<Product> pageList = allProducts.subList(from, to);

            System.out.printf(BOLD + BLUE + "Trang %d / %d   |   Tổng sản phẩm: %d%n" + RESET,
                    currentPage, totalPages, total);
            System.out.println("═".repeat(110));

            printProductTable(pageList);

            System.out.println("\n" + BLUE + "[1] Trang trước    [2] Trang sau    [3] Chọn trang    [0] Thoát" + RESET);
            int action = InputUtil.inputInt(sc, YELLOW + "Chọn hành động: " + RESET, 0, 3);

            switch (action) {
                case 1 -> {
                    if (currentPage > 1) currentPage--;
                    else System.out.println(RED + "❌ Bạn đang ở trang đầu tiên!" + RESET);
                }
                case 2 -> {
                    if (currentPage < totalPages) currentPage++;
                    else System.out.println(RED + "❌ Bạn đang ở trang cuối cùng!" + RESET);
                }
                case 3 -> {
                    int chosen = InputUtil.inputInt(sc, YELLOW + "Nhập số trang (1-" + totalPages + "): " + RESET, 1, totalPages);
                    currentPage = chosen;
                }
                case 0 -> { return; }
            }
        }
    }

    // ================= BẢNG HIỂN THỊ ĐẸP =================
    private static void printProductTable(List<Product> list) {
        System.out.println(BOLD + BLUE + "+-------+---------------------------+--------------------+--------------+-------------------+-------------------+----------+" + RESET);
        System.out.printf(BOLD + BLUE + "| %-5s | %-25s | %-18s | %-12s | %-17s | %17s | %8s |%n" + RESET,
                "ID", "TÊN SẢN PHẨM", "DANH MỤC", "STORAGE", "COLOR", "GIÁ (VND)", "STOCK");
        System.out.println(BOLD + BLUE + "+-------+---------------------------+--------------------+--------------+-------------------+-------------------+----------+" + RESET);

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
        System.out.println(BOLD + BLUE + "+-------+---------------------------+--------------------+--------------+-------------------+-------------------+----------+" + RESET);
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

    // ================= CHỌN SẢN PHẨM (dùng cho Add to Cart) =================
    public static Product selectProductFromList(List<Product> products, String title) {
        if (products.isEmpty()) {
            System.out.println(YELLOW + "⚠️  Không có sản phẩm nào!" + RESET);
            return null;
        }

        while (true) {
            clearScreen();
            printHeader(title);

            System.out.println("  " + BLUE + "1." + RESET + " " + YELLOW + "📋 Xem tất cả sản phẩm" + RESET);
            System.out.println("  " + BLUE + "2." + RESET + " " + YELLOW + "🔍 Tìm theo tên" + RESET);
            System.out.println("  " + BLUE + "3." + RESET + " " + YELLOW + "📂 Tìm theo danh mục" + RESET);
            System.out.println("  " + BLUE + "0." + RESET + " " + RED + "← Hủy chọn" + RESET);

            int choice = InputUtil.inputInt(sc, YELLOW + "Chọn → " + RESET, 0, 3);

            switch (choice) {
                case 1 -> {
                    return selectFromPaginatedList(products, "DANH SÁCH SẢN PHẨM");
                }
                case 2 -> {
                    String keyword = InputUtil.inputString(sc, YELLOW + "Nhập tên sản phẩm: " + RESET);
                    List<Product> result = products.stream()
                            .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(keyword.toLowerCase()))
                            .collect(Collectors.toList());
                    return selectFromPaginatedList(result, "KẾT QUẢ TÌM KIẾM: " + keyword.toUpperCase());
                }
                case 3 -> {
                    List<Category> categories = categoryService.getAll();
                    System.out.println(BOLD + BLUE + "\nDANH SÁCH DANH MỤC:" + RESET);
                    for (Category c : categories) {
                        System.out.println("   " + c.getId() + ". " + c.getName());
                    }
                    System.out.println();

                    int catId = InputUtil.inputInt(sc, YELLOW + "Chọn ID danh mục: " + RESET, 1, 1000);

                    List<Product> result = products.stream()
                            .filter(p -> p.getCategoryId() == catId)
                            .collect(Collectors.toList());

                    String catName = categories.stream()
                            .filter(c -> c.getId() == catId)
                            .map(Category::getName)
                            .findFirst()
                            .orElse("Không xác định");

                    return selectFromPaginatedList(result, "SẢN PHẨM THUỘC: " + catName);
                }
                case 0 -> {
                    return null;
                }
            }
        }
    }

    private static Product selectFromPaginatedList(List<Product> products, String title) {
        if (products.isEmpty()) {
            System.out.println(YELLOW + "⚠️  Không tìm thấy sản phẩm!" + RESET);
            return null;
        }

        while (true) {
            showPaginated(products, title);

            int selectedId = InputUtil.inputInt(sc, YELLOW + "Nhập ID sản phẩm muốn chọn (0 để quay lại): " + RESET, 0, Integer.MAX_VALUE);

            if (selectedId == 0) return null;

            Product selected = products.stream()
                    .filter(p -> p.getId() == selectedId)
                    .findFirst()
                    .orElse(null);

            if (selected != null) {
                return selected;
            } else {
                System.out.println(RED + "❌ Không tìm thấy sản phẩm với ID này!" + RESET);
            }
        }
    }
}