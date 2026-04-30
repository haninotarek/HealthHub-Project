package healthhub.dao;

import healthhub.models.Appointment;
import healthhub.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    // ── جيب كل الـ Appointments ──
    public List<Appointment> getAll() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments";

        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                list.add(new Appointment(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("status"),
                        rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            System.out.println("getAll error: " + e.getMessage());
        }
        return list;
    }

    // ── أضف Appointment جديد ──
    public boolean add(Appointment a) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, date, time, status, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, a.getPatientId());
            ps.setInt(2, a.getDoctorId());
            ps.setString(3, a.getDate());
            ps.setString(4, a.getTime());
            ps.setString(5, a.getStatus());
            ps.setString(6, a.getNotes());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("add error: " + e.getMessage());
            return false;
        }
    }

    // ── احذف Appointment ──
    public boolean delete(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("delete error: " + e.getMessage());
            return false;
        }
    }

    // ── Conflict Check: هل الدكتور متحجوزه في نفس الوقت؟ ──
    public boolean hasConflict(int doctorId, String date, String time) {
        String sql = "SELECT COUNT(*) FROM appointments " +
                "WHERE doctor_id = ? AND date = ? AND time = ? AND status = 'Scheduled'";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, doctorId);
            ps.setString(2, date);
            ps.setString(3, time);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("conflict check error: " + e.getMessage());
        }
        return false;
    }

    // ── غير Status ──
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("updateStatus error: " + e.getMessage());
            return false;
        }
    }
}