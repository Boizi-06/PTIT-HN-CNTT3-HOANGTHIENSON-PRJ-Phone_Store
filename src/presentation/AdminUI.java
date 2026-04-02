package presentation;

import utils.InputUtil;

import java.util.Scanner;

public class AdminUI {

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
        Scanner sc = new Scanner(System.in);

        while (true) {
            printAdminHeader();

            System.out.println(" " + CYAN + "1." + RESET + " Quản lý Danh mục (Category)");
            System.out.println(" " + CYAN + "2." + RESET + " Quản lý Sản phẩm (Product)");
            System.out.println(" " + CYAN + "3." + RESET + " Quản lý Đơn hàng (Order)");
            System.out.println(" " + RED + "0." + RESET + " Đăng xuất (Logout)");

            System.out.println();
            int choice = InputUtil.inputInt(sc, BOLD + "➤ Chọn chức năng: " + RESET, 0, 3);

            switch (choice) {
                case 1:
                    System.out.println(GREEN + "\n→ Chuyển sang Quản lý Danh mục..." + RESET);
                    CategoryUI.menu();
                    break;

                case 2:
                    System.out.println(GREEN + "\n→ Chuyển sang Quản lý Sản phẩm..." + RESET);
                    ProductUI.menu();
                    break;

                case 3:
                    System.out.println(GREEN + "\n→ Chuyển sang Quản lý Đơn hàng..." + RESET);
                    AdminOrderUI.manageOrders();
                    break;

                case 0:
                    System.out.println(YELLOW + "\n👋 Đã đăng xuất. Hẹn gặp lại!" + RESET);
                    return;

                default:
                    System.out.println(RED + "❌ Lựa chọn không hợp lệ!" + RESET);
            }
        }
    }

    // ==================== HEADER ĐẸP CHO ADMIN ====================
    private static void printAdminHeader() {
        System.out.println();
        System.out.println(BOLD + PURPLE + "╔" + "═".repeat(70) + "╗" + RESET);
        System.out.println(BOLD + PURPLE + "║" + " ".repeat(25) + "👨‍💼 ADMIN PANEL" + " ".repeat(31) + "║" + RESET);
        System.out.println(BOLD + PURPLE + "╠" + "═".repeat(70) + "╣" + RESET);
        System.out.println(BOLD + PURPLE + "║" + " ".repeat(20) + "Chào mừng bạn đến với khu vực quản trị" + " ".repeat(12) + "║" + RESET);
        System.out.println(BOLD + PURPLE + "╚" + "═".repeat(70) + "╝" + RESET);
        System.out.println();
    }
}