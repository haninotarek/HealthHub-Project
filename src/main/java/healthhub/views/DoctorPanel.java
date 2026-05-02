package healthhub.views;

import healthhub.dao.DoctorDAO;
import healthhub.models.Doctor;
import healthhub.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DoctorPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DoctorDAO doctorDAO;
    private JTextField txtName, txtPhone, txtEmail, txtSearch;
    private JComboBox<String> comboSpec;
    private int selectedDoctorId = -1;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public DoctorPanel() {
        doctorDAO = new DoctorDAO();
        setLayout(new BorderLayout(0, 16));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // العنوان العلوي
        JLabel title = new JLabel("Doctors");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ColorPalette.PRIMARY);
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(buildDoctorForm(), BorderLayout.NORTH);
        center.add(buildTableSection(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        loadTableData();
    }

    private JPanel buildDoctorForm() {
        // الـ Card المنحني زي البيشنتس
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
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

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        JLabel cardTitle = new JLabel("Doctor Management");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cardTitle.setForeground(ColorPalette.PRIMARY);
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(cardTitle, gbc);
        gbc.gridwidth = 1;

        // Labels & Fields (شكل منظم)
        gbc.gridy = 1; gbc.gridx = 0; card.add(new JLabel("Doctor Name"), gbc);
        gbc.gridx = 2; card.add(new JLabel("Specialization"), gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        txtName = createStyledField(); card.add(txtName, gbc);
        gbc.gridx = 2;
        String[] specs = {"General Medicine", "Pediatrics", "Cardiology", "Dermatology", "Neurology", "Orthopedics", "Dentistry"};
        comboSpec = new JComboBox<>(specs); styleComboBox(comboSpec); card.add(comboSpec, gbc);

        gbc.gridy = 3; gbc.gridx = 0; card.add(new JLabel("Phone"), gbc);
        gbc.gridx = 2; card.add(new JLabel("Email"), gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        txtPhone = createStyledField(); card.add(txtPhone, gbc);
        gbc.gridx = 2;
        txtEmail = createStyledField(); card.add(txtEmail, gbc);

        // الزراير (بألوانك الأصلية للدكاترة)
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 4;
        gbc.insets = new Insets(16, 8, 4, 8);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnAdd = createStyledButton("Add Doctor", new Color(235, 255, 235), new Color(40, 167, 69));
        JButton btnUpdate = createStyledButton("Update", new Color(235, 245, 255), ColorPalette.PRIMARY);
        JButton btnDelete = createStyledButton("Delete", new Color(255, 235, 235), ColorPalette.DANGER);

        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete);
        card.add(btnPanel, gbc);

        // Listeners (نفس منطقك الأصلي)
        btnAdd.addActionListener(e -> {
            if(txtName.getText().trim().isEmpty()) return;
            Doctor d = new Doctor(0, txtName.getText(), comboSpec.getSelectedItem().toString(), txtPhone.getText(), txtEmail.getText());
            if (doctorDAO.addDoctor(d)) { loadTableData(); clearFields(); }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedDoctorId != -1) {
                Doctor d = new Doctor(selectedDoctorId, txtName.getText(), comboSpec.getSelectedItem().toString(), txtPhone.getText(), txtEmail.getText());
                if (doctorDAO.updateDoctor(d)) { loadTableData(); clearFields(); }
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedDoctorId != -1) {
                if (JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", 0) == 0) {
                    if (doctorDAO.deleteDoctor(selectedDoctorId)) { loadTableData(); clearFields(); }
                }
            }
        });

        return card;
    }

    private JPanel buildTableSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        txtSearch = createStyledField();
        JPanel searchP = new JPanel(new BorderLayout(10, 0));
        searchP.setOpaque(false);
        searchP.add(new JLabel(" Search Doctor:"), BorderLayout.WEST);
        searchP.add(txtSearch, BorderLayout.CENTER);

        String[] columns = {"ID", "Name", "Specialization", "Phone", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);

        // التوسيط واللون الأزرق للهيدر
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setBackground(ColorPalette.PRIMARY);
                l.setForeground(Color.WHITE);
                l.setHorizontalAlignment(JLabel.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
                return l;
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        table.setRowHeight(35);
        table.setSelectionBackground(new Color(230, 240, 255));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        wrapper.add(searchP, BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);

        // Search Logic
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            void filter() {
                String t = txtSearch.getText();
                if(t.isEmpty()) table.setRowSorter(null);
                else {
                    TableRowSorter<DefaultTableModel> s = new TableRowSorter<>(tableModel);
                    table.setRowSorter(s);
                    s.setRowFilter(RowFilter.regexFilter("(?i)" + t, 1, 2));
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int vr = table.getSelectedRow();
            if (vr != -1) {
                int r = table.convertRowIndexToModel(vr);
                selectedDoctorId = (int) tableModel.getValueAt(r, 0);
                txtName.setText((String) tableModel.getValueAt(r, 1));
                comboSpec.setSelectedItem((String) tableModel.getValueAt(r, 2));
                txtPhone.setText((String) tableModel.getValueAt(r, 3));
                txtEmail.setText((String) tableModel.getValueAt(r, 4));
            }
        });

        return wrapper;
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 224), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private void styleComboBox(JComboBox<String> c) {
        c.setBackground(Color.WHITE);
        c.setPreferredSize(new Dimension(0, 38));
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(fg, 1));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(120, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(fg); btn.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(bg); btn.setForeground(fg); }
        });
        return btn;
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        for (Doctor d : doctors) tableModel.addRow(new Object[]{d.getId(), d.getName(), d.getSpecialization(), d.getPhone(), d.getEmail()});
    }

    private void clearFields() {
        txtName.setText(""); txtPhone.setText(""); txtEmail.setText("");
        comboSpec.setSelectedIndex(0); selectedDoctorId = -1;
        table.clearSelection();
    }
}