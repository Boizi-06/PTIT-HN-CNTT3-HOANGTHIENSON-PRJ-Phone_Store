package presentation;

import entity.User;
import utils.InputUtil;

import java.util.Scanner;

public class CustomerUI {

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

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void pause() {
        System.out.println("\n" + YELLOW + "Nhấn Enter để tiếp tục..." + RESET);
        sc.nextLine();
    }

    public static void customerMenu(User currentUser) {
        if (currentUser == null) {
            System.out.println(RED + "❌ Lỗi: Chưa đăng nhập!" + RESET);
            return;
        }

        while (true) {
            clearScreen();

            System.out.println(BOLD + CYAN + "╔════════════════════════════════════════════════════════════╗" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "                " + BOLD + PURPLE + "🛒 CUSTOMER MENU 🛒" + RESET + "                         " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "╠════════════════════════════════════════════════════════════╣" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + GREEN + "Xin chào, " + BOLD + currentUser.getName() + RESET + "                                " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "╠════════════════════════════════════════════════════════════╣" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "1." + RESET + " " + YELLOW + "👀 Xem sản phẩm còn hàng" + RESET + "                               " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "2." + RESET + " " + YELLOW + "🛍️  Thêm vào giỏ hàng" + RESET + "                                  " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "3." + RESET + " " + YELLOW + "🛒 Xem giỏ hàng" + RESET + "                                        " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "4." + RESET + " " + YELLOW + "💳 Đặt hàng / Thanh toán" + RESET + "                               " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "5." + RESET + " " + YELLOW + "📜 Lịch sử đơn hàng" + RESET + "                                    " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "║" + RESET + "  " + BLUE + "0." + RESET + " " + RED + "← Đăng xuất" + RESET + "                                            " + BOLD + CYAN + "║" + RESET);
            System.out.println(BOLD + CYAN + "╚════════════════════════════════════════════════════════════╝" + RESET);

            int choice = InputUtil.inputInt(sc, BOLD + "Chọn chức năng → " + RESET, 0, 5);

            switch (choice) {
                case 1 -> ProductCustomerUI.viewAvailableProducts();
                case 2 -> CartUI.addToCart(currentUser);
                case 3 -> CartUI.viewCart(currentUser);
                case 4 -> OrderUI.checkout(currentUser);
                case 5 -> OrderUI.viewOrderHistory(currentUser);
                case 0 -> {
                    clearScreen();
                    System.out.println(GREEN + "👋 Đăng xuất thành công! Hẹn gặp lại bạn." + RESET);
                    pause();
                    return;
                }
                default -> System.out.println(RED + "❌ Chức năng không hợp lệ! Vui lòng chọn lại." + RESET);
            }

            if (choice != 0) {
                pause();
            }
        }
    }
}