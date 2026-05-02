package healthhub.views;

import healthhub.dao.AppointmentDAO;
import healthhub.dao.DoctorDAO;
import healthhub.dao.PatientDAO;
import healthhub.models.Appointment;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentsPanel extends JPanel {
    private static final Color PRIMARY = new Color(0x11529A);
    private static final Color BG = new Color(0xFAFAFA);
    private static final Color WHITE = Color.WHITE;

    // ألوان الـ Status Badge
    private static final Color STATUS_BG = new Color(0xE8F5E9);
    private static final Color STATUS_TEXT = new Color(0x2E7D32);

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> patientCombo, doctorCombo, timeCombo;
    private JTextField dateField, notesField;
    private JButton saveButton;

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private PatientDAO patientDAO = new PatientDAO();

    public AppointmentsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(BG);
        center.add(buildForm(), BorderLayout.NORTH);
        center.add(buildTableSection(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        loadComboBoxData();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        JLabel title = new JLabel("Appointments Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        p.add(title, BorderLayout.WEST);
        return p;
    }

    private JPanel buildForm() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC2C3C3), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; card.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1; patientCombo = new JComboBox<>(); card.add(patientCombo, gbc);
        gbc.gridx = 2; card.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 3; doctorCombo = new JComboBox<>(); card.add(doctorCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; card.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1; dateField = new JTextField(LocalDate.now().toString()); card.add(dateField, gbc);
        gbc.gridx = 2; card.add(new JLabel("Time:"), gbc);
        gbc.gridx = 3;
        String[] hours = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00"};
        timeCombo = new JComboBox<>(hours); card.add(timeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; card.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; notesField = new JTextField(); card.add(notesField, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1;
        saveButton = new JButton("Save Appointment");
        saveButton.setBackground(PRIMARY);
        saveButton.setForeground(WHITE);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> saveAppointment());
        card.add(saveButton, gbc);
        return card;
    }

    private JPanel buildTableSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setBackground(BG);

        String[] columns = {"#", "Patient Name", "Doctor ID", "Date", "Time", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xEEEEEE)));

        // إصلاح تلوين الـ Status عند التحديد (Selection Fix)
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);

                if (isSelected) {
                    // لو محددة الصف، ياخد لون التحديد الأزرق العادي
                    l.setBackground(table.getSelectionBackground());
                    l.setForeground(table.getSelectionForeground());
                } else {
                    // لو مش محددة، يظهر اللون الأخضر
                    if ("Scheduled".equals(value)) {
                        l.setBackground(STATUS_BG);
                        l.setForeground(STATUS_TEXT);
                    } else {
                        l.setBackground(WHITE);
                        l.setForeground(Color.BLACK);
                    }
                }
                l.setOpaque(true);
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        scroll.getViewport().setBackground(WHITE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(BG);

        JButton editBtn = new JButton("Edit");
        styleButton(editBtn, new Color(0x11529A), false);
        editBtn.addActionListener(e -> prepareEdit()); // تشغيل زرار الـ Edit

        JButton deleteBtn = new JButton("Delete");
        styleButton(deleteBtn, new Color(0xD32F2F), true);
        deleteBtn.addActionListener(e -> deleteAppointment());

        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        wrapper.add(scroll, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);

        loadData();
        return wrapper;
    }

    private void styleButton(JButton btn, Color color, boolean solid) {
        btn.setPreferredSize(new Dimension(100, 32));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        if (solid) {
            btn.setBackground(color);
            btn.setForeground(WHITE);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
        } else {
            btn.setBackground(WHITE);
            btn.setForeground(color);
            btn.setBorder(BorderFactory.createLineBorder(color));
            btn.setContentAreaFilled(false);
        }
    }

    private void loadComboBoxData() {
        patientCombo.removeAllItems();
        patientDAO.getAllPatients().forEach(p -> patientCombo.addItem(p.getId() + " - " + p.getName()));
        doctorCombo.removeAllItems();
        doctorDAO.getAllDoctors().forEach(d -> doctorCombo.addItem(d.getId() + " - " + d.getName()));
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Appointment> list = appointmentDAO.getAll();
        for (Appointment a : list) {
            String pName = patientDAO.getPatientById(a.getPatientId()).getName();
            tableModel.addRow(new Object[]{
                    a.getId(), pName, a.getDoctorId(), a.getDate(), a.getTime(), a.getStatus(), a.getNotes()
            });
        }
    }

    // دالة لتجهيز البيانات للتعديل
    private void prepareEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to edit!");
            return;
        }

        String patientName = tableModel.getValueAt(row, 1).toString();
        String doctorId = tableModel.getValueAt(row, 2).toString();
        String date = tableModel.getValueAt(row, 3).toString();
        String time = tableModel.getValueAt(row, 4).toString();
        String notes = tableModel.getValueAt(row, 6).toString();

        // ضبط الـ Patient Combo
        for (int i = 0; i < patientCombo.getItemCount(); i++) {
            if (patientCombo.getItemAt(i).contains(patientName)) {
                patientCombo.setSelectedIndex(i);
                break;
            }
        }

        // ضبط الـ Doctor Combo
        for (int i = 0; i < doctorCombo.getItemCount(); i++) {
            if (doctorCombo.getItemAt(i).startsWith(doctorId + " -")) {
                doctorCombo.setSelectedIndex(i);
                break;
            }
        }

        dateField.setText(date);
        timeCombo.setSelectedItem(time);
        notesField.setText(notes);

        JOptionPane.showMessageDialog(this, "Data loaded into the form. Modify and click Save.");
    }

    private void saveAppointment() {
        try {
            int pId = Integer.parseInt(patientCombo.getSelectedItem().toString().split(" - ")[0]);
            int dId = Integer.parseInt(doctorCombo.getSelectedItem().toString().split(" - ")[0]);
            LocalDate d = LocalDate.parse(dateField.getText());
            LocalTime t = LocalTime.parse(timeCombo.getSelectedItem().toString());

            if (appointmentDAO.hasConflict(dId, d, t)) {
                JOptionPane.showMessageDialog(this, "Conflict: Doctor is busy!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (appointmentDAO.add(new healthhub.models.Appointment(0, pId, dId, d, t, "Scheduled", notesField.getText()))) {
                JOptionPane.showMessageDialog(this, "Saved Successfully!");
                loadData();
                notesField.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Check date format!");
        }
    }

    private void deleteAppointment() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to delete!");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this?");
        if (confirm == JOptionPane.YES_OPTION) {
            if (appointmentDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "Deleted!");
                loadData();
            }
        }
    }
}