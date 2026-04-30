package healthhub.dao;

import healthhub.models.User;
import healthhub.utils.DBConnection;
import java.sql.*;

public class UserDAO {

    // دالة تسجيل الدخول
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"), // تأكد من ترتيب البرامترات حسب كلاس User
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // دالة البحث عن مستخدم بالاسم (المطلوبة في AuthService)
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding user: " + e.getMessage());
        }
        return null;
    }
}