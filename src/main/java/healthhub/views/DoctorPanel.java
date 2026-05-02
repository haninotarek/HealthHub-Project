package healthhub.views;

import healthhub.dao.DoctorDAO;
import healthhub.models.Doctor;
import healthhub.utils.ColorPalette;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class DoctorPanel extends JPanel {
    private static final Color PRIMARY    = new Color(0x11529A);
    private static final Color PRIMARY_LT = new Color(0x296FBB);
    private static final Color BG         = new Color(0xF4F6F8);
    private static final Color WHITE      = Color.WHITE;
    private static final Color GRAY       = new Color(0xC2C3C3);
    private static final Color SUCCESS    = new Color(0x2E7D32);
    private static final Color DANGER     = new Color(0xD32F2F);

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
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildTopBar(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(buildFormCard(),     BorderLayout.NORTH);
        center.add(buildTableSection(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        loadTableData();
    }

    // ── TopBar ─────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel title = new JLabel("Doctors");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);

        bar.add(title, BorderLayout.WEST);
        return bar;
    }

    // ── Form Card ──────────────────────────────────
    private JPanel buildFormCard() {
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
        JLabel cardTitle = new JLabel("Doctor Management");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cardTitle.setForeground(PRIMARY);
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(cardTitle, gbc);
        gbc.gridwidth = 1;

        // Row labels
        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.15;
        card.add(fieldLabel("Doctor Name"), gbc);
        gbc.gridx = 2;
        card.add(fieldLabel("Specialization"), gbc);

        // Row fields
        gbc.gridy = 2; gbc.gridx = 0; gbc.weightx = 0.35;
        txtName = createStyledField();
        card.add(txtName, gbc);

        gbc.gridx = 2;
        String[] specializations = {
                "General Medicine", "Pediatrics", "Cardiology",
                "Dermatology", "Neurology", "Orthopedics", "Dentistry"
        };
        comboSpec = new JComboBox<>(specializations);
        styleComboBox(comboSpec);
        card.add(comboSpec, gbc);

        // Row 2 labels
        gbc.gridy = 3; gbc.gridx = 0; gbc.weightx = 0.15;
        card.add(fieldLabel("Phone"), gbc);
        gbc.gridx = 2;
        card.add(fieldLabel("Email"), gbc);

        // Row 2 fields
        gbc.gridy = 4; gbc.gridx = 0; gbc.weightx = 0.35;
        txtPhone = createStyledField();
        card.add(txtPhone, gbc);

        gbc.gridx = 2;
        txtEmail = createStyledField();
        card.add(txtEmail, gbc);

        // Buttons row
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 4;
        gbc.insets = new Insets(16, 8, 4, 8);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnAdd    = createRoundedButton("Add Doctor", SUCCESS,  true);
        JButton btnUpdate = createRoundedButton("Update",     PRIMARY,  true);
        JButton btnDelete = createRoundedButton("Delete",     DANGER,   true);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        card.add(btnPanel, gbc);

        // Listeners
        btnAdd.addActionListener(e -> {
            if (txtName.getText().trim().isEmpty()) return;
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

        return card;
    }

    // ── Table Section ──────────────────────────────
    private JPanel buildTableSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("Search Doctor: ");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSearch.setForeground(new Color(0x333333));

        txtSearch = createStyledField();
        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        // Table
        String[] columns = {"ID", "Name", "Specialization", "Phone", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

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

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(GRAY, 1));
        scroll.getViewport().setBackground(WHITE);

        // Search Logic
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

        // Selection
        table.getSelectionModel().addListSelectionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow != -1) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                selectedDoctorId = (int) tableModel.getValueAt(modelRow, 0);
                txtName.setText((String) tableModel.getValueAt(modelRow, 1));
                String spec = (String) tableModel.getValueAt(modelRow, 2);
                comboSpec.setSelectedItem(spec);
                txtPhone.setText((String) tableModel.getValueAt(modelRow, 3));
                txtEmail.setText((String) tableModel.getValueAt(modelRow, 4));
            }
        });

        wrapper.add(searchPanel, BorderLayout.NORTH);
        wrapper.add(scroll,      BorderLayout.CENTER);
        return wrapper;
    }

    // ── Helpers ────────────────────────────────────
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(0x444444));
        return l;
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD5E0), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        field.setPreferredSize(new Dimension(0, 38));
        return field;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(WHITE);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(0, 38));
    }

    private JButton createRoundedButton(String text, Color color, boolean solid) {
        final Color baseColor = color;
        final boolean isSolid = solid;

        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);

        if (isSolid) btn.setForeground(WHITE);
        else         btn.setForeground(baseColor);

        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        javax.swing.plaf.basic.BasicButtonUI ui = new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton b = (AbstractButton) c;

                if (isSolid) {
                    Color fill = b.getModel().isRollover() ? baseColor.darker() : baseColor;
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
        return btn;
    }

    // ── Data ───────────────────────────────────────
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
}