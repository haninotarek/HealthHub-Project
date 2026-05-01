package healthhub.views;

import healthhub.dao.DoctorDAO;
import healthhub.models.Doctor;
import healthhub.utils.ColorPalette;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class DoctorPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DoctorDAO doctorDAO;
    private JTextField txtName, txtSpec, txtPhone, txtEmail;
    private int selectedDoctorId = -1;

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

        txtName = createStyledField(); txtSpec = createStyledField();
        txtPhone = createStyledField(); txtEmail = createStyledField();

        inputPanel.add(new JLabel("Doctor Name:"));    inputPanel.add(txtName);
        inputPanel.add(new JLabel("Specialization:")); inputPanel.add(txtSpec);
        inputPanel.add(new JLabel("Phone:"));          inputPanel.add(txtPhone);
        inputPanel.add(new JLabel("Email:"));          inputPanel.add(txtEmail);

        // --- 2. الزراير ---
        JButton btnAdd = createStyledButton("Add Doctor", ColorPalette.SUCCESS);
        JButton btnUpdate = createStyledButton("Update", ColorPalette.PRIMARY);
        JButton btnDelete = createStyledButton("Delete", ColorPalette.DANGER);

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnWrapper.setBackground(ColorPalette.BACKGROUND);
        btnWrapper.add(btnAdd); btnWrapper.add(btnUpdate); btnWrapper.add(btnDelete);

        // --- 3. الجدول (حل مشكلة اختفاء العناوين النهائي) ---
        String[] columns = {"ID", "Name", "Specialization", "Phone", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);

        // الحل السحري: Custom Renderer للـ Header
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                label.setBackground(ColorPalette.PRIMARY); // أزرق غامق
                label.setForeground(Color.WHITE);          // أبيض ناصع
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
                return label;
            }
        });

        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 242, 255));
        table.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ColorPalette.BACKGROUND);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(btnWrapper, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Selection Logic
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                selectedDoctorId = (int) tableModel.getValueAt(row, 0);
                txtName.setText((String) tableModel.getValueAt(row, 1));
                txtSpec.setText((String) tableModel.getValueAt(row, 2));
                txtPhone.setText((String) tableModel.getValueAt(row, 3));
                txtEmail.setText((String) tableModel.getValueAt(row, 4));
            }
        });

        btnAdd.addActionListener(e -> {
            if(txtName.getText().trim().isEmpty()) return;
            Doctor d = new Doctor(0, txtName.getText(), txtSpec.getText(), txtPhone.getText(), txtEmail.getText());
            if (doctorDAO.addDoctor(d)) { loadTableData(); clearFields(); }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedDoctorId != -1) {
                Doctor d = new Doctor(selectedDoctorId, txtName.getText(), txtSpec.getText(), txtPhone.getText(), txtEmail.getText());
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
        txtName.setText(""); txtSpec.setText(""); txtPhone.setText(""); txtEmail.setText("");
        selectedDoctorId = -1;
        table.clearSelection();
    }
}