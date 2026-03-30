package presentation;

import entity.User;
import utils.InputUtil;

import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        AuthUI authUI = new AuthUI();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");

            int choice = InputUtil.inputInt(sc, "Chọn: ",0,2);

            switch (choice) {
                case 1:
                    authUI.register();
                    break;

                case 2:

                    User user = authUI.login();

                    if (user != null) {
                        if (user.getRole().equalsIgnoreCase("ADMIN")) {
                            System.out.println("Đăng nhập ADMIN");
                            AdminUI.adminMenu();
                        } else {
                            System.out.println("Đăng nhập CUSTOMER");
                            CustomerUI.customerMenu(user);
                        }
                    }
                    break;

                case 0:
                    return;

                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }
}