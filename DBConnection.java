package hotel.management.db;

import java.sql.*;
import javax.swing.JOptionPane;

public class DBConnection {
    // ============================================
    // APNA MySQL PASSWORD YAHAN LIKHEIN
    // ============================================
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "hotel_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Soomro123@";   // <-- apna password
    // ============================================

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private static Connection con = null;

    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "MySQL Driver nahi mila!\nLib folder mein mysql-connector-java.jar add karein.",
                "Driver Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Database connection fail!\n\n" + e.getMessage() +
                "\n\nCheck karein:\n1. MySQL chal raha ho\n2. 'hotel_db' database exist kare\n3. Password sahi ho",
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return con;
    }

    public static void close() {
        try { if (con != null && !con.isClosed()) con.close(); }
        catch (SQLException ignored) {}
    }
}
