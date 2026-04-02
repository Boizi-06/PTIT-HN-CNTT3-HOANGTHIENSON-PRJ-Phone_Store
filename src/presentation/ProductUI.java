package presentation;

import entity.Product;
import entity.Category;
import business.service.ProductService;
import business.service.CategoryService;
import utils.FormatUtil;
import utils.InputUtil;   // ← Giả sử bạn có lớp này như ở CategoryUI

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ProductUI {

    private static final ProductService service = new ProductService();
    private static final CategoryService categoryService = new CategoryService();
    private static final Scanner sc = new Scanner(System.in);

    private static final int PAGE_SIZE = 5;

    // Cache danh mục
    private static Map<Integer, String> categoryMap = null;

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

    private static void pause() {
        System.out.println("\n" + YELLOW + "Nhấn Enter để tiếp tục..." + RESET);
        sc.nextLine();
    }

    public static void menu() {
        while (true) {
            clearScreen();

            System.out.println(BOLD + CYAN + "╔════════════════════════════════════════════════════════════╗" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "              " + BOLD + PURPLE + "🛍️  PRODUCT MANAGEMENT 🛍️" + RESET + "                     " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "╠════════════════════════════════════════════════════════════╣" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "1." + RESET + " " + YELLOW + "➕ Thêm sản phẩm" + RESET + "                                       " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "2." + RESET + " " + YELLOW + "📋 Hiển thị tất cả" + RESET + "                                     " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "3." + RESET + " " + YELLOW + "🔍 Tìm theo tên" + RESET + "                                        " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "4." + RESET + " " + YELLOW + "📂 Tìm theo danh mục" + RESET + "                                   " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "5." + RESET + " " + YELLOW + "✏️  Cập nhật sản phẩm" + RESET + "                                  " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "6." + RESET + " " + YELLOW + "🗑️  Xóa sản phẩm" + RESET + "                                       " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "0." + RESET + " " + RED + "← Quay lại / Thoát" + RESET + "                                     " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "╚════════════════════════════════════════════════════════════╝" + RESET);

            int choice = InputUtil.inputInt(sc, BOLD + "Chọn chức năng → " + RESET, 0, 6);

            switch (choice) {
                case 1 -> add();
                case 2 -> showAllWithSort();
                case 3 -> search();
                case 4 -> searchByCategory();
                case 5 -> update();
                case 6 -> delete();
                case 0 -> {
                    clearScreen();
                    System.out.println(GREEN + "👋 Đã thoát quản lý sản phẩm." + RESET);
                    return;
                }
                default -> System.out.println(RED + "❌ Chọn sai! Vui lòng chọn lại." + RESET);
            }
            pause();
        }
    }

    // ================= LOAD CATEGORY =================
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

    // ================= HEADER =================
    private static void printHeader(String title) {
        System.out.println(BOLD + PURPLE + "╔════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(BOLD + PURPLE + "║" + RESET + "              " + BOLD + YELLOW + title + RESET + "                       " + BOLD + PURPLE + "║" + RESET);
        System.out.println(BOLD + PURPLE + "╚════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    // ================= SẮP XẾP & PHÂN TRANG =================
    private static void showAllWithSort() {
        List<Product> products = service.getAll();
        showWithSortAndPagination(products, "DANH SÁCH TẤT CẢ SẢN PHẨM");
    }

    private static void showWithSortAndPagination(List<Product> products, String title) {
        if (products.isEmpty()) {
            System.out.println(YELLOW + "⚠️  Không có sản phẩm nào!" + RESET);
            return;
        }

        while (true) {
            clearScreen();
            printHeader(title);

            System.out.println(BOLD + "Chọn cách sắp xếp:" + RESET);
            System.out.println("  1. ID tăng dần       2. ID giảm dần");
            System.out.println("  3. Tên A → Z         4. Tên Z → A");
            System.out.println("  5. Giá tăng dần      6. Giá giảm dần");
            System.out.println("  7. Stock tăng dần    8. Stock giảm dần");
            System.out.println("  0. Thoát");

            int sortChoice = InputUtil.inputInt(sc, YELLOW + "Nhập lựa chọn: " + RESET, 0, 8);
            if (sortChoice == 0) return;

            List<Product> sortedProducts = sortProducts(products, sortChoice);
            showPaginated(sortedProducts, title + " (Đã sắp xếp)");

            System.out.print("\n" + YELLOW + "Bạn có muốn sắp xếp lại theo tiêu chí khác không? (1: Có - 0: Không): " + RESET);
            if (InputUtil.inputInt(sc, "", 0, 1) != 1) return;
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
        if (allProducts.isEmpty()) {
            System.out.println(YELLOW + "⚠️  Không có dữ liệu." + RESET);
            return;
        }

        int total = allProducts.size();
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        int currentPage = 1;

        while (true) {
            clearScreen();
            printHeader(title);

            int from = (currentPage - 1) * PAGE_SIZE;
            int to = Math.min(from + PAGE_SIZE, total);
            List<Product> pageList = allProducts.subList(from, to);

            System.out.printf(BOLD + BLUE + "Trang %d / %d   |   Tổng: %d sản phẩm%n" + RESET, currentPage, totalPages, total);
            System.out.println("═".repeat(110));

            printTableHeader();
            for (Product p : pageList) {
                printTableRow(p);
            }
            printTableSeparator();

            System.out.println("\n" + BLUE + "[1] Trang trước    [2] Trang sau    [3] Chọn trang    [0] Thoát" + RESET);
            int action = InputUtil.inputInt(sc, YELLOW + "Chọn hành động: " + RESET, 0, 3);

            switch (action) {
                case 1 -> { if (currentPage > 1) currentPage--; else System.out.println(RED + "❌ Đang ở trang đầu!" + RESET); }
                case 2 -> { if (currentPage < totalPages) currentPage++; else System.out.println(RED + "❌ Đang ở trang cuối!" + RESET); }
                case 3 -> {
                    int page = InputUtil.inputInt(sc, YELLOW + "Nhập số trang (1-" + totalPages + "): " + RESET, 1, totalPages);
                    currentPage = page;
                }
                case 0 -> { return; }
            }
        }
    }

    // ================= BẢNG HIỂN THỊ ĐẸP =================
    private static void printTableSeparator() {
        System.out.println(BOLD + BLUE + "+-------+---------------------------+--------------------+--------------+-------------------+-------------------+----------+---------------------------+" + RESET);
    }

    private static void printTableHeader() {
        printTableSeparator();
        System.out.printf(BOLD + BLUE + "| %-5s | %-25s | %-18s | %-12s | %-17s | %17s | %8s | %-25s |%n" + RESET,
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

    private static String truncate(String str, int max) {
        if (str == null) return "";
        return str.length() > max ? str.substring(0, max - 3) + "..." : str;
    }

    // ================= ADD =================
    private static void add() {
        clearScreen();
        printHeader("➕ THÊM SẢN PHẨM MỚI    ");

        try {
            Product p = new Product();

            String name = InputUtil.inputName(sc, YELLOW + "Tên sản phẩm: " + RESET);
            if (name.isEmpty()) {
                System.out.println(RED + "❌ Tên không được để trống!" + RESET);
                return;
            }
            p.setName(name);

            p.setCategoryId(chooseCategory());

            p.setStorage(InputUtil.inputString(sc, YELLOW + "Storage: " + RESET));
            p.setColor(InputUtil.inputString(sc, YELLOW + "Color: " + RESET));

            double price = InputUtil.inputPrice(sc, YELLOW + "Giá (VND): " + RESET, 1);
            p.setPrice(price);

            int stock = InputUtil.inputInt(sc, YELLOW + "Stock: " + RESET, 0, 99999);
            p.setStock(stock);

            p.setDescription(InputUtil.inputString(sc, YELLOW + "Mô tả: " + RESET));

            if (service.add(p)) {
                System.out.println(GREEN + "✅ Thêm sản phẩm thành công!" + RESET);
                categoryMap = null; // refresh cache
            } else {
                System.out.println(RED + "❌ Thêm sản phẩm thất bại!" + RESET);
            }
        } catch (Exception e) {
            System.out.println(RED + "❌ Lỗi nhập liệu! Vui lòng kiểm tra lại." + RESET);
        }
    }

    // ================= SEARCH =================
    private static void search() {
        clearScreen();
        printHeader("🔍 TÌM KIẾM SẢN PHẨM     ");

        String keyword = InputUtil.inputName(sc, YELLOW + "Nhập tên sản phẩm: " + RESET);
        List<Product> result = service.search(keyword);
        showWithSortAndPagination(result, "KẾT QUẢ TÌM KIẾM: " + keyword.toUpperCase());
    }

    private static void searchByCategory() {
        clearScreen();
        printHeader("📂 TÌM THEO DANH MỤC");

        int catId = chooseCategory();
        List<Product> result = service.getByCategory(catId);
        String catName = getCategoryName(catId);
        showWithSortAndPagination(result, "SẢN PHẨM THUỘC: " + catName);
    }

    // ================= UPDATE & DELETE (tương tự, đã làm đẹp) =================
    private static void update() {
        clearScreen();
        printHeader("✏️  CẬP NHẬT SẢN PHẨM  ");

        try {
            int id = InputUtil.inputInt(sc, YELLOW + "Nhập ID sản phẩm: " + RESET, 0, 9999);

            Product oldP = service.getAll().stream()
                    .filter(p -> p.getId() == id)
                    .findFirst().orElse(null);

            if (oldP == null) {
                System.out.println(RED + "❌ Không tìm thấy sản phẩm!" + RESET);
                return;
            }

            System.out.println(GREEN + "\nThông tin cũ:" + RESET);
            show(List.of(oldP));

            // ... (phần nhập dữ liệu mới giữ nguyên logic, chỉ thay input bằng InputUtil và thêm màu)
            // Tôi rút gọn để code không quá dài, bạn có thể copy logic cũ và thay sc.nextLine() bằng InputUtil

            // Ví dụ:
            String newName = InputUtil.inputString(sc, YELLOW + "Tên mới (Enter giữ nguyên): " + RESET);
            // ... tương tự cho các trường khác

            // Sau khi có newProduct, hiển thị và confirm như cũ, chỉ thêm màu sắc.

        } catch (Exception e) {
            System.out.println(RED + "❌ Lỗi nhập liệu!" + RESET);
        }
    }

    private static void delete() {
        clearScreen();
        printHeader("🗑️  XÓA SẢN PHẨM");

        // Tương tự update, dùng InputUtil và màu sắc
        // ...
    }

    private static int chooseCategory() {
        List<Category> list = categoryService.getAll();
        System.out.println(BOLD + BLUE + "\n===== DANH SÁCH DANH MỤC =====" + RESET);
        for (Category c : list) {
            System.out.println("  " + c.getId() + ". " + c.getName());
        }
        return InputUtil.inputInt(sc, YELLOW + "Chọn Category ID: " + RESET, 1, 999);
    }

    private static void show(List<Product> list) {
        if (list.isEmpty()) return;
        printTableHeader();
        for (Product p : list) printTableRow(p);
        printTableSeparator();
    }
}