package healthhub.views;

import healthhub.dao.DoctorDAO;
import healthhub.models.Doctor;
import healthhub.utils.ColorPalette;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DoctorDAO doctorDAO;
    private JTextField txtName, txtSpec, txtPhone, txtEmail;

    public DoctorPanel() {
        doctorDAO = new DoctorDAO();
        setLayout(new BorderLayout(15, 15));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Input Form ---
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setBackground(ColorPalette.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        txtName = new JTextField(); txtSpec = new JTextField();
        txtPhone = new JTextField(); txtEmail = new JTextField();

        inputPanel.add(new JLabel("Doctor Name:"));    inputPanel.add(txtName);
        inputPanel.add(new JLabel("Specialization:")); inputPanel.add(txtSpec);
        inputPanel.add(new JLabel("Phone:"));          inputPanel.add(txtPhone);
        inputPanel.add(new JLabel("Email:"));          inputPanel.add(txtEmail);

        // --- Add Button ---
        JButton btnAdd = new JButton("Add Doctor");
        btnAdd.setBackground(ColorPalette.PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrapper.setBackground(ColorPalette.BACKGROUND);
        btnWrapper.add(btnAdd);

        // --- Table ---
        String[] columns = {"ID", "Name", "Specialization", "Phone", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setSelectionBackground(ColorPalette.ACCENT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(table);

        // Assembly
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(btnWrapper, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Actions
        btnAdd.addActionListener(e -> {
            if (validateInputs()) {
                Doctor d = new Doctor(0, txtName.getText(), txtSpec.getText(), txtPhone.getText(), txtEmail.getText());
                if (doctorDAO.addDoctor(d)) {
                    JOptionPane.showMessageDialog(this, "Doctor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Error saving doctor data.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loadTableData();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        for (Doctor d : doctors) {
            tableModel.addRow(new Object[]{d.getId(), d.getName(), d.getSpecialization(), d.getPhone(), d.getEmail()});
        }
    }

    private boolean validateInputs() {
        if (txtName.getText().isEmpty() || txtSpec.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in the Name and Specialization fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearFields() {
        txtName.setText(""); txtSpec.setText(""); txtPhone.setText(""); txtEmail.setText("");
    }
}