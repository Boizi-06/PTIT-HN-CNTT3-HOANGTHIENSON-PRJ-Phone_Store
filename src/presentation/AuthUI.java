package presentation;

import business.service.AuthService;
import entity.User;

import java.util.Scanner;

public class AuthUI {
    private AuthService authService = new AuthService();
    private Scanner sc = new Scanner(System.in);

    public void register() {
        System.out.println("=== REGISTER ===");

        String name;
        do {
            System.out.print("Name: ");
            name = sc.nextLine();
            if (name.isEmpty()) {
                System.out.println("❌ Không được để trống!");
            }
        } while (name.isEmpty());

        // EMAIL
        String email;
        do {
            System.out.print("Email: ");
            email = sc.nextLine();
            if (!utils.ValidateUtil.isValidEmail(email)) {
                System.out.println("❌ Email phải có dạng @gmail.com");
            }
        } while (!utils.ValidateUtil.isValidEmail(email));

        // PASSWORD
        String password;
        do {
            System.out.print("Password: ");
            password = sc.nextLine();
            if (!utils.ValidateUtil.isValidPassword(password)) {
                System.out.println("❌ Password ≥ 8 ký tự và có ít nhất 1 chữ hoa");
            }
        } while (!utils.ValidateUtil.isValidPassword(password));

        // PHONE
        String phone;
        do {
            System.out.print("Phone: ");
            phone = sc.nextLine();
            if (!utils.ValidateUtil.isValidPhone(phone)) {
                System.out.println("❌ Phone phải là 10 số");
            }
        } while (!utils.ValidateUtil.isValidPhone(phone));

        // ADDRESS
        String address;
        do {
            System.out.print("Address: ");
            address = sc.nextLine();
            if (address.isEmpty()) {
                System.out.println("❌ Không được để trống!");
            }
        } while (address.isEmpty());

        User user = new User(name, email, password, phone, address, "CUSTOMER");

        if (authService.register(user)) {
            System.out.println("✅ Đăng ký thành công!");
        } else {
            System.out.println("❌ Email đã tồn tại!");
        }
    }

    public User login() {
        System.out.println("=== LOGIN ===");

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        User user = authService.login(email, password);

        if (user != null) {
            System.out.println("Login thành công!");
            return user;
        } else {
            System.out.println("Sai tài khoản!");
            return null;
        }
    }
}