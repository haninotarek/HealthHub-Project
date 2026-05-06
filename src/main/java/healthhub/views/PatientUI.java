package healthhub.views;

import healthhub.dao.PatientDAO;
import healthhub.models.Patient;
import healthhub.utils.ColorPalette;
// استخدمت swing , awt ,table ,event
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PatientUI extends JPanel { //1. use container كلاس بيورث من panel

    private JTable table; // عرض البيانات في (شكل)table
    private DefaultTableModel tableModel; //يخزن البيانات
    private PatientDAO patientDAO; // object from class PatientDAO

    private JTextField txtName, txtPhone, txtAge, txtSearch; //components (text field)
    private JComboBox<String> cmbGender; //(Dropdown)

    private int selectedPatientId = -1; // id بيخزن المريض
    private TableRowSorter<DefaultTableModel> rowSorter; //object مسؤل عن الترتيب في الجدول

    public PatientUI() { // constructor
        patientDAO = new PatientDAO(); // object from database

        setLayout(new BorderLayout(0, 16)); // قسمت بيها الشاشه
        setBackground(ColorPalette.BACKGROUND); // استخدام Palette الموحدة وتحدد لون الخلفيه
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // add padding

        // العنوان العلوي ب اسنخدام component (label)
        JLabel title = new JLabel("Patients");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22)); // شكل ونوع الخط
        title.setForeground(ColorPalette.PRIMARY); // لون الخط بنحدد
        add(title, BorderLayout.NORTH); // اعلي الشاشه

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false); // الخلفيه شفافه
        center.add(buildFormCard(),     BorderLayout.NORTH);
        center.add(buildTableSection(), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        loadData(); // بتجيب البيانات من الداتا تحطها في الجدول
    }

    // ── Form Card (نفس شكل الدكاترة المنظم) ────────────────
    private JPanel buildFormCard() {
        JPanel card = new JPanel(new GridBagLayout()) { // بتسمح بتحديد كل عنصر بدقه
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(220, 220, 220));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();// نقفل الرسم وننضف الموارد
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL; //عناصر تتمدد افقي
        gbc.insets  = new Insets(6, 8, 6, 8);
        gbc.weightx = 1.0;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        JLabel cardTitle = new JLabel("Patient Management"); //label
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cardTitle.setForeground(ColorPalette.PRIMARY);
        cardTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(cardTitle, gbc);
        gbc.gridwidth = 1;

        // Row 1
        gbc.gridy = 1; gbc.gridx = 0; card.add(new JLabel("Name"), gbc);
        gbc.gridx = 2; card.add(new JLabel("Phone"), gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        txtName = createField(); card.add(txtName, gbc);
        gbc.gridx = 2;
        txtPhone = createField(); card.add(txtPhone, gbc);

        // Row 2
        gbc.gridy = 3; gbc.gridx = 0; card.add(new JLabel("Age"), gbc);
        gbc.gridx = 2; card.add(new JLabel("Gender"), gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        txtAge = createField(); card.add(txtAge, gbc);
        gbc.gridx = 2;
        cmbGender = new JComboBox<>(new String[]{"Male", "Female"});
        styleComboBox(cmbGender);
        card.add(cmbGender, gbc);

        // Buttons (ألوان الجديدة)
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 4;
        gbc.insets = new Insets(16, 8, 4, 8);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        // تطبيق نفس الالوان  هنا
        JButton btnAdd    = createStyledButton("Add Patient", new Color(235, 255, 235), new Color(40, 167, 69));
        JButton btnUpdate = createStyledButton("Update",      new Color(235, 245, 255), ColorPalette.PRIMARY);
        JButton btnDelete = createStyledButton("Delete",      new Color(255, 235, 235), ColorPalette.DANGER);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        card.add(btnPanel, gbc);

        // Listeners (add,update,delete)
        btnAdd.addActionListener(e -> { // event
            if (txtName.getText().trim().isEmpty()) return;
            try {
                Patient p = new Patient(0, txtName.getText(), txtPhone.getText(),
                        Integer.parseInt(txtAge.getText()), cmbGender.getSelectedItem().toString());
                if (patientDAO.addPatient(p)) { loadData(); clear(); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid data"); }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedPatientId != -1) {
                try {
                    Patient p = new Patient(selectedPatientId, txtName.getText(), txtPhone.getText(),
                            Integer.parseInt(txtAge.getText()), cmbGender.getSelectedItem().toString());
                    if (patientDAO.updatePatient(p)) { loadData(); clear(); }
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid data"); } // لو في خطا
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedPatientId != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete Patient?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (patientDAO.deletePatient(selectedPatientId)) { loadData(); clear(); }
                }
            }
        });

        return card;
    }

    // ── Table Section (أزرق ومتسنتر ) ───────────
    private JPanel buildTableSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        // Search
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel(" Search: ");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtSearch = createField();
        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        // Table
        String[] cols = {"ID", "Name", "Phone", "Age", "Gender"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(230, 230, 230));

        // هيدر الجدول أزرق
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setBackground(ColorPalette.PRIMARY);
                l.setForeground(Color.WHITE);
                l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                l.setHorizontalAlignment(JLabel.CENTER);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
                return l;
            }
        });

        // توسيط المحتوى
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JScrollPane scroll = new JScrollPane(table); // بنحط الجدول داخل scroll عشان لو البيانات كتير
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1)); //حدود حولين الجدول

        // Search Logic
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText();
                if (text.isEmpty()) rowSorter.setRowFilter(null);
                else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1)); // A=a
            }
        });

        // Selection Logic اختيار صف من الجدول
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

        wrapper.add(searchPanel, BorderLayout.NORTH);
        wrapper.add(scroll,      BorderLayout.CENTER);
        return wrapper;
    }

    // ── Helpers ────────────────────────────────────
    private JTextField createField() { // داله بتعمل تيكست فيلد جاهز
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD5E0), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(Color.WHITE);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(0, 38));
    }

    // ميثود الزراير الموحدة (نفس شكل الدكاترة)
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(fg, 1));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(fg); btn.setForeground(Color.WHITE); } //يغير لون
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(bg); btn.setForeground(fg); } //يرجع لون
        });
        return btn;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Patient> list = patientDAO.getAllPatients();
        for (Patient p : list) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getPhone(), p.getAge(), p.getGender()}); // مسح البيانات القديمه
        }
    }

    private void clear() {
        txtName.setText(""); txtPhone.setText(""); txtAge.setText(""); txtSearch.setText("");
        selectedPatientId = -1; table.clearSelection();
    }
}