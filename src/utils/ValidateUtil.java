package utils;

public class ValidateUtil {

    // Email phải có @gmail.com
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$");
    }

    // Password >=8 ký tự + có ít nhất 1 chữ hoa
    public static boolean isValidPassword(String password) {
        return password != null &&
                password.length() >= 8 &&
                password.matches(".*[A-Z].*");
    }

    // Phone = đúng 10 số
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\d{10}$");
    }
}