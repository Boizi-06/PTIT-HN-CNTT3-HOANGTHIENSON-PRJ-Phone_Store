package presentation;

import business.service.CategoryService;
import entity.Category;
import utils.InputUtil;

import java.util.List;
import java.util.Scanner;

public class CategoryUI {

    private static final CategoryService service = new CategoryService();
    private static final Scanner sc = new Scanner(System.in);

    // ==================== ANSI COLORS ====================
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String BLUE = "\u001B[34m";

    // Clear screen (hoạt động tốt trên Linux/Mac, Windows 10+ và IntelliJ)
    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void menu() {
        while (true) {
            clearScreen(); // Xóa màn hình mỗi lần hiển thị menu

            System.out.println(BOLD + CYAN + "╔════════════════════════════════════════════════════════════╗" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "               " + BOLD + PURPLE + "🔹 CATEGORY MANAGEMENT 🔹" + RESET + "                    " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "╠════════════════════════════════════════════════════════════╣" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "1." + RESET + " " + YELLOW + "➕ Thêm category mới" + RESET + "                                   " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "2." + RESET + " " + YELLOW + "📋 Hiển thị danh sách" + RESET + "                                  " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "3." + RESET + " " + YELLOW + "✏️  Cập nhật category" + RESET + "                                  " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "4." + RESET + " " + YELLOW + "🗑️  Xóa category" + RESET + "                                       " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "0." + RESET + " " + RED + "← Quay lại" + RESET + "                                             " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "╚════════════════════════════════════════════════════════════╝" + RESET);

            int choice = InputUtil.inputInt(sc, BOLD + "Chọn chức năng → " + RESET, 0, 4);

            switch (choice) {
                case 1:
                    add();
                    break;
                case 2:
                    show();
                    break;
                case 3:
                    update();
                    break;
                case 4:
                    delete();
                    break;
                case 0:
                    clearScreen();
                    return;
            }

            // Dừng lại để người dùng đọc thông báo trước khi quay lại menu
            System.out.println("\n" + YELLOW + "Nhấn Enter để tiếp tục..." + RESET);
            sc.nextLine(); // Chờ người dùng nhấn Enter
        }
    }

    static void add() {
        clearScreen();
        printHeader("THÊM CATEGORY MỚI");

        String name = InputUtil.inputString(sc, YELLOW + "Nhập tên category: " + RESET);

        System.out.println();
        if (service.add(new Category(name))) {
            System.out.println(GREEN + "✅ Thêm thành công! Category \"" + name + "\" đã được tạo." + RESET);
        } else {
            System.out.println(RED + "❌ Thêm thất bại! Có thể tên đã tồn tại hoặc có lỗi hệ thống." + RESET);
        }
    }

    static void show() {
        clearScreen();
        printHeader("DANH SÁCH CATEGORY");

        List<Category> list = service.getAll();

        if (list.isEmpty()) {
            System.out.println(YELLOW + "⚠️  Hiện tại chưa có category nào!" + RESET);
            return;
        }

        System.out.println(BOLD + BLUE + "┌─────┬──────────────────────────────┐" + RESET);
        System.out.println(BOLD + BLUE + "│  ID │ Tên Category                 │" + RESET);
        System.out.println(BOLD + BLUE + "├─────┼──────────────────────────────┤" + RESET);

        for (Category c : list) {
            System.out.printf(BOLD + BLUE + "│ %3d │ " + RESET + "%-28s" + BOLD + BLUE + " │" + RESET + "\n",
                    c.getId(), c.getName());
        }

        System.out.println(BOLD + BLUE + "└─────┴──────────────────────────────┘" + RESET);
    }

    static void update() {
        clearScreen();
        printHeader("CẬP NHẬT CATEGORY");

        int id = InputUtil.inputInt(sc, YELLOW + "Nhập ID category cần cập nhật: " + RESET, 0, 999);

        Category c = service.findById(id);
        if (c == null) {
            System.out.println(RED + "❌ Không tìm thấy category với ID = " + id + "!" + RESET);
            return;
        }

        System.out.println(GREEN + "Tên hiện tại: " + c.getName() + RESET);
        System.out.println();

        String newName = InputUtil.inputString(sc, YELLOW + "Nhập tên mới: " + RESET);

        if (c.getName().equalsIgnoreCase(newName)) {
            System.out.println(YELLOW + "⚠️  Tên không thay đổi so với hiện tại!" + RESET);
            return;
        }

        System.out.println("\n" + BOLD + "Bạn có chắc muốn thay đổi?" + RESET);
        System.out.println(RED + "   " + c.getName() + RESET + "  →  " + GREEN + newName + RESET);
        System.out.println("1. " + GREEN + "Yes" + RESET);
        System.out.println("0. " + RED + "No" + RESET);

        int confirm = InputUtil.inputInt(sc, YELLOW + "Chọn: " + RESET, 0, 1);

        if (confirm == 1) {
            if (service.update(id, newName)) {
                System.out.println(GREEN + "✅ Cập nhật thành công!" + RESET);
            } else {
                System.out.println(RED + "❌ Cập nhật thất bại!" + RESET);
            }
        } else {
            System.out.println(YELLOW + "🚫 Đã hủy cập nhật." + RESET);
        }
    }

    static void delete() {
        clearScreen();
        printHeaderD("XÓA CATEGORY");

        int id = InputUtil.inputInt(sc, YELLOW + "Nhập ID category cần xóa: " + RESET, 0, 999);

        Category c = service.findById(id);
        if (c == null) {
            System.out.println(RED + "❌ Không tìm thấy category với ID = " + id + "!" + RESET);
            return;
        }

        System.out.println("\n" + BOLD + "Bạn có chắc muốn xóa category sau?" + RESET);
        System.out.println(RED + "   " + c.getName() + RESET);

        System.out.println("1. " + GREEN + "Yes" + RESET);
        System.out.println("0. " + RED + "No" + RESET);

        int confirm = InputUtil.inputInt(sc, YELLOW + "Chọn: " + RESET, 0, 1);

        if (confirm == 1) {
            if (service.delete(id)) {
                System.out.println(GREEN + "✅ Xóa thành công!" + RESET);
            } else {
                System.out.println(RED + "❌ Xóa thất bại!" + RESET);
            }
        } else {
            System.out.println(YELLOW + "🚫 Đã hủy xóa." + RESET);
        }
    }

    // ==================== HÀM HỖ TRỢ ====================
    private static void printHeader(String title) {
        System.out.println(BOLD + PURPLE + "╔════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(BOLD + PURPLE + "║" + RESET + "              " + BOLD + YELLOW + title + RESET + "                            " + BOLD + PURPLE + "║" + RESET);
        System.out.println(BOLD + PURPLE + "╚════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }
    private static void printHeaderD(String title) {
        System.out.println(BOLD + PURPLE + "╔════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(BOLD + PURPLE + "║" + RESET + "              " + BOLD + YELLOW + title + RESET + "                               " + BOLD + PURPLE + "║" + RESET);
        System.out.println(BOLD + PURPLE + "╚════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }
}