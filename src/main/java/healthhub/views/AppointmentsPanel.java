package healthhub.views;

import healthhub.dao.AppointmentDAO;
import healthhub.models.Appointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentsPanel extends JPanel {

    // ── Color Palette ──────────────────────────────
    private static final Color PRIMARY    = new Color(0x11529A);
    private static final Color PRIMARY_LT = new Color(0x296FBB);
    private static final Color BG         = new Color(0xFAFAFA);
    private static final Color BLACK      = new Color(0x000000);
    private static final Color GRAY       = new Color(0xC2C3C3);
    private static final Color WHITE      = Color.WHITE;
    // ───────────────────────────────────────────────

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> patientCombo, doctorCombo, timeCombo;
    private JTextField dateField, notesField;
    private JButton saveButton;
    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    public AppointmentsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(BG);
        center.add(buildForm(),  BorderLayout.NORTH);
        center.add(buildTable(), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    // ── Header ─────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Appointments");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        p.add(title, BorderLayout.WEST);
        return p;
    }

    // ── Form ───────────────────────────────────────
    private JPanel buildForm() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRAY, 1, true),
                BorderFactory.createEmptyBorder(16, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Card Title
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 6; gbc.weightx = 1.0;
        JLabel cardTitle = new JLabel("Book New Appointment");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cardTitle.setForeground(PRIMARY);
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        card.add(cardTitle, gbc);

        // Separator
        gbc.gridy = 1;
        JSeparator sep = new JSeparator();
        sep.setForeground(GRAY);
        card.add(sep, gbc);

        // Row 2: Patient + Doctor
        gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = 2;
        card.add(makeLabel("Patient:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.4; gbc.gridwidth = 2;
        patientCombo = new JComboBox<>(new String[]{
                "1 - Mohamed Ali", "2 - Sara Khaled", "3 - Omar Youssef"
        });
        styleCombo(patientCombo);
        card.add(patientCombo, gbc);

        gbc.gridx = 3; gbc.weightx = 0; gbc.gridwidth = 1;
        card.add(makeLabel("Doctor:"), gbc);

        gbc.gridx = 4; gbc.weightx = 0.4; gbc.gridwidth = 2;
        doctorCombo = new JComboBox<>(new String[]{
                "1 - Dr. Ahmed Hassan", "2 - Dr. Mona Salem", "3 - Dr. Karim Nabil"
        });
        styleCombo(doctorCombo);
        card.add(doctorCombo, gbc);

        // Row 3: Date + Time + Notes
        gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = 3;
        card.add(makeLabel("Date:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.2; gbc.gridwidth = 1;
        dateField = new JTextField("2026-04-30");
        styleField(dateField);
        card.add(dateField, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.gridwidth = 1;
        card.add(makeLabel("Time:"), gbc);

        gbc.gridx = 3; gbc.weightx = 0.15; gbc.gridwidth = 1;
        timeCombo = new JComboBox<>(new String[]{
                "09:00","09:30","10:00","10:30","11:00","11:30",
                "12:00","12:30","13:00","13:30","14:00","14:30",
                "15:00","15:30","16:00","16:30","17:00","17:30"
        });
        styleCombo(timeCombo);
        card.add(timeCombo, gbc);

        gbc.gridx = 4; gbc.weightx = 0; gbc.gridwidth = 1;
        card.add(makeLabel("Notes:"), gbc);

        gbc.gridx = 5; gbc.weightx = 0.25; gbc.gridwidth = 1;
        notesField = new JTextField();
        styleField(notesField);
        card.add(notesField, gbc);

        // Row 4: Save Button
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 6; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(14, 6, 4, 6);

        saveButton = new JButton("Save Appointment");
        saveButton.setPreferredSize(new Dimension(200, 40));
        saveButton.setBackground(PRIMARY);
        saveButton.setForeground(WHITE);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setOpaque(true);
        saveButton.setContentAreaFilled(true);
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                saveButton.setBackground(PRIMARY_LT);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                saveButton.setBackground(PRIMARY);
            }
        });
        saveButton.addActionListener(e -> saveAppointment());
        card.add(saveButton, gbc);

        return card;
    }

    // ── Table ──────────────────────────────────────
    private JPanel buildTable() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setBackground(BG);

        JLabel tableTitle = new JLabel("All Appointments");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableTitle.setForeground(PRIMARY);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(4, 2, 6, 0));
        wrapper.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"#", "Patient", "Doctor", "Date", "Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(PRIMARY_LT);
                    c.setForeground(WHITE);
                } else if (col != 5) {
                    // الـ Status column بيتحكم فيه الـ custom renderer
                    c.setBackground(row % 2 == 0 ? WHITE : BG);
                    c.setForeground(BLACK);
                }
                return c;
            }
        };

        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setShowGrid(true);
        table.setGridColor(new Color(0xE8E8E8));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(PRIMARY_LT);
        table.setSelectionForeground(WHITE);
        table.setFillsViewportHeight(true);
        table.setBackground(WHITE);

        // ── Header ──
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setBackground(PRIMARY);
                l.setForeground(WHITE);
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setHorizontalAlignment(CENTER);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, PRIMARY_LT));
                return l;
            }
        });
        header.setBackground(PRIMARY);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);

        // ── Status Renderer ──
        table.getColumnModel().getColumn(5).setCellRenderer(
                new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                        super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                        setHorizontalAlignment(CENTER);
                        setFont(new Font("Segoe UI", Font.BOLD, 11));
                        if (!sel) {
                            String s = val != null ? val.toString() : "";
                            switch (s) {
                                case "Scheduled" -> { setForeground(PRIMARY);              setBackground(new Color(0xE3EDF8)); }
                                case "Completed" -> { setForeground(new Color(0x2E7D32)); setBackground(new Color(0xE8F5E9)); }
                                case "Cancelled" -> { setForeground(new Color(0xC62828)); setBackground(new Color(0xFFEBEE)); }
                                default          -> { setForeground(BLACK);               setBackground(WHITE); }
                            }
                        }
                        return this;
                    }
                }
        );

        // ── Column Widths ──
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);

        loadData();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(GRAY, 1));
        scroll.getViewport().setBackground(BG);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Helpers ────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(BLACK);
        return l;
    }

    private void styleField(JTextField f) {
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRAY, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBackground(WHITE);
        f.setForeground(BLACK);
    }

    private void styleCombo(JComboBox<String> c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        c.setBackground(WHITE);
        c.setForeground(BLACK);
    }

    // ── Data ───────────────────────────────────────
    private void loadData() {
        tableModel.setRowCount(0);
        List<Appointment> list = appointmentDAO.getAll();
        for (Appointment a : list) {
            tableModel.addRow(new Object[]{
                    a.getId(), a.getPatientId(), a.getDoctorId(),
                    a.getDate(), a.getTime(), a.getStatus()
            });
        }
    }

    private void saveAppointment() {
        String dateStr = dateField.getText().trim();
        String timeStr = (String) timeCombo.getSelectedItem();
        String notes   = notesField.getText().trim();

        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a date.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate appointmentDate = LocalDate.parse(dateStr);
            LocalTime appointmentTime = LocalTime.parse(timeStr);

            int patientId = Integer.parseInt(patientCombo.getSelectedItem().toString().split(" - ")[0]);
            int doctorId  = Integer.parseInt(doctorCombo.getSelectedItem().toString().split(" - ")[0]);

            if (appointmentDAO.hasConflict(doctorId, dateStr, timeStr)) {
                JOptionPane.showMessageDialog(this, "Doctor already booked!", "Conflict Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Appointment a = new Appointment(0, patientId, doctorId,
                    appointmentDate, appointmentTime, "Scheduled", notes);

            if (appointmentDAO.add(a)) {
                JOptionPane.showMessageDialog(this, "Saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}