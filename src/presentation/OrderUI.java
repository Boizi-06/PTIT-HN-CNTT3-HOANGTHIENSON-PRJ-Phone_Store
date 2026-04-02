package presentation;

import entity.CartItem;
import entity.Order;
import entity.OrderDetail;
import entity.User;
import business.service.CartService;
import business.service.OrderService;
import utils.InputUtil;
import utils.FormatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderUI {

    private static final CartService cartService = new CartService();
    private static final OrderService orderService = new OrderService();
    private static final Scanner sc = new Scanner(System.in);

    // ==================== ANSI COLORS ====================
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";
    private static final String BLUE = "\u001B[34m";

    /**
     * Thanh toán - Chỉ tạo MỘT đơn hàng gộp tất cả sản phẩm
     */
    public static void checkout(User user) {
        clearScreen();
        printHeader("🧾 THANH TOÁN ĐƠN HÀNG");

        List<CartItem> cartItems = cartService.getCartItems(user.getId());

        if (cartItems.isEmpty()) {
            System.out.println(YELLOW + "⚠️  Giỏ hàng của bạn đang trống!" + RESET);
            pause();
            return;
        }

        // Hiển thị giỏ hàng
        System.out.println(BOLD + BLUE + "📋 DANH SÁCH SẢN PHẨM TRONG GIỎ HÀNG" + RESET);
        System.out.println("═".repeat(85));

        double totalAmount = 0.0;

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            double subtotal = item.getSubtotal();
            totalAmount += subtotal;

            System.out.printf("  %2d. %-40s × %3d = %s%n",
                    i + 1,
                    truncate(item.getProductName(), 40),
                    item.getQuantity(),
                    FormatUtil.formatVND(subtotal));
        }

        System.out.println("═".repeat(85));
        System.out.println(BOLD + GREEN + "TỔNG CỘNG: " + FormatUtil.formatVND(totalAmount) + RESET);
        System.out.println();

        // Xác nhận đặt hàng
        int confirm = InputUtil.inputInt(sc,
                BOLD + YELLOW + "Xác nhận tạo đơn hàng với tất cả sản phẩm trên? (1: Có - 0: Không): " + RESET,
                0, 1);

        if (confirm != 1) {
            System.out.println(YELLOW + "🚫 Đã hủy đặt hàng." + RESET);
            pause();
            return;
        }

        // Tạo đơn hàng (gộp tất cả)
        boolean success = orderService.createOrder(user.getId(), cartItems);

        if (success) {
            System.out.println(GREEN + "✅ Đặt hàng thành công! Cảm ơn bạn đã mua hàng." + RESET);
            System.out.println("   Đơn hàng của bạn đang ở trạng thái: 🟡 Chờ xử lý");
        } else {
            System.out.println(RED + "❌ Đặt hàng thất bại! Vui lòng thử lại sau." + RESET);
        }

        pause();
    }

    /**
     * Xem lịch sử đơn hàng (đã làm đẹp)
     */
    public static void viewOrderHistory(User user) {
        clearScreen();
        printHeader("📋 LỊCH SỬ ĐƠN HÀNG");

        List<Order> allOrders = orderService.getOrderHistory(user.getId());

        if (allOrders.isEmpty()) {
            System.out.println(YELLOW + "⚠️  Bạn chưa có đơn hàng nào." + RESET);
            pause();
            return;
        }

        final int ORDERS_PER_PAGE = 2;
        int totalOrders = allOrders.size();
        int totalPages = (int) Math.ceil((double) totalOrders / ORDERS_PER_PAGE);
        int currentPage = 1;

        while (true) {
            int fromIndex = (currentPage - 1) * ORDERS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ORDERS_PER_PAGE, totalOrders);
            List<Order> ordersOnPage = allOrders.subList(fromIndex, toIndex);

            System.out.printf(BOLD + BLUE + "📌 Trang %d / %d   |   Tổng đơn hàng: %d%n%n" + RESET,
                    currentPage, totalPages, totalOrders);

            for (Order order : ordersOnPage) {
                System.out.println("🔹 ĐƠN HÀNG #" + order.getId());
                System.out.println("   Ngày đặt     : " + FormatUtil.formatDate(order.getCreatedAt()));
                System.out.println("   Trạng thái   : " + getStatusDisplay(order.getStatus()));
                System.out.println("   Tổng tiền    : " + FormatUtil.formatVND(order.getTotalPrice()));
                System.out.println("   " + "─".repeat(75));

                List<OrderDetail> details = order.getOrderDetails();

                if (details == null || details.isEmpty()) {
                    System.out.println("   Không có chi tiết sản phẩm.");
                } else {
                    System.out.println("   Chi tiết sản phẩm:");
                    System.out.println("   " + "─".repeat(80));
                    System.out.printf("   %-4s %-40s %-15s %-18s%n",
                            "SL", "TÊN SẢN PHẨM", "ĐƠN GIÁ", "THÀNH TIỀN");
                    System.out.println("   " + "─".repeat(80));

                    for (OrderDetail detail : details) {
                        System.out.printf("   %-4d %-40s %s   %s%n",
                                detail.getQuantity(),
                                truncate(detail.getProductName(), 40),
                                FormatUtil.formatVND(detail.getPrice()),
                                FormatUtil.formatVND(detail.getSubtotal()));
                    }
                    System.out.println("   " + "─".repeat(80));
                }
                System.out.println("=".repeat(85));
                System.out.println();
            }

            System.out.println("[1] Trang trước    [2] Trang sau    [3] Chọn trang    [0] Quay lại");
            int choice = InputUtil.inputInt(sc, BOLD + "Chọn chức năng: " + RESET, 0, 3);

            switch (choice) {
                case 1 -> {
                    if (currentPage > 1) currentPage--;
                    else System.out.println(RED + "⚠️  Bạn đang ở trang đầu tiên!" + RESET);
                }
                case 2 -> {
                    if (currentPage < totalPages) currentPage++;
                    else System.out.println(RED + "⚠️  Bạn đang ở trang cuối cùng!" + RESET);
                }
                case 3 -> {
                    int selectedPage = InputUtil.inputInt(sc,
                            "Nhập số trang (1-" + totalPages + "): ", 1, totalPages);
                    currentPage = selectedPage;
                }
                case 0 -> {
                    System.out.println(GREEN + "👋 Quay lại menu chính." + RESET);
                    return;
                }
            }
        }
    }

    // ==================== HÀM HỖ TRỢ ====================
    private static String getStatusDisplay(String status) {
        if (status == null) return "Không xác định";
        return switch (status.toUpperCase()) {
            case "PENDING"   -> "🟡 Chờ xử lý";
            case "SHIPPING"  -> "🔵 Đang giao hàng";
            case "DELIVERED" -> "🟢 Đã giao hàng";
            case "CANCELLED" -> "🔴 Đã hủy";
            default          -> status;
        };
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    private static void printHeader(String title) {
        System.out.println(BOLD + PURPLE + "╔" + "═".repeat(70) + "╗" + RESET);
        System.out.println(BOLD + PURPLE + "║" + RESET + " ".repeat(25) + BOLD + YELLOW + title + RESET + " ".repeat(20) + BOLD + PURPLE + "║" + RESET);
        System.out.println(BOLD + PURPLE + "╚" + "═".repeat(70) + "╝" + RESET);
        System.out.println();
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void pause() {
        System.out.println("\n" + YELLOW + "Nhấn Enter để tiếp tục..." + RESET);
        sc.nextLine();
    }
}