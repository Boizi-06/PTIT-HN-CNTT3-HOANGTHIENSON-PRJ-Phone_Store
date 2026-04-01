package utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FormatUtil {

    // Múi giờ Việt Nam
    private static final TimeZone VN_TIMEZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");

    private static final SimpleDateFormat DATE_TIME_FORMAT_VN = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private static final SimpleDateFormat DATE_FORMAT_VN = new SimpleDateFormat("dd/MM/yyyy");

    static {
        // Áp dụng múi giờ Việt Nam cho tất cả formatter
        DATE_TIME_FORMAT_VN.setTimeZone(VN_TIMEZONE);
        DATE_FORMAT_VN.setTimeZone(VN_TIMEZONE);
    }

    /**
     * Format số tiền sang VND
     */
    public static String formatVND(double price) {
        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        return vn.format(price) + " VNĐ";
    }

    /**
     * Format ngày giờ theo múi giờ Việt Nam (dd/MM/yyyy HH:mm)
     * Ví dụ: 31/03/2026 15:05
     */
    public static String formatDate(java.sql.Timestamp timestamp) {
        if (timestamp == null) {
            return "N/A";
        }
        return DATE_TIME_FORMAT_VN.format(new Date(timestamp.getTime()));
    }

    /**
     * Format chỉ ngày theo múi giờ Việt Nam (dd/MM/yyyy)
     */
    public static String formatDateOnly(java.sql.Timestamp timestamp) {
        if (timestamp == null) {
            return "N/A";
        }
        return DATE_FORMAT_VN.format(new Date(timestamp.getTime()));
    }

    /**
     * Format ngày giờ với pattern tùy chỉnh theo múi giờ Việt Nam
     */
    public static String formatDate(java.sql.Timestamp timestamp, String pattern) {
        if (timestamp == null) {
            return "N/A";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(VN_TIMEZONE);
        return sdf.format(new Date(timestamp.getTime()));
    }
}