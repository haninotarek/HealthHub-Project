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

    // ── Color Palette ──────────────────────────────
    private static final Color PRIMARY    = new Color(0x11529A);
    private static final Color PRIMARY_LT = new Color(0x296FBB);
    private static final Color BG         = new Color(0xFAFAFA);
    private static final Color WHITE      = Color.WHITE;
    private static final Color BLACK      = Color.BLACK;
    private static final Color GRAY       = new Color(0xC2C3C3);
    // ───────────────────────────────────────────────

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> patientCombo, doctorCombo, timeCombo;
    private JTextField dateField, notesField;
    private JButton saveButton;

    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private DoctorDAO      doctorDAO      = new DoctorDAO();
    private PatientDAO     patientDAO     = new PatientDAO();

    public AppointmentsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(BG);
        center.add(buildForm(),         BorderLayout.NORTH);
        center.add(buildTableSection(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        loadComboBoxData();
    }

    // ── Header ─────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        JLabel title = new JLabel("Appointments Management");
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
                BorderFactory.createEmptyBorder(16, 16, 16, 16) // ← قللنا الـ padding
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6); // ← قللنا المسافات بين العناصر
        gbc.weightx = 1.0;

        // Row 1: Patient + Doctor
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        card.add(makeLabel("Patient:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.4;
        patientCombo = new JComboBox<>();
        styleCombo(patientCombo);
        card.add(patientCombo, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        card.add(makeLabel("Doctor:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.4;
        doctorCombo = new JComboBox<>();
        styleCombo(doctorCombo);
        card.add(doctorCombo, gbc);

        // Row 2: Date + Time
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        card.add(makeLabel("Date:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.4;
        dateField = new JTextField(LocalDate.now().toString());
        styleField(dateField);
        card.add(dateField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        card.add(makeLabel("Time:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.4;
        String[] hours = {
                "09:00","10:00","11:00","12:00","13:00",
                "14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00"
        };
        timeCombo = new JComboBox<>(hours);
        styleCombo(timeCombo);
        card.add(timeCombo, gbc);

        // Row 3: Notes + Save Button
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        card.add(makeLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.8;
        notesField = new JTextField();
        styleField(notesField);
        card.add(notesField, gbc);

        gbc.gridx = 3; gbc.gridwidth = 1; gbc.weightx = 0.2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        saveButton = new JButton("Save Appointment");
        saveButton.setBackground(PRIMARY);
        saveButton.setForeground(WHITE);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setOpaque(true);
        saveButton.setContentAreaFilled(true);
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(150, 38));
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

    // ── Table Section ──────────────────────────────
    private JPanel buildTableSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setBackground(BG);

        String[] columns = {"#", "Patient Name", "Doctor ID", "Date", "Time", "Status", "Notes"};
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

        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(true);
        table.setGridColor(new Color(0xE8E8E8));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(PRIMARY_LT);
        table.setSelectionForeground(WHITE);
        table.setFillsViewportHeight(true);
        table.setBackground(WHITE);

        // ── Header ──
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setBackground(PRIMARY);
                l.setForeground(WHITE);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setHorizontalAlignment(CENTER); // ← النص في النص
                l.setOpaque(true);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, PRIMARY_LT));
                return l;
            }
        });
        header.setBackground(PRIMARY);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);

        // ── Center Renderer لكل الأعمدة ──
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columns.length; i++) {
            if (i != 5) { // الـ Status عنده renderer خاص
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // ── Status Renderer ──
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                if (!sel) {
                    String s = val != null ? val.toString() : "";
                    switch (s) {
                        case "Scheduled" -> { setForeground(PRIMARY);              setBackground(new Color(0xE3EDF8)); }
                        case "Completed" -> { setForeground(new Color(0x2E7D32)); setBackground(new Color(0xE8F5E9)); }
                        case "Cancelled" -> { setForeground(new Color(0xC62828)); setBackground(new Color(0xFFEBEE)); }
                        default          -> { setForeground(BLACK);               setBackground(WHITE); }
                    }
                }
                setOpaque(true);
                return this;
            }
        });

        // ── Column Widths ──
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(GRAY, 1));
        scroll.getViewport().setBackground(BG);

        // ── Buttons ──
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(BG);

        JButton editBtn = new JButton("Edit");
        styleButton(editBtn, PRIMARY, false);
        editBtn.addActionListener(e -> prepareEdit());

        JButton deleteBtn = new JButton("Delete");
        styleButton(deleteBtn, new Color(0xD32F2F), true);
        deleteBtn.addActionListener(e -> deleteAppointment());

        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        wrapper.add(scroll,    BorderLayout.CENTER);
        wrapper.add(btnPanel,  BorderLayout.SOUTH);

        loadData();
        return wrapper;
    }

    // ── Helpers ────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(BLACK);
        return l;
    }

    private void styleField(JTextField f) {
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRAY, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBackground(WHITE);
        f.setForeground(BLACK);
    }

    private void styleCombo(JComboBox<String> c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBackground(WHITE);
        c.setForeground(BLACK);
    }

    private void styleButton(JButton btn, Color color, boolean solid) {
        btn.setPreferredSize(new Dimension(100, 34));
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

    // ── Data ───────────────────────────────────────
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
                    a.getId(), pName, a.getDoctorId(),
                    a.getDate(), a.getTime(), a.getStatus(), a.getNotes()
            });
        }
    }

    private void prepareEdit() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to edit!");
            return;
        }
        String patientName = tableModel.getValueAt(row, 1).toString();
        String doctorId    = tableModel.getValueAt(row, 2).toString();
        String date        = tableModel.getValueAt(row, 3).toString();
        String time        = tableModel.getValueAt(row, 4).toString();
        String notes       = tableModel.getValueAt(row, 6).toString();

        for (int i = 0; i < patientCombo.getItemCount(); i++) {
            if (patientCombo.getItemAt(i).contains(patientName)) {
                patientCombo.setSelectedIndex(i); break;
            }
        }
        for (int i = 0; i < doctorCombo.getItemCount(); i++) {
            if (doctorCombo.getItemAt(i).startsWith(doctorId + " -")) {
                doctorCombo.setSelectedIndex(i); break;
            }
        }
        dateField.setText(date);
        timeCombo.setSelectedItem(time);
        notesField.setText(notes);
        JOptionPane.showMessageDialog(this, "Data loaded. Modify and click Save.");
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
            if (appointmentDAO.add(new Appointment(0, pId, dId, d, t, "Scheduled", notesField.getText()))) {
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
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?");
        if (confirm == JOptionPane.YES_OPTION) {
            if (appointmentDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "Deleted!");
                loadData();
            }
        }
    }
}