package healthhub.views;

import healthhub.dao.DoctorDAO;
import healthhub.models.Doctor;
import healthhub.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        setLayout(new BorderLayout(15, 15));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- 1. فورم الإدخال (تم تعديله ليكون منظم) ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ColorPalette.PRIMARY, 1),
                " Doctor Management ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), ColorPalette.PRIMARY
        );
        inputPanel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        txtName = createStyledField();
        txtPhone = createStyledField();
        txtEmail = createStyledField();

        String[] specializations = {
                "General Medicine", "Pediatrics", "Cardiology",
                "Dermatology", "Neurology", "Orthopedics", "Dentistry"
        };
        comboSpec = new JComboBox<>(specializations);
        styleComboBox(comboSpec);

        // إضافة العناصر بالتنسيق الجديد
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; inputPanel.add(new JLabel("Doctor Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; inputPanel.add(txtName, gbc);

        gbc.gridx = 2; gbc.weightx = 0; inputPanel.add(new JLabel("Specialization:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1; inputPanel.add(comboSpec, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; inputPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; inputPanel.add(txtPhone, gbc);

        gbc.gridx = 2; gbc.weightx = 0; inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1; inputPanel.add(txtEmail, gbc);

        // --- 2. الزراير بألوان البيشنتس ---
        JButton btnAdd = createStyledButton("Add Doctor", new Color(235, 255, 235), new Color(40, 167, 69)); // فاتح بحدود خضراء
        JButton btnUpdate = createStyledButton("Update", new Color(235, 245, 255), ColorPalette.PRIMARY); // فاتح بحدود زرقاء
        JButton btnDelete = createStyledButton("Delete", new Color(255, 235, 235), ColorPalette.DANGER); // فاتح بحدود حمراء

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnWrapper.setBackground(ColorPalette.BACKGROUND);
        btnWrapper.add(btnAdd); btnWrapper.add(btnUpdate); btnWrapper.add(btnDelete);

        // --- 3. منطقة البحث ---
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(ColorPalette.BACKGROUND);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel lblSearch = new JLabel(" Search Doctor:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSearch = createStyledField();

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        // --- 4. الجدول مع التوسيط ---
        String[] columns = {"ID", "Name", "Specialization", "Phone", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // تنسيق الهيدر (العناوين)
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setOpaque(true);
                label.setBackground(ColorPalette.PRIMARY);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
                return label;
            }
        });

        // تنسيق محتوى الجدول (التوسيط)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(ColorPalette.BACKGROUND);
        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ColorPalette.BACKGROUND);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(btnWrapper, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);

        // --- منطق البحث والـ Selection (نفسه بدون تغيير) ---
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterTable(); }
            @Override public void removeUpdate(DocumentEvent e) { filterTable(); }
            @Override public void changedUpdate(DocumentEvent e) { filterTable(); }
            private void filterTable() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) rowSorter.setRowFilter(null);
                else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2));
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow != -1) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                selectedDoctorId = (int) tableModel.getValueAt(modelRow, 0);
                txtName.setText((String) tableModel.getValueAt(modelRow, 1));
                comboSpec.setSelectedItem((String) tableModel.getValueAt(modelRow, 2));
                txtPhone.setText((String) tableModel.getValueAt(modelRow, 3));
                txtEmail.setText((String) tableModel.getValueAt(modelRow, 4));
            }
        });

        btnAdd.addActionListener(e -> {
            if(txtName.getText().trim().isEmpty()) return;
            Doctor d = new Doctor(0, txtName.getText(), (String) comboSpec.getSelectedItem(), txtPhone.getText(), txtEmail.getText());
            if (doctorDAO.addDoctor(d)) { loadTableData(); clearFields(); }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedDoctorId != -1) {
                Doctor d = new Doctor(selectedDoctorId, txtName.getText(), (String) comboSpec.getSelectedItem(), txtPhone.getText(), txtEmail.getText());
                if (doctorDAO.updateDoctor(d)) { loadTableData(); clearFields(); }
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedDoctorId != -1) {
                if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this doctor?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == 0) {
                    if (doctorDAO.deleteDoctor(selectedDoctorId)) { loadTableData(); clearFields(); }
                }
            }
        });

        loadTableData();
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(Color.WHITE);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return field;
    }

    // تعديل ميثود الزرار عشان ياخد لون الخلفية ولون النص/الحدود
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(fg, 1));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(120, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // تأثير عند مرور الماوس
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(fg); btn.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(bg); btn.setForeground(fg); }
        });
        return btn;
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        for (Doctor d : doctors) {
            tableModel.addRow(new Object[]{d.getId(), d.getName(), d.getSpecialization(), d.getPhone(), d.getEmail()});
        }
    }

    private void clearFields() {
        txtName.setText("");
        comboSpec.setSelectedIndex(0);
        txtPhone.setText("");
        txtEmail.setText("");
        txtSearch.setText("");
        selectedDoctorId = -1;
        table.clearSelection();
    }

    // لإضافة MouseAdapter
    private static class MouseAdapter extends java.awt.event.MouseAdapter {}
}