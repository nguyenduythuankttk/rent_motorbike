package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL  = "jdbc:mysql://localhost:3306/java_motor_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Ho_Chi_Minh";
    private static final String USER = "root";
    private static final String PASS = "19082173"; // Cập nhật mật khẩu MySQL của bạn

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
            return null;
        }
    }
}
