package presentation;

import entity.CartItem;
import entity.User;
import business.service.CartService;
import business.service.OrderService;
import utils.InputUtil;
import utils.FormatUtil;
import entity.Order;
import entity.OrderDetail;
// (CartItem, User, CartService, OrderService, InputUtil, FormatUtil đã có)
import java.util.*;

public class OrderUI {

    private static final CartService cartService = new CartService();
    private static final OrderService orderService = new OrderService();
    private static final Scanner sc = new Scanner(System.in);

    public static void checkout(User user) {
        System.out.println("\n===== 🧾 THANH TOÁN =====");

        List<CartItem> cartItems = cartService.getCartItems(user.getId());

        if (cartItems.isEmpty()) {
            System.out.println("❌ Giỏ hàng trống!");
            return;
        }

        // ===== HIỂN THỊ GIỎ HÀNG =====
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem c = cartItems.get(i);
            System.out.printf("%d. %s | SL: %d | %s\n",
                    i + 1,
                    c.getProductName(),
                    c.getQuantity(),
                    FormatUtil.formatVND(c.getSubtotal()));
        }

        // ===== CHỌN SẢN PHẨM =====
        List<CartItem> selected = new ArrayList<>();

        while (true) {
            int choice = InputUtil.inputInt(sc,
                    "Chọn sản phẩm (0 để dừng): ",
                    0, cartItems.size());

            if (choice == 0) break;

            CartItem item = cartItems.get(choice - 1);

            if (!selected.contains(item)) {
                selected.add(item);
                System.out.println("✅ Đã chọn: " + item.getProductName());
            } else {
                System.out.println("⚠️ Đã chọn rồi: " + item.getProductName());
            }
        }

        if (selected.isEmpty()) {
            System.out.println("❌ Chưa chọn sản phẩm!");
            return;
        }

        // ===== MENU CHỌN KIỂU ĐẶT HÀNG =====
        System.out.println("\n1. Tạo 1 đơn hàng (gộp tất cả)");
        System.out.println("2. Tạo nhiều đơn hàng (mỗi sản phẩm 1 đơn)");

        int option = InputUtil.inputInt(sc,
                "Chọn: ",
                1, 2);

        boolean success = false;

        // ===== OPTION 1: 1 ĐƠN =====
        if (option == 1) {
            double total = selected.stream()
                    .mapToDouble(CartItem::getSubtotal)
                    .sum();

            System.out.println("💰 Tổng tiền: " + FormatUtil.formatVND(total));

            int confirm = InputUtil.inputInt(sc,
                    "Xác nhận mua? (1/0): ",
                    0, 1);

            if (confirm == 1) {
                success = orderService.createOrder(user.getId(), selected);
            }
        }

        // ===== OPTION 2: NHIỀU ĐƠN =====
        else {
            int confirm = InputUtil.inputInt(sc,
                    "Tạo nhiều đơn? (1/0): ",
                    0, 1);

            if (confirm == 1) {
                success = true;

                for (CartItem item : selected) {
                    List<CartItem> single = new ArrayList<>();
                    single.add(item);

                    boolean result = orderService.createOrder(user.getId(), single);

                    if (!result) {
                        success = false;
                    }
                }
            }
        }

        // ===== KẾT QUẢ =====
        if (success) {
            System.out.println("✅ Đặt hàng thành công!");
        } else {
            System.out.println("❌ Có lỗi khi đặt hàng!");
        }
    }
    public static void viewOrderHistory(User user) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                    📋 LỊCH SỬ ĐƠN HÀNG");
        System.out.println("=".repeat(80));

        List<Order> allOrders = orderService.getOrderHistory(user.getId());

        if (allOrders.isEmpty()) {
            System.out.println("❌ Bạn chưa có đơn hàng nào.");
            System.out.println("=".repeat(80));
            return;
        }

        final int ORDERS_PER_PAGE = 2;                    // Số đơn hàng mỗi trang
        int totalOrders = allOrders.size();
        int totalPages = (int) Math.ceil((double) totalOrders / ORDERS_PER_PAGE);
        int currentPage = 1;

        while (true) {
            // Tính vị trí bắt đầu và kết thúc của trang hiện tại
            int fromIndex = (currentPage - 1) * ORDERS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ORDERS_PER_PAGE, totalOrders);
            List<Order> ordersOnPage = allOrders.subList(fromIndex, toIndex);

            // Hiển thị thông tin trang
            System.out.printf("📌 Trang %d / %d   |   Tổng đơn hàng: %d\n\n",
                    currentPage, totalPages, totalOrders);

            // Hiển thị các đơn hàng trên trang hiện tại
            for (Order order : ordersOnPage) {
                System.out.println("🔹 ĐƠN HÀNG #" + order.getId());
                System.out.println("   Ngày đặt     : " + FormatUtil.formatDate(order.getCreatedAt()));
                System.out.println("   Trạng thái   : " + getStatusDisplay(order.getStatus()));
                System.out.println("   Tổng tiền    : " + FormatUtil.formatVND(order.getTotalPrice()));
                System.out.println("   " + "─".repeat(70));

                // Chi tiết sản phẩm
                List<OrderDetail> details = order.getOrderDetails();

                if (details.isEmpty()) {
                    System.out.println("   Không có chi tiết sản phẩm.");
                } else {
                    System.out.println("   Chi tiết sản phẩm:");
                    System.out.println("   " + "─".repeat(75));
                    System.out.printf("   %-4s %-38s %-16s %-18s\n",
                            "SL", "TÊN SẢN PHẨM", "ĐƠN GIÁ", "THÀNH TIỀN");
                    System.out.println("   " + "─".repeat(75));

                    for (OrderDetail detail : details) {
                        double subtotal = detail.getSubtotal();

                        System.out.printf("   %-4d %-38s %s   %s\n",
                                detail.getQuantity(),
                                truncate(detail.getProductName(), 38),
                                FormatUtil.formatVND(detail.getPrice()),
                                FormatUtil.formatVND(subtotal));
                    }
                    System.out.println("   " + "─".repeat(75));
                }

                System.out.println("=".repeat(80));
                System.out.println();   // Khoảng cách giữa các đơn
            }

            // Hiển thị menu phân trang
            System.out.println("=".repeat(80));
            System.out.print("[1] Trang trước    ");
            System.out.print("[2] Trang sau     ");
            System.out.print("[3] Chọn trang    ");
            System.out.println("[0] Quay lại");

            int choice = InputUtil.inputInt(sc, "Chọn chức năng: ", 0, 3);

            switch (choice) {
                case 1 -> { // Trang trước
                    if (currentPage > 1) currentPage--;
                    else System.out.println("⚠️  Bạn đang ở trang đầu tiên!");
                }
                case 2 -> { // Trang sau
                    if (currentPage < totalPages) currentPage++;
                    else System.out.println("⚠️  Bạn đang ở trang cuối cùng!");
                }
                case 3 -> { // Chọn trang
                    int selectedPage = InputUtil.inputInt(sc,
                            "Nhập số trang (1-" + totalPages + "): ", 1, totalPages);
                    currentPage = selectedPage;
                }
                case 0 -> {
                    System.out.println("👋 Quay lại menu chính.");
                    return;
                }
            }
        }
    }
    // ==================== HÀM HỖ TRỢ ====================

    private static String getStatusDisplay(String status) {
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

}