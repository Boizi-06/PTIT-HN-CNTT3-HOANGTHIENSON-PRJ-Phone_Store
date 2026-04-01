package presentation;

import entity.CartItem;
import entity.Product;
import entity.User;
import business.service.CartService;
import business.service.ProductService;
import utils.InputUtil;
import utils.FormatUtil;

import java.util.List;
import java.util.Scanner;

public class CartUI {

    private static final CartService cartService = new CartService();
    private static final ProductService productService = new ProductService();
    private static final Scanner sc = new Scanner(System.in);

    // ================= THÊM SẢN PHẨM VÀO GIỎ HÀNG =================
    public static void addToCart(User user) {
        System.out.println("\n===== THÊM SẢN PHẨM VÀO GIỎ HÀNG =====");

        List<Product> allAvailableProducts = productService.getAvailableProducts();

        if (allAvailableProducts.isEmpty()) {
            System.out.println("❌ Hiện không có sản phẩm nào còn hàng.");
            return;
        }

        Product selectedProduct = ProductCustomerUI.selectProductFromList(
                allAvailableProducts,
                "Chọn sản phẩm muốn thêm vào giỏ hàng"
        );

        if (selectedProduct == null) {
            System.out.println("ℹ️  Đã hủy chọn sản phẩm.");
            return;
        }

        int maxQuantity = Math.min(100, selectedProduct.getStock());
        int quantity = InputUtil.inputInt(sc,
                "Nhập số lượng (tối đa " + maxQuantity + "): ",
                1, maxQuantity);

        System.out.println("\nXác nhận thêm vào giỏ hàng:");
        System.out.println("Sản phẩm : " + selectedProduct.getName());
        System.out.println("Số lượng : " + quantity);

        if (InputUtil.inputInt(sc, "Xác nhận (1: Có - 0: Không): ", 0, 1) != 1) {
            System.out.println("Đã hủy thao tác.");
            return;
        }

        boolean success = cartService.addToCart(user.getId(), selectedProduct.getId(), quantity);

        if (success) {
            System.out.println("✅ Đã thêm **" + quantity + "** " + selectedProduct.getName() + " vào giỏ hàng thành công!");
        } else {
            System.out.println("❌ Thêm vào giỏ hàng thất bại! (Sản phẩm có thể không đủ hàng)");
        }
    }

    // ================= XEM VÀ QUẢN LÝ GIỎ HÀNG =================
    public static void viewCart(User user) {
        System.out.println("\n===== 🛒 GIỎ HÀNG CỦA BẠN =====");

        List<CartItem> cartItems = cartService.getCartItems(user.getId());

        if (cartItems.isEmpty()) {
            System.out.println("❌ Giỏ hàng của bạn đang trống.");
            return;
        }

        printCartTable(cartItems);

        double totalAmount = cartItems.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();

        System.out.println("=".repeat(110));
        System.out.printf("TỔNG TIỀN: %s%n", FormatUtil.formatVND(totalAmount));
        System.out.println("=".repeat(110));

        System.out.println("\n📋 Chọn hành động:");
        System.out.println("   1. Thay đổi số lượng sản phẩm");
        System.out.println("   2. Xóa một sản phẩm khỏi giỏ hàng");
        System.out.println("   3. 🗑️  Xóa toàn bộ giỏ hàng");
        System.out.println("   0. Quay lại");

        int choice = InputUtil.inputInt(sc, "Nhập lựa chọn của bạn: ", 0, 3);

        if (choice == 0) return;

        switch (choice) {
            case 1 -> handleUpdateQuantity(user, cartItems);
            case 2 -> handleRemoveItem(user, cartItems);
            case 3 -> handleClearAllCart(user);
        }
    }

