package healthhub.dao;

import healthhub.models.Doctor;
import healthhub.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    // ── 1. دالة التأكد من وجود الجدول (أضيفيها هنا) ──
    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS doctors (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "specialization VARCHAR(100)," +
                "phone VARCHAR(20)," +
                "email VARCHAR(100)" +
                ")";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("[DoctorDAO] Table is ready.");
        } catch (SQLException e) {
            System.err.println("[DoctorDAO] Error creating table: " + e.getMessage());
        }
    }

    // ── 2. دالة الإضافة ──
    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (name, specialization, phone, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getSpecialization());
            pstmt.setString(3, doctor.getPhone());
            pstmt.setString(4, doctor.getEmail());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── 3. دالة جلب كل البيانات ──
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                doctors.add(new Doctor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getString("phone"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    // ── 4. دالة الحذف ──
    public boolean deleteDoctor(int id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── 5. دالة التعديل ──
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET name=?, specialization=?, phone=?, email=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getSpecialization());
            pstmt.setString(3, doctor.getPhone());
            pstmt.setString(4, doctor.getEmail());
            pstmt.setInt(5, doctor.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}