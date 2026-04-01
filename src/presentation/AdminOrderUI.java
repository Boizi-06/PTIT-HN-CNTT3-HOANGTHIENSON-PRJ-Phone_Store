package presentation;

import business.service.OrderService;
import entity.Order;
import entity.OrderDetail;
import utils.FormatUtil;
import utils.InputUtil;

import java.util.List;
import java.util.Scanner;

public class AdminOrderUI {

    private static final OrderService orderService = new OrderService();
    private static final Scanner sc = new Scanner(System.in);

    // ==================== MÀU SẮC ANSI ====================
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";

    public static void manageOrders() {
        while (true) {
            printHeader("👨‍💼 QUẢN LÝ ĐƠN HÀNG (ADMIN)");

            System.out.println(" " + CYAN + "1." + RESET + " Xem tất cả đơn hàng");
            System.out.println(" " + CYAN + "2." + RESET + " Cập nhật trạng thái đơn hàng");
            System.out.println(" " + RED + "0." + RESET + " Quay lại menu Admin");

            int choice = InputUtil.inputInt(sc, BOLD + "Chọn chức năng: " + RESET, 0, 2);

            switch (choice) {
                case 1 -> viewAllOrders();
                case 2 -> updateOrderStatus();
                case 0 -> {
                    System.out.println(GREEN + "👋 Đã quay lại menu Admin." + RESET);
                    return;
                }
                default -> System.out.println(RED + "❌ Lựa chọn không hợp lệ!" + RESET);
            }
        }
    }

    // ==================== XEM TẤT CẢ ĐƠN HÀNG ====================
    private static void viewAllOrders() {
        List<Order> allOrders = orderService.getAllOrdersWithDetails();
        if (allOrders.isEmpty()) {
            System.out.println(RED + "❌ Hiện tại chưa có đơn hàng nào." + RESET);
            return;
        }

        final int ORDERS_PER_PAGE = 5;
        int totalPages = (int) Math.ceil((double) allOrders.size() / ORDERS_PER_PAGE);
        int currentPage = 1;

        while (true) {
            int from = (currentPage - 1) * ORDERS_PER_PAGE;
            int to = Math.min(from + ORDERS_PER_PAGE, allOrders.size());
            List<Order> pageOrders = allOrders.subList(from, to);

            printHeader("📋 DANH SÁCH ĐƠN HÀNG - Trang " + currentPage + " / " + totalPages
                    + " (Tổng: " + allOrders.size() + " đơn)");

            for (Order order : pageOrders) {
                printOrderDetail(order);
            }

            System.out.println(" " + CYAN + "[1]" + RESET + " Trang trước   "
                    + CYAN + "[2]" + RESET + " Trang sau   "
                    + CYAN + "[3]" + RESET + " Chọn trang   "
                    + RED + "[0]" + RESET + " Quay lại");

            int action = InputUtil.inputInt(sc, BOLD + "Chọn: " + RESET, 0, 3);

            switch (action) {
                case 1 -> { if (currentPage > 1) currentPage--; else System.out.println(YELLOW + "⚠️ Đang ở trang đầu!" + RESET); }
                case 2 -> { if (currentPage < totalPages) currentPage++; else System.out.println(YELLOW + "⚠️ Đang ở trang cuối!" + RESET); }
                case 3 -> {
                    int chosen = InputUtil.inputInt(sc, "Nhập số trang (1-" + totalPages + "): ", 1, totalPages);
                    currentPage = chosen;
                }
                case 0 -> { return; }
            }
        }
    }

    // ==================== CẬP NHẬT TRẠNG THÁI ====================
    private static void updateOrderStatus() {
        printHeader("🔄 CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG");

        List<Order> allOrders = orderService.getAllOrders();
        if (allOrders.isEmpty()) {
            System.out.println(RED + "❌ Chưa có đơn hàng nào để cập nhật." + RESET);
            return;
        }

        // Bảng danh sách ngắn gọn để chọn ID
        System.out.println(BOLD + "ID     | Ngày tạo           | Trạng thái               | Tổng tiền" + RESET);
        System.out.println("─".repeat(85));
        for (Order o : allOrders) {
            System.out.printf("%-6d | %s | %s | %s\n",
                    o.getId(),
                    FormatUtil.formatDate(o.getCreatedAt()),
                    getStatusDisplayPlain(o.getStatus()),
                    FormatUtil.formatVND(o.getTotalPrice()));
        }
        System.out.println("─".repeat(85));

        int orderId = InputUtil.inputInt(sc, BOLD + "\nNhập ID đơn hàng cần cập nhật (0 để hủy): " + RESET, 0, 999999);
        if (orderId == 0) return;

        Order selectedOrder = orderService.getFullOrderById(orderId);
        if (selectedOrder == null) {
            System.out.println(RED + "❌ Không tìm thấy đơn hàng với ID này!" + RESET);
            return;
        }

        System.out.println("\n" + BOLD + "THÔNG TIN ĐƠN HÀNG CẦN CẬP NHẬT:" + RESET);
        printOrderDetail(selectedOrder);   // ← Dùng chung bảng đẹp

        // Chọn trạng thái mới
        System.out.println("\n" + BOLD + "Chọn trạng thái mới:" + RESET);
        System.out.println(" " + YELLOW + "1." + RESET + " PENDING    (Chờ xử lý)");
        System.out.println(" " + BLUE + "2." + RESET + " SHIPPING   (Đang giao hàng)");
        System.out.println(" " + GREEN + "3." + RESET + " DELIVERED  (Đã giao thành công)");
        System.out.println(" " + RED + "4." + RESET + " CANCELLED  (Đã hủy)");

        int choice = InputUtil.inputInt(sc, BOLD + "Chọn: " + RESET, 1, 4);
        String newStatus = switch (choice) {
            case 1 -> "PENDING";
            case 2 -> "SHIPPING";
            case 3 -> "DELIVERED";
            case 4 -> "CANCELLED";
            default -> null;
        };

        if (newStatus == null) return;

        if (!isValidTransition(selectedOrder.getStatus(), newStatus)) {
            System.out.println(RED + "❌ Không được phép chuyển từ "
                    + getStatusDisplay(selectedOrder.getStatus())
                    + " sang " + getStatusDisplay(newStatus) + RESET);
            return;
        }

        System.out.println("\nBạn muốn đổi trạng thái đơn #" + orderId
                + " từ " + getStatusDisplay(selectedOrder.getStatus())
                + " sang " + getStatusDisplay(newStatus) + " ?");

        int confirm = InputUtil.inputInt(sc, BOLD + "Xác nhận (1: Có - 0: Không): " + RESET, 0, 1);

        if (confirm == 1) {
            boolean success = orderService.updateOrderStatus(orderId, newStatus);
            if (success) {
                System.out.println(GREEN + "✅ Cập nhật trạng thái thành công!" + RESET);

                Order updatedOrder = orderService.getFullOrderById(orderId);
                if (updatedOrder != null) {
                    System.out.println("\n" + BOLD + "THÔNG TIN SAU KHI CẬP NHẬT:" + RESET);
                    printOrderDetail(updatedOrder);   // ← Dùng chung bảng đẹp
                }
            } else {
                System.out.println(RED + "❌ Cập nhật thất bại!" + RESET);
            }
        } else {
            System.out.println(YELLOW + "Đã hủy thao tác." + RESET);
        }
    }