    private static void handleUpdateQuantity(User user, List<CartItem> cartItems) {
        printCartTable(cartItems);
        int stt = InputUtil.inputInt(sc, "Nhập STT sản phẩm muốn thay đổi số lượng: ", 1, cartItems.size());
        CartItem item = cartItems.get(stt - 1);

        int newQuantity = InputUtil.inputInt(sc,
                "Nhập số lượng mới cho " + item.getProductName() + " (1-100): ",
                1, 100);

        System.out.printf("Xác nhận thay đổi %s: %d → %d ?%n",
                item.getProductName(), item.getQuantity(), newQuantity);

        if (InputUtil.inputInt(sc, "Xác nhận (1: Có - 0: Không): ", 0, 1) == 1) {
            boolean success = cartService.updateCartItemQuantity(user.getId(), item.getCartItemId(), newQuantity);
            System.out.println(success ? "✅ Cập nhật số lượng thành công!" : "❌ Cập nhật số lượng thất bại!");
        }
    }

    private static void handleRemoveItem(User user, List<CartItem> cartItems) {
        printCartTable(cartItems);
        int stt = InputUtil.inputInt(sc, "Nhập STT sản phẩm muốn xóa: ", 1, cartItems.size());
        CartItem item = cartItems.get(stt - 1);

        System.out.println("⚠️  Bạn sắp xóa: " + item.getProductName());

        if (InputUtil.inputInt(sc, "Xác nhận xóa (1: Có - 0: Không): ", 0, 1) == 1) {
            boolean success = cartService.removeFromCart(user.getId(), item.getCartItemId());
            System.out.println(success ? "✅ Đã xóa sản phẩm khỏi giỏ hàng!" : "❌ Xóa sản phẩm thất bại!");
        }
    }

    private static void handleClearAllCart(User user) {
        System.out.println("\n⚠️  CẢNH BÁO: Bạn đang xóa TOÀN BỘ giỏ hàng!");
        System.out.println("Tất cả sản phẩm sẽ bị xóa và stock sẽ được hoàn lại.");

        if (InputUtil.inputInt(sc, "Xác nhận xóa toàn bộ giỏ hàng? (1: Có - 0: Không): ", 0, 1) != 1) {
            System.out.println("✅ Đã hủy thao tác.");
            return;
        }

        boolean success = cartService.clearAllCart(user.getId());

        if (success) {
            System.out.println("🗑️  Đã xóa toàn bộ giỏ hàng thành công! Stock đã được hoàn lại vào kho.");
        } else {
            System.out.println("❌ Xóa toàn bộ giỏ hàng thất bại!");
        }
    }

    private static void printCartTable(List<CartItem> cartItems) {
        System.out.println("+-----+---------------------------+--------------------+--------------+-------------------+-------------------+----------+----------------------+");
        System.out.printf("| %-3s | %-25s | %-18s | %-12s | %-17s | %17s | %8s | %20s |\n",
                "STT", "TÊN SẢN PHẨM", "DANH MỤC", "STORAGE", "COLOR", "GIÁ", "SL", "THÀNH TIỀN");
        System.out.println("+-----+---------------------------+--------------------+--------------+-------------------+-------------------+----------+----------------------+");

        int stt = 1;
        for (CartItem item : cartItems) {
            System.out.printf("| %3d | %-25s | %-18s | %-12s | %-17s | %17s | %8d | %20s |\n",
                    stt++,
                    truncate(item.getProductName(), 25),
                    truncate(item.getCategoryName(), 18),
                    truncate(item.getStorage(), 12),
                    truncate(item.getColor(), 17),
                    FormatUtil.formatVND(item.getPrice()),
                    item.getQuantity(),
                    FormatUtil.formatVND(item.getSubtotal()));
        }
        System.out.println("+-----+---------------------------+--------------------+--------------+-------------------+-------------------+----------+----------------------+");
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    // ================= CÁC CHỨC NĂNG KHÁC =================
    public static void checkout(User user) {
        System.out.println("🔄 Chức năng thanh toán đang được phát triển...");
    }

    public static void viewOrderHistory(User user) {
        System.out.println("📜 Chức năng xem lịch sử đơn hàng đang được phát triển...");
    }
}