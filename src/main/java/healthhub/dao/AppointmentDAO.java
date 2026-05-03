package healthhub.dao;

import healthhub.models.Appointment;
import healthhub.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public List<Appointment> getAll() {
        List<Appointment> list = new ArrayList<>();
        // التعديل هنا: عملنا JOIN عشان نجيب الأسامي من الجداول التانية
        String sql = "SELECT a.*, p.name AS patient_name, d.name AS doctor_name " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN doctors d ON a.doctor_id = d.id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Appointment app = new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getObject("date", java.time.LocalDate.class),
                        rs.getObject("time", java.time.LocalTime.class),
                        rs.getString("status"),
                        rs.getString("notes")
                );
                // بنستخدم الـ notes مؤقتاً عشان نخزن أسامي العرض لو محتاجين،
                // بس إحنا هنعرض الأسامي مباشرة في الـ Panel
                list.add(app);
            }
        } catch (SQLException e) {
            System.err.println("getAll error: " + e.getMessage());
        }
        return list;
    }

    public boolean add(Appointment a) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, date, time, status, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setObject(3, a.getDate());
            ps.setObject(4, a.getTime());
            ps.setString(5, a.getStatus());
            ps.setString(6, a.getNotes());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("add error: " + e.getMessage());
            return false;
        }
    }

    public boolean hasConflict(int doctorId, java.time.LocalDate date, java.time.LocalTime time) {
        // المقارنة بتتم عن طريق تحويل الوقت لنص HH:mm عشان SQL Server يقارن الساعة بدقة
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND date = ? " +
                "AND LEFT(CONVERT(varchar, time, 108), 5) = ? AND status = 'Scheduled'";
        try (Connection con = healthhub.utils.DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setString(3, time.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Conflict Check Error: " + e.getMessage());
        }
        return false;
    }
    public boolean delete(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("delete error: " + e.getMessage());
            return false;
        }
    }
}