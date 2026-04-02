package presentation;

import business.service.OrderService;
import entity.TopSellingProduct;
import utils.InputUtil;
import utils.FormatUtil;

import java.util.List;
import java.util.Scanner;

public class AdminUI {

    private static final Scanner sc = new Scanner(System.in);
    private static final OrderService orderService = new OrderService();

    // ==================== MÀU SẮC ANSI ====================
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String BLUE = "\u001B[34m";

    public static void adminMenu() {
        while (true) {
            clearScreen();
            printAdminHeader();

            System.out.println(BOLD + BLUE + "   1." + RESET + " " + YELLOW + "📂 Quản lý Danh mục" + RESET);
            System.out.println(BOLD + BLUE + "   2." + RESET + " " + YELLOW + "🛍️  Quản lý Sản phẩm" + RESET);
            System.out.println(BOLD + BLUE + "   3." + RESET + " " + YELLOW + "📦 Quản lý Đơn hàng" + RESET);
            System.out.println(BOLD + BLUE + "   4." + RESET + " " + YELLOW + "📊 Báo cáo thống kê & Doanh thu" + RESET);
            System.out.println(BOLD + BLUE + "   0." + RESET + " " + RED + "← Đăng xuất" + RESET);

            System.out.println();
            int choice = InputUtil.inputInt(sc, BOLD + "➤ Chọn chức năng: " + RESET, 0, 4);

            switch (choice) {
                case 1 -> { System.out.println(GREEN + "\n→ Chuyển sang Quản lý Danh mục..." + RESET); pause(); CategoryUI.menu(); }
                case 2 -> { System.out.println(GREEN + "\n→ Chuyển sang Quản lý Sản phẩm..." + RESET); pause(); ProductUI.menu(); }
                case 3 -> { System.out.println(GREEN + "\n→ Chuyển sang Quản lý Đơn hàng..." + RESET); pause(); AdminOrderUI.manageOrders(); }
                case 4 -> showStatisticsReport();
                case 0 -> {
                    clearScreen();
                    System.out.println(YELLOW + "👋 Đã đăng xuất. Hẹn gặp lại!" + RESET);
                    return;
                }
                default -> System.out.println(RED + "❌ Lựa chọn không hợp lệ!" + RESET);
            }
            if (choice != 0) pause();
        }
    }

    // ==================== BÁO CÁO THỐNG KÊ (ĐÃ SỬA) ====================
    private static void showStatisticsReport() {
        clearScreen();
        printReportHeader("📊 BÁO CÁO THỐNG KÊ DOANH THU");

        double totalRevenue = orderService.getTotalRevenue();
        System.out.println(BOLD + GREEN + "TỔNG DOANH THU (chỉ tính đơn DELIVERED): "
                + FormatUtil.formatVND(totalRevenue) + RESET);
        System.out.println("═".repeat(90));

        List<TopSellingProduct> top5 = orderService.getTopSellingProducts(5);

        if (top5.isEmpty()) {
            System.out.println(YELLOW + "⚠️  Chưa có dữ liệu bán hàng nào (không có đơn hàng trạng thái DELIVERED)." + RESET);
            System.out.println(YELLOW + "   → Hãy tạo một số đơn hàng và chuyển trạng thái sang DELIVERED để kiểm tra." + RESET);
            pause();
            return;
        }

        System.out.println(BOLD + BLUE + "🏆 TOP 5 SẢN PHẨM BÁN CHẠY NHẤT" + RESET);
        System.out.println(BOLD + BLUE + "┌────┬──────────────────────────────┬───────────────┬─────────────────┐" + RESET);
        System.out.println(BOLD + BLUE + "│ STT│ Tên sản phẩm                 │ Số lượng bán  │ Doanh thu       │" + RESET);
        System.out.println(BOLD + BLUE + "├────┼──────────────────────────────┼───────────────┼─────────────────┤" + RESET);

        int rank = 1;
        for (TopSellingProduct item : top5) {
            String name = truncate(item.getProductName(), 28);

            System.out.print(BOLD + BLUE + "│ " + RESET);
            System.out.printf("%2d ", rank++);
            System.out.print(BOLD + BLUE + "│ " + RESET);
            System.out.printf("%-28s", name);
            System.out.print(BOLD + BLUE + " │ " + RESET);
            System.out.printf("%13d", item.getQuantitySold());
            System.out.print(BOLD + BLUE + " │ " + RESET);
            System.out.printf("%15s", FormatUtil.formatVND(item.getRevenue()));
            System.out.println(BOLD + BLUE + " │" + RESET);
        }

        System.out.println(BOLD + BLUE + "└────┴──────────────────────────────┴───────────────┴─────────────────┘" + RESET);
    }

    // ==================== HEADER & UTILS ====================
    private static void printAdminHeader() {
        System.out.println(BOLD + PURPLE + "╔" + "═".repeat(68) + "╗" + RESET);
        System.out.println(BOLD + PURPLE + "║" + RESET + " ".repeat(24) + BOLD + "👨‍💼 ADMIN PANEL" + RESET + " ".repeat(27) + BOLD + PURPLE + "║" + RESET);
        System.out.println(BOLD + PURPLE + "╠" + "═".repeat(68) + "╣" + RESET);
        System.out.println(BOLD + PURPLE + "║" + RESET + " ".repeat(18) + "Khu vực quản trị hệ thống" + " ".repeat(25) + BOLD + PURPLE + "║" + RESET);
        System.out.println(BOLD + PURPLE + "╚" + "═".repeat(68) + "╝" + RESET);
        System.out.println();
    }

    private static void printReportHeader(String title) {
        System.out.println(BOLD + PURPLE + "╔" + "═".repeat(68) + "╗" + RESET);
        System.out.println(BOLD + PURPLE + "║" + RESET + " ".repeat(20) + BOLD + YELLOW + title + RESET + " ".repeat(20) + BOLD + PURPLE + "║" + RESET);
        System.out.println(BOLD + PURPLE + "╚" + "═".repeat(68) + "╝" + RESET);
        System.out.println();
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void pause() {
        System.out.println("\n" + YELLOW + "Nhấn Enter để tiếp tục..." + RESET);
        sc.nextLine();
    }
}

