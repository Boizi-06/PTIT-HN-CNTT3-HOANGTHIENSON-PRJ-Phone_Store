package presentation;

import java.util.Scanner;

public class AdminUI {

    public static void adminMenu() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("=== ADMIN MENU ===");
            System.out.println("1. Quản lý Category");
            System.out.println("2. Quản lý Product");
            System.out.println("3. Quản lý Order");
            System.out.println("0. Logout");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    CategoryUI.menu();
                    break;
                case 2:
                    ProductUI.menu();
                    break;
                case 3:
                    System.out.println("Order Management...");
                    break;
                case 0:
                    return;
            }
        }
    }
}