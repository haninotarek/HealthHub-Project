package healthhub.dao;

import healthhub.models.Patient;
import healthhub.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    // -----------------------------------------------
    // CREATE TABLE (run once at app startup)
    // -----------------------------------------------
    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS patients (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "phone VARCHAR(20)," +
                "age INT," +
                "gender VARCHAR(10)" +
                ")";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("[PatientDAO] Table is ready.");

        } catch (SQLException e) {
            System.err.println("[PatientDAO] Error creating table: " + e.getMessage());
        }
    }

    // -----------------------------------------------
    // GET ALL PATIENTS
    // -----------------------------------------------
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(new Patient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getInt("age"),
                        rs.getString("gender")
                ));
            }

        } catch (SQLException e) {
            System.err.println("[PatientDAO] Error fetching patients: " + e.getMessage());
        }

        return patients;
    }

    // -----------------------------------------------
    // SEARCH BY NAME
    // -----------------------------------------------
    public List<Patient> searchPatientsByName(String keyword) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE name LIKE ? ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(new Patient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getInt("age"),
                            rs.getString("gender")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("[PatientDAO] Error searching patients: " + e.getMessage());
        }

        return patients;
    }

    // -----------------------------------------------
    // ADD PATIENT
    // -----------------------------------------------
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (name, phone, age, gender) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getName());
            pstmt.setString(2, patient.getPhone());
            pstmt.setInt(3, patient.getAge());
            pstmt.setString(4, patient.getGender());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[PatientDAO] Error adding patient: " + e.getMessage());
            return false;
        }
    }

    // -----------------------------------------------
    // UPDATE PATIENT
    // -----------------------------------------------
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET name=?, phone=?, age=?, gender=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getName());
            pstmt.setString(2, patient.getPhone());
            pstmt.setInt(3, patient.getAge());
            pstmt.setString(4, patient.getGender());
            pstmt.setInt(5, patient.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[PatientDAO] Error updating patient: " + e.getMessage());
            return false;
        }
    }

    // -----------------------------------------------
    // DELETE PATIENT
    // -----------------------------------------------
    public boolean deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[PatientDAO] Error deleting patient: " + e.getMessage());
            return false;
        }
    }

    // -----------------------------------------------
    // COUNT PATIENTS
    // -----------------------------------------------
    public int getPatientCount() {
        String sql = "SELECT COUNT(*) FROM patients";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("[PatientDAO] Error counting patients: " + e.getMessage());
        }

        return 0;
    }
    public Patient getPatientById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // التعديل هنا: بنبعت البيانات بالترتيب اللي الـ Constructor محتاجه
                    // (id, name, phone, age, gender)
                    return new Patient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getInt("age"),
                            rs.getString("gender")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("getPatientById error: " + e.getMessage());
        }
        return null;
    }
}