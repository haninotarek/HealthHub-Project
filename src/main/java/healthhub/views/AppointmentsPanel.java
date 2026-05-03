package healthhub.views;

import healthhub.dao.AppointmentDAO;
import healthhub.dao.DoctorDAO;
import healthhub.dao.PatientDAO;
import healthhub.models.Appointment;
import healthhub.utils.ColorPalette;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentsPanel extends JPanel {

    private static final Color PRIMARY    = new Color(0x11529A);
    private static final Color PRIMARY_LT = new Color(0x296FBB);
    private static final Color BG         = new Color(0xF4F6F8);
    private static final Color WHITE      = Color.WHITE;
    private static final Color GRAY       = new Color(0xC2C3C3);

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> patientCombo, doctorCombo, timeCombo;
    private JTextField dateField, notesField;
    private JButton saveButton;

    // لتتبع الـ appointment المحدود للتعديل
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
        center.add(buildForm(),         BorderLayout.NORTH);
        center.add(buildTableSection(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        loadComboBoxData();
    }

    // ── Top Bar ────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        JLabel title = new JLabel("Appointments");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        bar.add(title, BorderLayout.WEST);
        return bar;
    }

    // ── Form Card (نفس شكل الـ Patients بالضبط) ───────────────
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
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(6, 8, 6, 8);
        gbc.weightx = 1.0;

        // Card Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        JLabel cardTitle = new JLabel("Book New Appointment");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cardTitle.setForeground(PRIMARY);
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(cardTitle, gbc);
        gbc.gridwidth = 1;

        // Row 1 - Labels
        gbc.gridy = 1; gbc.gridx = 0; card.add(fieldLabel("Patient"), gbc);
        gbc.gridx = 2; card.add(fieldLabel("Doctor"), gbc);

        // Row 2 - Combos
        gbc.gridy = 2; gbc.gridx = 0;
        patientCombo = new JComboBox<>();
        styleCombo(patientCombo);
        card.add(patientCombo, gbc);

        gbc.gridx = 2;
        doctorCombo = new JComboBox<>();
        styleCombo(doctorCombo);
        card.add(doctorCombo, gbc);

        // Row 3 - Labels
        gbc.gridy = 3; gbc.gridx = 0; card.add(fieldLabel("Date"), gbc);
        gbc.gridx = 2; card.add(fieldLabel("Time"), gbc);

        // Row 4 - Date & Time
        gbc.gridy = 4; gbc.gridx = 0;
        dateField = createField();
        dateField.setText(LocalDate.now().toString());
        card.add(dateField, gbc);

        gbc.gridx = 2;
        // نفس الأوقات من الكود القديم الصح
        String[] hours = {"09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00"};
        timeCombo = new JComboBox<>(hours);
        styleCombo(timeCombo);
        card.add(timeCombo, gbc);

        // Row 5 - Notes Label
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 4;
        card.add(fieldLabel("Notes"), gbc);

        // Row 6 - Notes Field (ممتد على الكل)
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 4;
        notesField = createField();
        card.add(notesField, gbc);

        // Row 7 - Buttons (نفس شكل Patients بالضبط)
        gbc.gridy = 7; gbc.gridwidth = 4;
        gbc.insets = new Insets(16, 8, 4, 8);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnBook   = createStyledButton("Book Appointment", new Color(235, 255, 235), new Color(40, 167, 69));
        JButton btnUpdate = createStyledButton("Update",           new Color(235, 245, 255), PRIMARY);
        JButton btnDelete = createStyledButton("Delete",           new Color(255, 235, 235), new Color(0xD32F2F));

        btnPanel.add(btnBook);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        card.add(btnPanel, gbc);

        // ── Listeners ─────────────────────────────────────────
        btnBook.addActionListener(e -> saveAppointment());

        btnUpdate.addActionListener(e -> {
            if (editingAppointmentId == -1) {
                JOptionPane.showMessageDialog(this, "Please select an appointment from the table first!");
                return;
            }
            try {
                int pId = Integer.parseInt(patientCombo.getSelectedItem().toString().split(" - ")[0]);
                int dId = Integer.parseInt(doctorCombo.getSelectedItem().toString().split(" - ")[0]);
                LocalDate d = LocalDate.parse(dateField.getText());
                LocalTime t = LocalTime.parse(timeCombo.getSelectedItem().toString());

                // نحذف ونضيف من جديد (أو تقدري تعملي update في الـ DAO لو موجودة)
                if (appointmentDAO.delete(editingAppointmentId)) {
                    if (appointmentDAO.hasConflict(dId, d, t)) {
                        JOptionPane.showMessageDialog(this, "Conflict: Doctor is busy at this time!", "Error", JOptionPane.ERROR_MESSAGE);
                        loadData();
                        return;
                    }
                    appointmentDAO.add(new Appointment(0, pId, dId, d, t, "Scheduled", notesField.getText()));
                    JOptionPane.showMessageDialog(this, "Updated Successfully!");
                    loadData();
                    clearForm();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: Check date format (YYYY-MM-DD)!");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an appointment to delete!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this appointment?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (appointmentDAO.delete(id)) {
                    JOptionPane.showMessageDialog(this, "Deleted Successfully!");
                    loadData();
                    clearForm();
                }
            }
        });

        return card;
    }

    // ── Table Section (هيدر أزرق + توسيط زي Patients) ─────────
    private JPanel buildTableSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        String[] columns = {"#", "Patient Name", "Doctor ID", "Date", "Time", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xF0F0F0));
        table.setBackground(WHITE);
        table.setFillsViewportHeight(true);

        // ── هيدر أزرق زي Patients ──────────────────────────────
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setBackground(PRIMARY);
                l.setForeground(WHITE);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setHorizontalAlignment(JLabel.CENTER);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
                return l;
            }
        });

        // ── توسيط كل الأعمدة زي Patients ──────────────────────
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // ── Status Column بألوان خاصة + توسيط ─────────────────
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                if (sel) {
                    l.setBackground(t.getSelectionBackground());
                    l.setForeground(t.getSelectionForeground());
                } else {
                    if ("Scheduled".equals(val)) {
                        l.setBackground(new Color(0xE8F5E9));
                        l.setForeground(new Color(0x2E7D32));
                    } else {
                        l.setBackground(WHITE);
                        l.setForeground(Color.BLACK);
                    }
                }
                l.setOpaque(true);
                return l;
            }
        });

        // ── Selection Listener: يحمّل البيانات في الفورم ────────
        table.getSelectionModel().addListSelectionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow != -1) {
                editingAppointmentId = (int) tableModel.getValueAt(viewRow, 0);

                String patientName = tableModel.getValueAt(viewRow, 1).toString();
                String doctorId    = tableModel.getValueAt(viewRow, 2).toString();
                String date        = tableModel.getValueAt(viewRow, 3).toString();
                String time        = tableModel.getValueAt(viewRow, 4).toString();
                String notes       = tableModel.getValueAt(viewRow, 6).toString();

                // ضبط Patient Combo
                for (int i = 0; i < patientCombo.getItemCount(); i++) {
                    if (patientCombo.getItemAt(i).contains(patientName)) {
                        patientCombo.setSelectedIndex(i);
                        break;
                    }
                }

                // ضبط Doctor Combo
                for (int i = 0; i < doctorCombo.getItemCount(); i++) {
                    if (doctorCombo.getItemAt(i).startsWith(doctorId + " -")) {
                        doctorCombo.setSelectedIndex(i);
                        break;
                    }
                }

                dateField.setText(date);
                timeCombo.setSelectedItem(time);
                notesField.setText(notes);
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scroll.getViewport().setBackground(WHITE);

        wrapper.add(scroll, BorderLayout.CENTER);
        loadData();
        return wrapper;
    }

    // ── Helpers ─────────────────────────────────────────────────
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(0x444444));
        return l;
    }

    private JTextField createField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD5E0), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setPreferredSize(new Dimension(0, 38));
        return f;
    }

    private void styleCombo(JComboBox<String> c) {
        c.setPreferredSize(new Dimension(0, 38));
        c.setBackground(WHITE);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    // نفس ميثود الزراير من Patients بالضبط
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(fg, 1));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(140, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(fg); btn.setForeground(WHITE); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); btn.setForeground(fg); }
        });
        return btn;
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

    private void saveAppointment() {
        try {
            int pId = Integer.parseInt(patientCombo.getSelectedItem().toString().split(" - ")[0]);
            int dId = Integer.parseInt(doctorCombo.getSelectedItem().toString().split(" - ")[0]);
            LocalDate d = LocalDate.parse(dateField.getText());
            LocalTime t = LocalTime.parse(timeCombo.getSelectedItem().toString());

            if (appointmentDAO.hasConflict(dId, d, t)) {
                JOptionPane.showMessageDialog(this, "Conflict: Doctor is busy at this time!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (appointmentDAO.add(new Appointment(0, pId, dId, d, t, "Scheduled", notesField.getText()))) {
                JOptionPane.showMessageDialog(this, "Appointment Booked Successfully!");
                loadData();
                clearForm();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: Check date format (YYYY-MM-DD)!");
        }
    }

    private void clearForm() {
        notesField.setText("");
        dateField.setText(LocalDate.now().toString());
        editingAppointmentId = -1;
        table.clearSelection();
    }
}