    // ==================== IN CHI TIẾT ĐƠN HÀNG (ĐÃ LÀM ĐẸP HƠN) ====================
    private static void printOrderDetail(Order order) {
        if (order == null) return;

        System.out.println(BOLD + "╔" + "═".repeat(95) + "╗" + RESET);
        System.out.printf(BOLD + "║ " + PURPLE + "ĐƠN HÀNG #%d" + RESET
                        + " | Khách: %-22s | Ngày: %s" + BOLD + " ║\n" + RESET,
                order.getId(),
                order.getCustomerName() != null ? order.getCustomerName() : "Khách #" + order.getUserId(),
                FormatUtil.formatDate(order.getCreatedAt()));

        System.out.printf(BOLD + "║ Trạng thái: " + RESET + "%s"
                        + BOLD + " | Tổng tiền: " + RESET + "%s" + BOLD + " ║\n" + RESET,
                getStatusDisplay(order.getStatus()),
                FormatUtil.formatVND(order.getTotalPrice()));

        List<OrderDetail> details = order.getOrderDetails();
        if (!details.isEmpty()) {
            System.out.println(BOLD + "╠" + "═".repeat(95) + "╣" + RESET);
            System.out.println(BOLD + "║ " + BLUE + "CHI TIẾT SẢN PHẨM" + RESET + BOLD + " ".repeat(78) + "║" + RESET);
            System.out.println(BOLD + "╠" + "─".repeat(95) + "╣" + RESET);
            System.out.println(BOLD + "║   " + "Tên sản phẩm" + " ".repeat(28)
                    + "Số lượng" + " ".repeat(8) + "Thành tiền" + BOLD + " ║" + RESET);

            for (OrderDetail d : details) {
                System.out.printf(BOLD + "║   %-35s %-8d %s" + BOLD + " ║\n" + RESET,
                        truncate(d.getProductName(), 35),
                        d.getQuantity(),
                        FormatUtil.formatVND(d.getPrice() * d.getQuantity()));
            }
        } else {
            System.out.println(BOLD + "║ " + YELLOW + "Không có chi tiết sản phẩm" + BOLD + " ".repeat(68) + "║" + RESET);
        }

        System.out.println(BOLD + "╚" + "═".repeat(95) + "╝" + RESET);
        System.out.println();
    }

    // ==================== HÀM HỖ TRỢ ====================
    private static void printHeader(String title) {
        System.out.println();
        System.out.println(BOLD + CYAN + "═".repeat(95) + RESET);
        System.out.println(BOLD + CYAN + "  " + title + RESET);
        System.out.println(BOLD + CYAN + "═".repeat(95) + RESET);
    }

    private static String getStatusDisplay(String status) {
        if (status == null) return YELLOW + "❓ Không xác định" + RESET;
        return switch (status.toUpperCase()) {
            case "PENDING"   -> YELLOW + "🟡 Chờ xử lý" + RESET;
            case "SHIPPING"  -> BLUE + "🔵 Đang giao" + RESET;
            case "DELIVERED" -> GREEN + "🟢 Đã giao thành công" + RESET;
            case "CANCELLED" -> RED + "🔴 Đã hủy" + RESET;
            default          -> status;
        };
    }

    private static String getStatusDisplayPlain(String status) {
        if (status == null) return "Không xác định";
        return switch (status.toUpperCase()) {
            case "PENDING"   -> "🟡 Chờ xử lý";
            case "SHIPPING"  -> "🔵 Đang giao";
            case "DELIVERED" -> "🟢 Đã giao";
            case "CANCELLED" -> "🔴 Đã hủy";
            default          -> status;
        };
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    private static boolean isValidTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) return false;
        String current = currentStatus.toUpperCase().trim();
        String next = newStatus.toUpperCase().trim();
        return switch (current) {
            case "PENDING"  -> next.equals("SHIPPING") || next.equals("CANCELLED");
            case "SHIPPING" -> next.equals("DELIVERED");
            case "DELIVERED", "CANCELLED" -> false;
            default -> false;
        };
    }
}