package utils;

import java.util.Scanner;

public class InputUtil {

    public static int inputInt(Scanner sc, String message, int min, int max) {
        while (true) {
            try {
                System.out.print(message);
                int value = Integer.parseInt(sc.nextLine());

                if (value < min || value > max) {
                    System.out.println("❌ Nhập từ " + min + " đến " + max);
                } else {
                    return value;
                }

            } catch (Exception e) {
                System.out.println("❌ Phải nhập số!");
            }
        }
    }
    public static int inputPrice(Scanner sc, String message, int min) {
        while (true) {
            try {
                System.out.print(message);
                int value = Integer.parseInt(sc.nextLine());

                if (value < min ) {
                    System.out.println("❌ Giá tiền bắt buộc lơn hơn " + min );
                } else {
                    return value;
                }

            } catch (Exception e) {
                System.out.println("❌ Phải nhập số!");
            }
        }
    }
    public static String inputString(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("❌ Không được để trống!");
            } else if (input.matches(".*\\d.*")) {
                System.out.println("❌ Không được chứa số!");
            } else {
                return input;
            }
        }
    }

    public static String inputProduct(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("❌ Không được để trống!");

            } else {
                return input;
            }
        }
    }
    public static String inputName(Scanner sc, String message) {
        while (true) {
            String input = inputString(sc, message);

            if (input.matches(".*\\d.*")) {
                System.out.println("❌ Tên không được chứa số!");
            } else {
                return input;
            }
        }
    }
}