package presentation;

import business.service.AuthService;
import entity.User;
import utils.InputUtil;
import utils.ValidateUtil;

import java.util.Scanner;

public class AuthUI {

    private final AuthService authService = new AuthService();
    private final Scanner sc = new Scanner(System.in);

    // ==================== MÀU SẮC ANSI ====================
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String BLUE = "\u001B[34m";

    public void register() {
        printAuthHeader("📝 ĐĂNG KÝ TÀI KHOẢN");

        String name = inputWithValidation("👤 Họ và tên",
                input -> !input.trim().isEmpty(),
                "❌ Họ tên không được để trống!");

        String email = inputWithValidation("📧 Email",
                ValidateUtil::isValidEmail,
                "❌ Email phải có dạng @gmail.com!");

        // Mật khẩu mới với yêu cầu mạnh hơn
        String password = inputWithValidation("🔑 Mật khẩu",
                this::isStrongPassword,
                "❌ Mật khẩu phải ≥ 8 ký tự, có ít nhất 1 chữ hoa và 1 ký tự đặc biệt!");

        String phone = inputWithValidation("📱 Số điện thoại",
                ValidateUtil::isValidPhone,
                "❌ Số điện thoại phải là 10 số!");

        String address = inputWithValidation("🏠 Địa chỉ",
                input -> !input.trim().isEmpty(),
                "❌ Địa chỉ không được để trống!");

        User newUser = new User(name.trim(), email.trim(), password, phone.trim(), address.trim(), "CUSTOMER");

        System.out.println("\n" + YELLOW + "Đang xử lý đăng ký..." + RESET);
        if (authService.register(newUser)) {
            System.out.println(GREEN + "\n✅ Đăng ký tài khoản thành công!" + RESET);
            System.out.println(GREEN + "Bạn có thể đăng nhập ngay bây giờ." + RESET);
        } else {
            System.out.println(RED + "\n❌ Đăng ký thất bại! Email này đã tồn tại." + RESET);
        }
    }

    public User login() {
        printAuthHeader("🔐 ĐĂNG NHẬP");

        System.out.print(BOLD + "📧 Email: " + RESET);
        String email = sc.nextLine().trim();

        System.out.print(BOLD + "🔑 Mật khẩu: " + RESET);
        String password = sc.nextLine();   // Không trim password

        System.out.println("\n" + YELLOW + "Đang kiểm tra thông tin..." + RESET);

        User user = authService.login(email, password);

        if (user != null) {
            System.out.println(GREEN + "\n✅ Đăng nhập thành công!" + RESET);
            System.out.println(GREEN + "Chào mừng " + BOLD + user.getName() + RESET + " trở lại!" + RESET);
            return user;
        } else {
            System.out.println(RED + "\n❌ Email hoặc mật khẩu không chính xác!" + RESET);
            return null;
        }
    }

    // ==================== VALIDATE MẬT KHẨU MẠNH ====================
    private boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            }
            if (!Character.isLetterOrDigit(c)) {   // Ký tự đặc biệt
                hasSpecialChar = true;
            }
            if (hasUpperCase && hasSpecialChar) {
                return true;   // Đủ điều kiện thì dừng sớm
            }
        }

        return hasUpperCase && hasSpecialChar;
    }

    // ==================== HÀM HỖ TRỢ NHẬP CÓ KIỂM TRA ====================
    private String inputWithValidation(String fieldName, java.util.function.Predicate<String> validator, String errorMsg) {
        String input;
        while (true) {
            System.out.print(BOLD + fieldName + ": " + RESET);
            input = sc.nextLine().trim();

            if (validator.test(input)) {
                return input;
            }
            System.out.println(RED + errorMsg + RESET);
        }
    }

    // ==================== HEADER ĐẸP ====================
    private void printAuthHeader(String title) {
        System.out.println();
        System.out.println(BOLD + CYAN + "╔" + "═".repeat(60) + "╗" + RESET);
        System.out.println(BOLD + CYAN + "║" + " ".repeat(20) + title + " ".repeat(60 - 20 - title.length()) + "║" + RESET);
        System.out.println(BOLD + CYAN + "╚" + "═".repeat(60) + "╝" + RESET);
        System.out.println();
    }
}