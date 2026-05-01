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
    private int selectedDoctorId = -1; // لحفظ الـ ID المختار للتعديل أو الحذف

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

        // --- Buttons Area ---
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");

        // Styling
        btnAdd.setBackground(ColorPalette.SUCCESS); btnAdd.setForeground(Color.WHITE);
        btnUpdate.setBackground(ColorPalette.PRIMARY); btnUpdate.setForeground(Color.WHITE);
        btnDelete.setBackground(ColorPalette.DANGER); btnDelete.setForeground(Color.WHITE);

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrapper.setBackground(ColorPalette.BACKGROUND);
        btnWrapper.add(btnAdd); btnWrapper.add(btnUpdate); btnWrapper.add(btnDelete);

        // --- Table ---
        String[] columns = {"ID", "Name", "Specialization", "Phone", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        // Assembly
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(btnWrapper, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Selection Logic (عند الضغط على سطر في الجدول) ---
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

        // --- Action Listeners ---
        btnAdd.addActionListener(e -> {
            Doctor d = new Doctor(0, txtName.getText(), txtSpec.getText(), txtPhone.getText(), txtEmail.getText());
            if (doctorDAO.addDoctor(d)) { loadTableData(); clearFields(); }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedDoctorId != -1) {
                Doctor d = new Doctor(selectedDoctorId, txtName.getText(), txtSpec.getText(), txtPhone.getText(), txtEmail.getText());
                if (doctorDAO.updateDoctor(d)) {
                    JOptionPane.showMessageDialog(this, "Updated!");
                    loadTableData(); clearFields();
                }
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedDoctorId != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete this doctor?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (doctorDAO.deleteDoctor(selectedDoctorId)) { loadTableData(); clearFields(); }
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

    private void clearFields() {
        txtName.setText(""); txtSpec.setText(""); txtPhone.setText(""); txtEmail.setText("");
        selectedDoctorId = -1;
        table.clearSelection();
    }
}