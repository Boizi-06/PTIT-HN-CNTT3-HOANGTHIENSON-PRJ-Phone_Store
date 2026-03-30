package presentation;

import business.service.CategoryService;
import entity.Category;
import utils.InputUtil;

import java.util.List;
import java.util.Scanner;

public class CategoryUI {
    private static CategoryService service = new CategoryService();
    private static Scanner sc = new Scanner(System.in);

    public static void menu() {
        while (true) {
            System.out.println("=== CATEGORY MANAGEMENT ===");
            System.out.println("1. Add");
            System.out.println("2. Show");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("0. Back");

            int choice = InputUtil.inputInt(sc, "Chọn: ",0,4);

            switch (choice) {
                case 1:
                    add();
                    break;
                case 2:
                    show();
                    break;
                case 3:
                    update();
                    break;
                case 4:
                    delete();
                    break;
                case 0:
                    return;
            }
        }
    }

    static void add() {
        String name = InputUtil.inputString(sc, "Tên category: ");

        if (service.add(new Category(name))) {
            System.out.println("✅ Thêm thành công!");
        } else {
            System.out.println("❌ Thêm thất bại!");
        }
    }

    static void show() {
        List<Category> list = service.getAll();

        if (list.isEmpty()) {
            System.out.println("⚠️ Không có category nào!");
            return;
        }

        for (Category c : list) {
            System.out.println(c.getId() + " - " + c.getName());
        }
    }
    static void update() {
        int id = InputUtil.inputInt(sc, "Nhập ID: ", 0, 999);

        // 🔥 Lấy category theo ID
        Category c = service.findById(id);

        if (c == null) {
            System.out.println("❌ Không tìm thấy category!");
            return;
        }

        // 🔥 Hiển thị tên cũ
        System.out.println("Tên hiện tại: " + c.getName());

        // Nhập tên mới
        String newName = InputUtil.inputString(sc, "Tên mới: ");
        if (c.getName().equalsIgnoreCase(newName)) {
            System.out.println("⚠️ Tên không thay đổi!");
            return;
        }

        // 🔥 Confirm
        System.out.println("Bạn có chắc muốn đổi:");
        System.out.println(c.getName() + " → " + newName);
        System.out.println("1. Yes");
        System.out.println("0. No");

        int confirm = InputUtil.inputInt(sc, "Chọn: ", 0, 1);

        if (confirm == 1) {
            if (service.update(id, newName)) {
                System.out.println("✅ Update thành công!");
            } else {
                System.out.println("❌ Update thất bại!");
            }
        } else {
            System.out.println("🚫 Đã hủy cập nhật!");
        }
    }

    static void delete() {
        int id = InputUtil.inputInt(sc, "Nhập ID: ",0,999);

        Category c = service.findById(id);

        if (c == null) {
            System.out.println("❌ Không tìm thấy category!");
            return;
        }

        System.out.println("Bạn có chắc muốn xóa: " + c.getName() + " ?");
        System.out.println("1. Yes");
        System.out.println("0. No");

        int confirm = InputUtil.inputInt(sc, "Chọn: ", 0, 1);

        if (confirm == 1) {
            if (service.delete(id)) {
                System.out.println("✅ Xóa thành công!");
            } else {
                System.out.println("❌ Xóa thất bại!");
            }
        } else {
            System.out.println("🚫 Đã hủy xóa!");
        }
    }
}