package healthhub.views;

import healthhub.dao.PatientDAO;
import healthhub.models.Patient;
import healthhub.utils.ColorPalette;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class PatientUI extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private PatientDAO patientDAO;

    private JTextField txtName, txtPhone, txtAge, txtSearch;
    private JComboBox<String> cmbGender;

    private int selectedPatientId = -1;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public PatientUI() {
        patientDAO = new PatientDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ================== FORM (FIXED) ==================
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(ColorPalette.PRIMARY),
                        " Patient Management ",
                        TitledBorder.LEFT, TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        ColorPalette.PRIMARY
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName = createField();
        txtPhone = createField();
        txtAge = createField();
        cmbGender = new JComboBox<>(new String[]{"Male", "Female"});
        cmbGender.setBorder(BorderFactory.createLineBorder(ColorPalette.PRIMARY, 1));

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        form.add(txtName, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(new JLabel("Phone:"), gbc);

        gbc.gridx = 3; gbc.weightx = 1;
        form.add(txtPhone, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("Age:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        form.add(txtAge, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        form.add(new JLabel("Gender:"), gbc);

        gbc.gridx = 3; gbc.weightx = 1;
        form.add(cmbGender, gbc);

        // ================== BUTTONS ==================
        JButton btnAdd = createButton("Add Patient", ColorPalette.SUCCESS);
        JButton btnUpdate = createButton("Update", ColorPalette.PRIMARY);
        JButton btnDelete = createButton("Delete", ColorPalette.DANGER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(ColorPalette.BACKGROUND);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        // ================== SEARCH ==================
        txtSearch = createField();
        txtSearch.setToolTipText("Search by name...");

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(ColorPalette.BACKGROUND);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        searchPanel.add(new JLabel("🔍 Search:"), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        // ================== TABLE ==================
        String[] cols = {"ID", "Name", "Phone", "Age", "Gender"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);

        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(220, 235, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(ColorPalette.PRIMARY);
        table.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, ColorPalette.PRIMARY)
        );

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }

                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        // ================== LAYOUT ==================
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(ColorPalette.BACKGROUND);
        top.add(form, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(ColorPalette.BACKGROUND);
        bottom.add(searchPanel, BorderLayout.NORTH);
        bottom.add(scroll, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(bottom, BorderLayout.CENTER);

        // ================== SEARCH ==================
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String text = txtSearch.getText();
                if (text.isEmpty()) rowSorter.setRowFilter(null);
                else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
            }
        });

        // ================== SELECT ==================
        table.getSelectionModel().addListSelectionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow != -1) {
                int row = table.convertRowIndexToModel(viewRow);

                selectedPatientId = (int) tableModel.getValueAt(row, 0);
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtPhone.setText(tableModel.getValueAt(row, 2).toString());
                txtAge.setText(tableModel.getValueAt(row, 3).toString());
                cmbGender.setSelectedItem(tableModel.getValueAt(row, 4));
            }
        });

        // ================== ACTIONS ==================
        btnAdd.addActionListener(e -> {
            if (txtName.getText().trim().isEmpty()) return;

            try {
                Patient p = new Patient(
                        0,
                        txtName.getText(),
                        txtPhone.getText(),
                        Integer.parseInt(txtAge.getText()),
                        cmbGender.getSelectedItem().toString()
                );

                if (patientDAO.addPatient(p)) {
                    loadData();
                    clear();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid data");
            }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedPatientId != -1) {
                try {
                    Patient p = new Patient(
                            selectedPatientId,
                            txtName.getText(),
                            txtPhone.getText(),
                            Integer.parseInt(txtAge.getText()),
                            cmbGender.getSelectedItem().toString()
                    );

                    if (patientDAO.updatePatient(p)) {
                        loadData();
                        clear();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid data");
                }
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedPatientId != -1) {

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this patient?\nThis action cannot be undone.",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    if (patientDAO.deletePatient(selectedPatientId)) {
                        loadData();
                        clear();
                    }
                }
            }
        });

        loadData();
    }

    private JTextField createField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setPreferredSize(new Dimension(140, 26));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.PRIMARY, 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        return f;
    }

    private JButton createButton(String text, Color color) {
        JButton b = new JButton(text);

        Color bg;
        Color fg;

        if (color.equals(ColorPalette.SUCCESS)) {
            bg = new Color(220, 248, 230);
            fg = new Color(40, 167, 69);
        } else if (color.equals(ColorPalette.PRIMARY)) {
            bg = new Color(220, 235, 255);
            fg = new Color(13, 110, 253);
        } else {
            bg = new Color(255, 230, 230);
            fg = new Color(220, 53, 69);
        }

        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(110, 32));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(bg);
            }
        });

        return b;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Patient> list = patientDAO.getAllPatients();

        for (Patient p : list) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getPhone(),
                    p.getAge(), p.getGender()
            });
        }
    }

    private void clear() {
        txtName.setText("");
        txtPhone.setText("");
        txtAge.setText("");
        txtSearch.setText("");
        selectedPatientId = -1;
        table.clearSelection();
    }
}