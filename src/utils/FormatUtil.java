package utils;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtil {
    public static String formatVND(double price) {
        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        return vn.format(price) + " VNĐ";
    }
}