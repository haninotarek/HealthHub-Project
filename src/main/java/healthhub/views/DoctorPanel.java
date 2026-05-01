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

public class DoctorPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DoctorDAO doctorDAO;
    private JTextField txtName, txtPhone, txtEmail, txtSearch;
    private JComboBox<String> comboSpec; // التغيير هنا: بدال الـ JTextField
    private int selectedDoctorId = -1;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public DoctorPanel() {
        doctorDAO = new DoctorDAO();
        setLayout(new BorderLayout(15, 15));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- 1. فورم الإدخال ---
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 15, 10));
        inputPanel.setBackground(Color.WHITE);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ColorPalette.PRIMARY, 1),
                " Doctor Management ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), ColorPalette.PRIMARY
        );
        inputPanel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        txtName = createStyledField();
        txtPhone = createStyledField();
        txtEmail = createStyledField();

        // قائمة التخصصات - تقدري تزودي أي تخصص تاني هنا
        String[] specializations = {
                "General Medicine", "Pediatrics", "Cardiology",
                "Dermatology", "Neurology", "Orthopedics", "Dentistry"
        };
        comboSpec = new JComboBox<>(specializations);
        styleComboBox(comboSpec);

        inputPanel.add(new JLabel("Doctor Name:"));    inputPanel.add(txtName);
        inputPanel.add(new JLabel("Specialization:")); inputPanel.add(comboSpec); // استخدمنا الـ Combo
        inputPanel.add(new JLabel("Phone:"));          inputPanel.add(txtPhone);
        inputPanel.add(new JLabel("Email:"));          inputPanel.add(txtEmail);

        // --- 2. الزراير ---
        JButton btnAdd = createStyledButton("Add Doctor", ColorPalette.SUCCESS);
        JButton btnUpdate = createStyledButton("Update", ColorPalette.PRIMARY);
        JButton btnDelete = createStyledButton("Delete", ColorPalette.DANGER);

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnWrapper.setBackground(ColorPalette.BACKGROUND);
        btnWrapper.add(btnAdd); btnWrapper.add(btnUpdate); btnWrapper.add(btnDelete);

        // --- 3. منطقة البحث ---
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(ColorPalette.BACKGROUND);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel lblSearch = new JLabel("🔍 Search Doctor:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSearch = createStyledField();

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        // --- 4. الجدول ---
        String[] columns = {"ID", "Name", "Specialization", "Phone", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                label.setBackground(ColorPalette.PRIMARY);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
                return label;
            }
        });

        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(table);

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

        // --- منطق البحث ---
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

        // --- Selection Logic ---
        table.getSelectionModel().addListSelectionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow != -1) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                selectedDoctorId = (int) tableModel.getValueAt(modelRow, 0);
                txtName.setText((String) tableModel.getValueAt(modelRow, 1));

                // اختيار التخصص من القائمة المنسدلة بناءً على المكتوب في الجدول
                String spec = (String) tableModel.getValueAt(modelRow, 2);
                comboSpec.setSelectedItem(spec);

                txtPhone.setText((String) tableModel.getValueAt(modelRow, 3));
                txtEmail.setText((String) tableModel.getValueAt(modelRow, 4));
            }
        });

        // --- الأزرار (Add / Update / Delete) ---
        btnAdd.addActionListener(e -> {
            if(txtName.getText().trim().isEmpty()) return;
            String spec = (String) comboSpec.getSelectedItem();
            Doctor d = new Doctor(0, txtName.getText(), spec, txtPhone.getText(), txtEmail.getText());
            if (doctorDAO.addDoctor(d)) { loadTableData(); clearFields(); }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedDoctorId != -1) {
                String spec = (String) comboSpec.getSelectedItem();
                Doctor d = new Doctor(selectedDoctorId, txtName.getText(), spec, txtPhone.getText(), txtEmail.getText());
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

        loadTableData();
    }

    // تنسيق الـ ComboBox ليكون شكله متناسق مع الحقول
    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(Color.WHITE);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return field;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        comboSpec.setSelectedIndex(0); // رجوع لأول تخصص
        txtPhone.setText("");
        txtEmail.setText("");
        txtSearch.setText("");
        selectedDoctorId = -1;
        table.clearSelection();
    }
}