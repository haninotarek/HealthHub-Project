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

    private static final Color PRIMARY    = new Color(0x11529A);
    private static final Color PRIMARY_LT = new Color(0x296FBB);
    private static final Color BG         = new Color(0xF4F6F8);
    private static final Color WHITE      = Color.WHITE;
    private static final Color BLACK      = Color.BLACK;
    private static final Color GRAY       = new Color(0xC2C3C3);
    private static final Color STATUS_BG   = new Color(0xE8F5E9);
    private static final Color STATUS_TEXT = new Color(0x2E7D32);

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> patientCombo, doctorCombo, timeCombo;
    private JTextField dateField, notesField;
    private JButton saveButton;

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
        center.add(buildForm(),         BorderLayout.NORTH);
        center.add(buildTableSection(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        loadComboBoxData();
    }

    // ── TopBar ─────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel title = new JLabel("Appointments");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);

//        JLabel lblAdmin = new JLabel("  \uD83D\uDC64 Admin  ");
//        lblAdmin.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        lblAdmin.setForeground(WHITE);
//        lblAdmin.setBackground(PRIMARY);
//        lblAdmin.setOpaque(true);
//        lblAdmin.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        bar.add(title,    BorderLayout.WEST);
//        bar.add(lblAdmin, BorderLayout.EAST);
        return bar;
    }

    // ── Form Card ──────────────────────────────────
    private JPanel buildForm() {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(6, 8, 6, 8);
        gbc.weightx = 1.0;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        JLabel cardTitle = new JLabel("Book New Appointment");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cardTitle.setForeground(PRIMARY);
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(cardTitle, gbc);
        gbc.gridwidth = 1;

        // Row 1 labels
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.15;
        card.add(fieldLabel("Patient"), gbc);
        gbc.gridx = 2;
        card.add(fieldLabel("Doctor"), gbc);

        // Row 2 combos
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0.35;
        patientCombo = new JComboBox<>();
        styleCombo(patientCombo);
        card.add(patientCombo, gbc);

        gbc.gridx = 2;
        doctorCombo = new JComboBox<>();
        styleCombo(doctorCombo);
        card.add(doctorCombo, gbc);

        // Row 3 labels
        gbc.gridy = 3; gbc.gridx = 0; gbc.weightx = 0.15;
        card.add(fieldLabel("Date"), gbc);
        gbc.gridx = 2;
        card.add(fieldLabel("Time"), gbc);

        // Row 4 fields
        gbc.gridy = 4; gbc.gridx = 0; gbc.weightx = 0.35;
        dateField = new JTextField(LocalDate.now().toString());
        styleField(dateField);
        card.add(dateField, gbc);

        gbc.gridx = 2;
        String[] hours = {"09:00","09:30","10:00","10:30","11:00","11:30",
                "12:00","13:00","14:00","15:00","16:00","17:00"};
        timeCombo = new JComboBox<>(hours);
        styleCombo(timeCombo);
        card.add(timeCombo, gbc);

        // Row 5 - Book button
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 4;
        gbc.insets = new Insets(14, 8, 6, 8);

        saveButton = new JButton("Book Appointment") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? PRIMARY_LT : PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveButton.setForeground(WHITE);
        saveButton.setOpaque(false);
        saveButton.setContentAreaFilled(false);
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(180, 40));
        saveButton.setMaximumSize(new Dimension(200, 40));
        saveButton.setHorizontalAlignment(SwingConstants.CENTER);
        saveButton.addActionListener(e -> saveAppointment());

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnWrap.setOpaque(false);
        btnWrap.add(saveButton);
        card.add(btnWrap, gbc);

        return card;
    }

    // ── Table Section ──────────────────────────────
    private JPanel buildTableSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        String[] columns = {"#", "Patient Name", "Doctor ID", "Date", "Time", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xF0F0F0));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(PRIMARY_LT);
        table.setSelectionForeground(WHITE);
        table.setBackground(WHITE);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setBackground(WHITE);
                l.setForeground(new Color(0x333333));
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                l.setHorizontalAlignment(LEFT);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0xE5E7EB)));
                return l;
            }
        });
        header.setBackground(WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                if (sel) {
                    l.setBackground(t.getSelectionBackground());
                    l.setForeground(t.getSelectionForeground());
                } else {
                    String s = val != null ? val.toString() : "";
                    switch (s) {
                        case "Scheduled" -> { l.setForeground(PRIMARY);              l.setBackground(new Color(0xE3EDF8)); }
                        case "Completed" -> { l.setForeground(new Color(0x2E7D32)); l.setBackground(new Color(0xE8F5E9)); }
                        case "Cancelled" -> { l.setForeground(new Color(0xC62828)); l.setBackground(new Color(0xFFEBEE)); }
                        default          -> { l.setForeground(BLACK);               l.setBackground(WHITE); }
                    }
                }
                l.setOpaque(true);
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(GRAY, 1));
        scroll.getViewport().setBackground(WHITE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton editBtn   = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        styleButton(editBtn,   new Color(0x11529A), false);
        styleButton(deleteBtn, new Color(0xD32F2F), true);
        editBtn.addActionListener(e -> prepareEdit());
        deleteBtn.addActionListener(e -> deleteAppointment());

        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        wrapper.add(scroll,   BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);

        loadData();
        return wrapper;
    }

    // ── Helpers ────────────────────────────────────
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(0x444444));
        return l;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD5E0), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        f.setPreferredSize(new Dimension(0, 38));
    }

    private void styleCombo(JComboBox<String> c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setBackground(WHITE);
        c.setPreferredSize(new Dimension(0, 38));
    }

    private void styleButton(JButton btn, Color color, boolean solid) {
        final Color baseColor = color;
        final boolean isSolid = solid;

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setPreferredSize(new Dimension(100, 36));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);

        if (isSolid) {
            btn.setForeground(WHITE);
        } else {
            btn.setForeground(baseColor);
        }

        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        btn.setModel(new DefaultButtonModel());

        // رسم الزرار بـ rounded corners
        btn.addPropertyChangeListener(evt -> btn.repaint());

        // override الرسم
        javax.swing.plaf.basic.BasicButtonUI ui = new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton b = (AbstractButton) c;

                if (isSolid) {
                    Color fill = b.getModel().isRollover()
                            ? baseColor.darker()
                            : baseColor;
                    g2.setColor(fill);
                    g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                } else {
                    if (b.getModel().isRollover()) {
                        g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 25));
                        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                    } else {
                        g2.setColor(WHITE);
                        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                    }
                    g2.setColor(baseColor);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 10, 10);
                }
                g2.dispose();
                super.paint(g, c);
            }
        };
        btn.setUI(ui);
    }

    // ── Data (لم يتغير شيء هنا) ────────────────────
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

    private void prepareEdit() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select an appointment to edit!"); return; }

        String patientName = tableModel.getValueAt(row, 1).toString();
        String doctorId    = tableModel.getValueAt(row, 2).toString();
        String date        = tableModel.getValueAt(row, 3).toString();
        String time        = tableModel.getValueAt(row, 4).toString();
        String notes       = tableModel.getValueAt(row, 6).toString();

        for (int i = 0; i < patientCombo.getItemCount(); i++)
            if (patientCombo.getItemAt(i).contains(patientName)) { patientCombo.setSelectedIndex(i); break; }

        for (int i = 0; i < doctorCombo.getItemCount(); i++)
            if (doctorCombo.getItemAt(i).startsWith(doctorId + " -")) { doctorCombo.setSelectedIndex(i); break; }

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
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select an appointment to delete!"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this?") == JOptionPane.YES_OPTION)
            if (appointmentDAO.delete(id)) { JOptionPane.showMessageDialog(this, "Deleted!"); loadData(); }
    }
}