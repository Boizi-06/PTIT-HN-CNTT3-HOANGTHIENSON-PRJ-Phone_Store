package presentation;

import entity.User;
import utils.InputUtil;

import java.util.Scanner;

public class CustomerUI {

    public static void customerMenu(User user) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("=== CUSTOMER MENU ===");
            System.out.println("Xin chào: " + user.getName());
            System.out.println("1. Xem sản phẩm");
            System.out.println("2. Giỏ hàng");
            System.out.println("3. Đơn hàng của tôi");
            System.out.println("0. Logout");

            int choice = InputUtil.inputInt(sc, "Chọn: ",0,3);

            switch (choice) {
                case 1:
                    System.out.println("Xem sản phẩm...");
                    break;
                case 2:
                    System.out.println("Giỏ hàng...");
                    break;
                case 3:
                    System.out.println("Đơn hàng...");
                    break;
                case 0:
                    return;
            }
        }
    }
}