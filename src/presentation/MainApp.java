package presentation;

import entity.User;
import utils.InputUtil;

import java.util.Scanner;

public class MainApp {

    // ==================== MÀU SẮC ANSI ====================
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";

    public static void main(String[] args) {
        AuthUI authUI = new AuthUI();
        Scanner sc = new Scanner(System.in);

        while (true) {
            printWelcomeHeader();

            System.out.println(" " + CYAN + "1." + RESET + " Đăng ký tài khoản");
            System.out.println(" " + CYAN + "2." + RESET + " Đăng nhập");
            System.out.println(" " + RED + "0." + RESET + " Thoát chương trình");

            System.out.println();
            int choice = InputUtil.inputInt(sc, BOLD + "➤ Chọn chức năng: " + RESET, 0, 2);

            switch (choice) {
                case 1:
                    authUI.register();
                    break;

                case 2:
                    User user = authUI.login();

                    if (user != null) {
                        if (user.getRole().equalsIgnoreCase("ADMIN")) {
                            System.out.println(GREEN + "\n✅ Đăng nhập thành công với quyền ADMIN" + RESET);
                            pause();
                            AdminUI.adminMenu();
                        } else {
                            System.out.println(GREEN + "\n✅ Đăng nhập thành công với quyền CUSTOMER" + RESET);
                            pause();
                            CustomerUI.customerMenu(user);
                        }
                    }
                    break;

                case 0:
                    System.out.println("\n" + YELLOW + "👋 Cảm ơn bạn đã sử dụng chương trình. Hẹn gặp lại!" + RESET);
                    sc.close();
                    return;

                default:
                    System.out.println(RED + "❌ Lựa chọn không hợp lệ!" + RESET);
            }
        }
    }

    // ==================== HIỂN THỊ HEADER ĐẸP ====================
    private static void printWelcomeHeader() {
        System.out.println();
        System.out.println(BOLD + CYAN + "╔" + "═".repeat(60) + "╗" + RESET);
        System.out.println(BOLD + CYAN + "║" + " ".repeat(22) + "🛒 SHOP MANAGEMENT" + " ".repeat(20) + "║" + RESET);
        System.out.println(BOLD + CYAN + "╠" + "═".repeat(60) + "╣" + RESET);
        System.out.println(BOLD + CYAN + "║" + " ".repeat(18) + "Chào mừng bạn đến với hệ thống" + " ".repeat(12) + "║" + RESET);
        System.out.println(BOLD + CYAN + "╚" + "═".repeat(60) + "╝" + RESET);
        System.out.println();
    }

    // ==================== TẠM DỪNG ĐỂ XEM THÔNG BÁO ====================
    private static void pause() {
        System.out.println();
        System.out.print(YELLOW + "Nhấn Enter để tiếp tục..." + RESET);
        try {
            System.in.read();
        } catch (Exception ignored) {}
    }
}