package healthhub.views;

import healthhub.dao.AppointmentDAO;
import healthhub.models.Appointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AppointmentsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> patientCombo, doctorCombo, timeCombo;
    private JTextField dateField, notesField;
    private JButton saveButton;
    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    public AppointmentsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.WEST);
        add(buildTable(), BorderLayout.CENTER);
    }
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel title = new JLabel("Appointments");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(0x11529A));
        p.add(title, BorderLayout.WEST);
        return p;
    }

    // ── FORM ──
    private JPanel buildForm() {
        JPanel p = new JPanel(new GridLayout(8, 2, 8, 8));
        p.setBackground(Color.WHITE);
        p.setPreferredSize(new Dimension(320, 0));
        p.setBorder(BorderFactory.createTitledBorder("Book New Appointment"));

        // Patient
        p.add(new JLabel("Patient:"));
        patientCombo = new JComboBox<>(new String[]{
                "1 - Mohamed Ali",
                "2 - Sara Khaled",
                "3 - Omar Youssef"
        });
        p.add(patientCombo);

        // Doctor
        p.add(new JLabel("Doctor:"));
        doctorCombo = new JComboBox<>(new String[]{
                "1 - Dr. Ahmed Hassan",
                "2 - Dr. Mona Salem",
                "3 - Dr. Karim Nabil"
        });
        p.add(doctorCombo);

        // Date
        p.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField("2026-04-30");
        p.add(dateField);

        // Time
        p.add(new JLabel("Time:"));
        timeCombo = new JComboBox<>(new String[]{
                "09:00","09:30","10:00","10:30","11:00","11:30",
                "12:00","12:30","13:00","13:30","14:00","14:30",
                "15:00","15:30","16:00","16:30","17:00","17:30"
        });
        p.add(timeCombo);

        // Notes
        p.add(new JLabel("Notes:"));
        notesField = new JTextField();
        p.add(notesField);

        // Save Button
        p.add(new JLabel(""));
        saveButton = new JButton("Save Appointment");
        saveButton.setBackground(new Color(0x11529A));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.addActionListener(e -> saveAppointment());
        p.add(saveButton);

        return p;
    }
    private JScrollPane buildTable() {
        String[] columns = {"#", "Patient ID", "Doctor ID", "Date", "Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(0x11529A));
        table.getTableHeader().setForeground(Color.WHITE);

        loadData();

        return new JScrollPane(table);
    }

    // ── LOAD DATA FROM DB ──
    private void loadData() {
        tableModel.setRowCount(0);
        List<Appointment> list = appointmentDAO.getAll();
        for (Appointment a : list) {
            tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getPatientId(),
                    a.getDoctorId(),
                    a.getDate(),
                    a.getTime(),
                    a.getStatus()
            });
        }
    }

    // ── SAVE ──
    private void saveAppointment() {
        String date = dateField.getText().trim();
        String time = (String) timeCombo.getSelectedItem();
        String notes = notesField.getText().trim();

        // Patient ID من اول حرف في الـ ComboBox
        int patientId = Integer.parseInt(
                patientCombo.getSelectedItem().toString().split(" - ")[0]
        );
        int doctorId = Integer.parseInt(
                doctorCombo.getSelectedItem().toString().split(" - ")[0]
        );

        // Validation
        if (date.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a date.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (appointmentDAO.hasConflict(doctorId, date, time)) {
            JOptionPane.showMessageDialog(this,
                    "Doctor already booked at this time!",
                    "Conflict Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save
        Appointment a = new Appointment(
                0, patientId, doctorId, date, time, "Scheduled", notes
        );

        if (appointmentDAO.add(a)) {
            JOptionPane.showMessageDialog(this,
                    "Appointment saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save appointment.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}