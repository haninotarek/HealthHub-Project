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

public class AppointmentsPanel extends JPanel {

    private static final Color PRIMARY    = new Color(0x11529A);
    private static final Color BG         = new Color(0xF4F6F8);
    private static final Color WHITE      = Color.WHITE;

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> patientCombo, doctorCombo, timeCombo, statusCombo;
    private JTextField dateField, notesField;
    private int editingAppointmentId = -1;

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private DoctorDAO      doctorDAO      = new DoctorDAO();
    private PatientDAO     patientDAO     = new PatientDAO();

    public AppointmentsPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(buildTopBar(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(buildForm(), BorderLayout.NORTH);
        center.add(buildTableSection(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        loadComboBoxData();
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        JLabel title = new JLabel("Appointments Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        bar.add(title, BorderLayout.WEST);
        return bar;
    }

    private JPanel buildForm() {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(220, 220, 220));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.weightx = 1.0;

        // Row 1
        gbc.gridy = 1; gbc.gridx = 0; card.add(fieldLabel("Patient"), gbc);
        gbc.gridx = 2; card.add(fieldLabel("Doctor"), gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        patientCombo = new JComboBox<>(); styleCombo(patientCombo); card.add(patientCombo, gbc);
        gbc.gridx = 2;
        doctorCombo = new JComboBox<>(); styleCombo(doctorCombo); card.add(doctorCombo, gbc);

        // Row 3
        gbc.gridy = 3; gbc.gridx = 0; card.add(fieldLabel("Date (YYYY-MM-DD)"), gbc);
        gbc.gridx = 2; card.add(fieldLabel("Time"), gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        dateField = createField(); dateField.setText(LocalDate.now().toString()); card.add(dateField, gbc);
        gbc.gridx = 2;
        String[] hours = {"09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00"};
        timeCombo = new JComboBox<>(hours); styleCombo(timeCombo); card.add(timeCombo, gbc);

        // Row 5 - Notes & Status
        gbc.gridy = 5; gbc.gridx = 0; card.add(fieldLabel("Notes"), gbc);
        gbc.gridx = 2; card.add(fieldLabel("Status"), gbc);

        gbc.gridy = 6; gbc.gridx = 0;
        notesField = createField(); card.add(notesField, gbc);
        gbc.gridx = 2;
        statusCombo = new JComboBox<>(new String[]{"Scheduled", "Completed", "Cancelled"});
        styleCombo(statusCombo); card.add(statusCombo, gbc);

        // Buttons
        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 4;
        gbc.insets = new Insets(20, 8, 5, 8);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnBook = createStyledButton("Book", new Color(235, 255, 235), new Color(40, 167, 69));
        JButton btnUpdate = createStyledButton("Update Status", new Color(235, 245, 255), PRIMARY);
        JButton btnDelete = createStyledButton("Delete", new Color(255, 235, 235), new Color(0xD32F2F));

        btnBook.addActionListener(e -> saveAppointment());
        btnUpdate.addActionListener(e -> updateAppointment());
        btnDelete.addActionListener(e -> deleteAppointment());

        btnPanel.add(btnBook); btnPanel.add(btnUpdate); btnPanel.add(btnDelete);
        card.add(btnPanel, gbc);

        return card;
    }

    private JPanel buildTableSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        String[] columns = {"#", "Patient Name", "Doctor ID", "Date", "Time", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xF0F0F0));

        // 1. توسيط جميع الأعمدة
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // 2. تلوين عمود الـ Status وتوسيطه
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (!sel) {
                    String status = (val != null) ? val.toString() : "";
                    switch (status) {
                        case "Completed": l.setBackground(new Color(0xE8F5E9)); l.setForeground(new Color(0x2E7D32)); break;
                        case "Scheduled": l.setBackground(new Color(0xE3F2FD)); l.setForeground(new Color(0x1565C0)); break;
                        case "Cancelled": l.setBackground(new Color(0xFFEBEE)); l.setForeground(new Color(0xC62828)); break;
                        default: l.setBackground(WHITE); l.setForeground(Color.BLACK);
                    }
                }
                l.setOpaque(true);
                return l;
            }
        });

        // تنسيق الهيدر
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setBackground(PRIMARY); l.setForeground(WHITE);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setHorizontalAlignment(JLabel.CENTER); l.setOpaque(true);
                return l;
            }
        });

        // Selection Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                editingAppointmentId = (int) tableModel.getValueAt(row, 0);
                dateField.setText(tableModel.getValueAt(row, 3).toString());
                timeCombo.setSelectedItem(tableModel.getValueAt(row, 4).toString());
                statusCombo.setSelectedItem(tableModel.getValueAt(row, 5).toString());
                notesField.setText(tableModel.getValueAt(row, 6).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scroll.getViewport().setBackground(WHITE);
        wrapper.add(scroll, BorderLayout.CENTER);

        loadData();
        return wrapper;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    private JTextField createField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.BOLD, 13));
        f.setPreferredSize(new Dimension(0, 38));
        return f;
    }

    private void styleCombo(JComboBox<String> c) {
        c.setFont(new Font("Segoe UI", Font.BOLD, 13));
        c.setPreferredSize(new Dimension(0, 38));
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(140, 38));
        btn.setBackground(bg); btn.setForeground(fg);
        return btn;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        appointmentDAO.getAll().forEach(a -> {
            String pName = patientDAO.getPatientById(a.getPatientId()).getName();
            tableModel.addRow(new Object[]{a.getId(), pName, a.getDoctorId(), a.getDate(), a.getTime(), a.getStatus(), a.getNotes()});
        });
    }

    private void saveAppointment() {
        try {
            // 1. استخراج البيانات من الفورم
            int pId = Integer.parseInt(patientCombo.getSelectedItem().toString().split(" - ")[0]);
            int dId = Integer.parseInt(doctorCombo.getSelectedItem().toString().split(" - ")[0]);
            LocalDate date = LocalDate.parse(dateField.getText());
            LocalTime time = LocalTime.parse(timeCombo.getSelectedItem().toString());
            String notes = notesField.getText();

            // 2. التحقق من تضارب المواعيد (Checking for Conflict)
            if (appointmentDAO.hasConflict(dId, date, time)) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ This doctor already has an appointment at this time!",
                        "Schedule Conflict",
                        JOptionPane.WARNING_MESSAGE);
                return; // وقف الكود هنا ومتحجزش
            }

            // 3. لو مفيش تعارض، كمل الحجز عادي
            Appointment app = new Appointment(0, pId, dId, date, time, "Scheduled", notes);
            if (appointmentDAO.add(app)) {
                JOptionPane.showMessageDialog(this, "Appointment Booked Successfully!");
                loadData();
                clearForm();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Please check date format (YYYY-MM-DD)");
        }
    }

    private void updateAppointment() {
        if (editingAppointmentId == -1) return;
        try {
            int pId = Integer.parseInt(patientCombo.getSelectedItem().toString().split(" - ")[0]);
            int dId = Integer.parseInt(doctorCombo.getSelectedItem().toString().split(" - ")[0]);
            Appointment app = new Appointment(editingAppointmentId, pId, dId, LocalDate.parse(dateField.getText()),
                    LocalTime.parse(timeCombo.getSelectedItem().toString()), statusCombo.getSelectedItem().toString(), notesField.getText());
            if (appointmentDAO.update(app)) {
                JOptionPane.showMessageDialog(this, "Updated!");
                loadData(); clearForm();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteAppointment() {
        if (editingAppointmentId != -1 && appointmentDAO.delete(editingAppointmentId)) {
            loadData(); clearForm();
        }
    }

    private void clearForm() {
        notesField.setText(""); editingAppointmentId = -1; table.clearSelection();
    }

    private void loadComboBoxData() {
        patientCombo.removeAllItems();
        patientDAO.getAllPatients().forEach(p -> patientCombo.addItem(p.getId() + " - " + p.getName()));
        doctorCombo.removeAllItems();
        doctorDAO.getAllDoctors().forEach(d -> doctorCombo.addItem(d.getId() + " - " + d.getName()));
    }
}