package healthhub.views;

import healthhub.dao.PatientDAO;
import healthhub.models.Patient;
import healthhub.utils.ColorPalette;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class PatientUI extends JPanel {

    // ============================================================
    // Constants — fonts reused across the panel
    // ============================================================
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_CELL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  12);

    // ============================================================
    // Table components
    // ============================================================
    private JTable table;
    private DefaultTableModel tableModel;

    // ============================================================
    // Search field
    // ============================================================
    private JTextField searchField;

    // ============================================================
    // DAO — handles all database operations for patients
    // ============================================================
    private final PatientDAO patientDAO = new PatientDAO();

    // ============================================================
    // Constructor
    // ============================================================
    public PatientUI() {
        // Use BorderLayout as the main layout — same as AppointmentsPanel
        setLayout(new BorderLayout(0, 0));
        setBackground(ColorPalette.BACKGROUND);

        // Build and add the topbar at the top
        add(buildTopBar(), BorderLayout.NORTH);

        // Build and add the main content (table card) in the center
        add(buildContentArea(), BorderLayout.CENTER);

        // Load all patients from the database when the panel opens
        loadAllPatients();
    }

    // ============================================================
    // TopBar — page title + admin badge
    // Matches the topbar style in DashboardPanel.java
    // ============================================================
    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE5E7EB)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Page title on the left
        JLabel lblPage = new JLabel("Patients");
        lblPage.setFont(FONT_TITLE);
        lblPage.setForeground(ColorPalette.PRIMARY);

        // Admin badge on the right (same style as DashboardPanel)
        JLabel lblAdmin = new JLabel("  \uD83D\uDC64 Admin  ");
        lblAdmin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAdmin.setForeground(Color.WHITE);
        lblAdmin.setBackground(ColorPalette.PRIMARY);
        lblAdmin.setOpaque(true);
        lblAdmin.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        topBar.add(lblPage,  BorderLayout.WEST);
        topBar.add(lblAdmin, BorderLayout.EAST);

        return topBar;
    }

    // ============================================================
    // Content Area — wraps the table card with some padding
    // ============================================================
    private JPanel buildContentArea() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(ColorPalette.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        content.add(buildTableCard(), BorderLayout.CENTER);

        return content;
    }

    // ============================================================
    // Table Card — white card containing the search bar,
    //              add button, and the JTable
    // Matches the "table-card" style from the HTML preview
    // ============================================================
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xE5E7EB)));

        // Card header: title + search + add button
        card.add(buildCardHeader(), BorderLayout.NORTH);

        // Card body: the actual JTable inside a scroll pane
        card.add(buildTable(), BorderLayout.CENTER);

        return card;
    }

    // ============================================================
    // Card Header — "All Patients" label + search field + Add button
    // ============================================================
    private JPanel buildCardHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE5E7EB)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        // "All Patients" label on the left
        JLabel lblTitle = new JLabel("All Patients");
        lblTitle.setFont(FONT_LABEL);
        lblTitle.setForeground(ColorPalette.PRIMARY);

        // Right side: search field + add button grouped together
        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightSide.setBackground(Color.WHITE);

        // Search field — live filtering as the user types
        searchField = new JTextField(16);
        searchField.setFont(FONT_CELL);
        searchField.setToolTipText("Search by name...");
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD5E0)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        // Attach a DocumentListener for live search
        // Every time the user types a character, filterPatients() is called
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { filterPatients(); }
            @Override public void removeUpdate(DocumentEvent e)  { filterPatients(); }
            @Override public void changedUpdate(DocumentEvent e) { filterPatients(); }
        });

        // Add Patient button — opens the add dialog when clicked
        JButton btnAdd = new JButton("+ Add Patient");
        btnAdd.setFont(FONT_BTN);
        btnAdd.setBackground(ColorPalette.PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> openAddDialog());

        rightSide.add(searchField);
        rightSide.add(btnAdd);

        header.add(lblTitle,  BorderLayout.WEST);
        header.add(rightSide, BorderLayout.EAST);

        return header;
    }

    // ============================================================
    // Table — JTable inside JScrollPane
    // Columns: #, Name, Phone, Age, Gender, Actions
    // ============================================================
    private JScrollPane buildTable() {
        // Column names matching the UI design
        String[] columns = {"#", "Name", "Phone", "Age", "Gender", "Actions"};

        // DefaultTableModel with isCellEditable = false so users can't
        // directly type into cells (they must use Edit button)
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Only the Actions column (col 5) should NOT be "editable"
                // but we return false for ALL because buttons handle editing
                return false;
            }
        };

        table = new JTable(tableModel);
        styleTable();

        // Custom renderer for the Actions column — renders Edit + Delete buttons
        // using a panel with two buttons painted inside each cell
        table.getColumn("Actions").setCellRenderer(new ActionCellRenderer());

        // MouseListener — detects clicks on Edit/Delete areas in the Actions column
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                // Only respond to clicks in the Actions column (index 5)
                if (col == 5 && row >= 0) {
                    // Determine whether Edit or Delete was clicked
                    // based on the x position within the cell
                    Rectangle cellRect = table.getCellRect(row, col, false);
                    int xInCell = e.getX() - cellRect.x;

                    if (xInCell < cellRect.width / 2) {
                        // Left half → Edit clicked
                        handleEdit(row);
                    } else {
                        // Right half → Delete clicked
                        handleDelete(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    // ============================================================
    // Style Table — applies visual styling to the JTable
    // Matches the table style from DashboardPanel.java
    // ============================================================
    private void styleTable() {
        table.setFont(FONT_CELL);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xEBF3FF));
        table.setFillsViewportHeight(true);

        // Header styling
        table.getTableHeader().setFont(FONT_HEADER);
        table.getTableHeader().setBackground(new Color(0xF7F8FA));
        table.getTableHeader().setForeground(ColorPalette.TEXT_MEDIUM);
        table.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE5E7EB))
        );

        // Center-align all columns except Actions
        DefaultTableModel m = tableModel;
        for (int i = 0; i < 5; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(
                    new javax.swing.table.DefaultTableCellRenderer() {
                        { setHorizontalAlignment(SwingConstants.CENTER); }
                    }
            );
        }

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);  // #
        table.getColumnModel().getColumn(1).setPreferredWidth(160); // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(130); // Phone
        table.getColumnModel().getColumn(3).setPreferredWidth(50);  // Age
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Gender
        table.getColumnModel().getColumn(5).setPreferredWidth(130); // Actions
    }

    // ============================================================
    // Load All Patients — fetches every patient from the DB
    // and populates the table
    // ============================================================
    private void loadAllPatients() {
        tableModel.setRowCount(0); // clear existing rows first

        List<Patient> patients = patientDAO.getAllPatients();

        int rowNum = 1;
        for (Patient p : patients) {
            tableModel.addRow(new Object[]{
                    rowNum++,
                    p.getName(),
                    p.getPhone(),
                    p.getAge(),
                    p.getGender(),
                    "actions" // placeholder — actual buttons rendered by ActionCellRenderer
            });
        }
    }

    // ============================================================
    // Filter Patients — called on every keystroke in the search field
    // Uses PatientDAO.searchPatientsByName() for DB-side filtering
    // ============================================================
    private void filterPatients() {
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0); // clear table before re-filling

        List<Patient> results;

        if (keyword.isEmpty()) {
            // No keyword → show all patients
            results = patientDAO.getAllPatients();
        } else {
            // Keyword present → search by name in DB
            results = patientDAO.searchPatientsByName(keyword);
        }

        int rowNum = 1;
        for (Patient p : results) {
            tableModel.addRow(new Object[]{
                    rowNum++,
                    p.getName(),
                    p.getPhone(),
                    p.getAge(),
                    p.getGender(),
                    "actions"
            });
        }
    }

    // ============================================================
    // Open Add Dialog — shows a dialog to enter new patient data
    // ============================================================
    private void openAddDialog() {
        // Build the input fields
        JTextField nameField   = new JTextField();
        JTextField phoneField  = new JTextField();
        JTextField ageField    = new JTextField();
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female"});

        // Arrange fields in a small grid panel
        JPanel form = buildFormPanel(
                new String[]{"Name:", "Phone:", "Age:", "Gender:"},
                new JComponent[]{nameField, phoneField, ageField, genderCombo}
        );

        int result = JOptionPane.showConfirmDialog(
                this, form,
                "Add New Patient",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // User clicked OK
        if (result == JOptionPane.OK_OPTION) {
            // Validate that required fields are not empty
            String name  = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String ageStr = ageField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();

            if (name.isEmpty() || phone.isEmpty() || ageStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Parse age safely
            int age;
            try {
                age = Integer.parseInt(ageStr);
                if (age <= 0 || age > 150) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid age (1–150).",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create Patient object (id = 0 because DB will auto-assign it)
            Patient newPatient = new Patient(0, name, phone, age, gender);

            // Call DAO to insert into DB
            boolean success = patientDAO.addPatient(newPatient);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Patient added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadAllPatients(); // refresh table
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add patient. Check the database connection.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ============================================================
    // Handle Edit — reads the selected row and opens edit dialog
    // row = the row index in the table that was clicked
    // ============================================================
    private void handleEdit(int row) {
        // Read current values from the table row
        String currentName   = tableModel.getValueAt(row, 1).toString();
        String currentPhone  = tableModel.getValueAt(row, 2).toString();
        String currentAge    = tableModel.getValueAt(row, 3).toString();
        String currentGender = tableModel.getValueAt(row, 4).toString();

        // Pre-fill the dialog fields with existing data
        JTextField nameField  = new JTextField(currentName);
        JTextField phoneField = new JTextField(currentPhone);
        JTextField ageField   = new JTextField(currentAge);
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
        genderCombo.setSelectedItem(currentGender);

        JPanel form = buildFormPanel(
                new String[]{"Name:", "Phone:", "Age:", "Gender:"},
                new JComponent[]{nameField, phoneField, ageField, genderCombo}
        );

        int result = JOptionPane.showConfirmDialog(
                this, form,
                "Edit Patient",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name   = nameField.getText().trim();
            String phone  = phoneField.getText().trim();
            String ageStr = ageField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();

            // Validate fields
            if (name.isEmpty() || phone.isEmpty() || ageStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr);
                if (age <= 0 || age > 150) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid age (1–150).",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // We need the real patient ID from the DB.
            // We find it by searching the name from the original row.
            // This is safe because we search for the exact original name.
            List<Patient> matches = patientDAO.searchPatientsByName(currentName);

            if (matches.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Could not find the patient in the database.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Use the first exact match (name is a good enough identifier here)
            Patient target = null;
            for (Patient p : matches) {
                if (p.getName().equals(currentName)
                        && p.getPhone().equals(currentPhone)
                        && p.getAge() == Integer.parseInt(currentAge)) {
                    target = p;
                    break;
                }
            }

            if (target == null) {
                JOptionPane.showMessageDialog(this,
                        "Could not uniquely identify the patient.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update the patient object with new values
            target.setName(name);
            target.setPhone(phone);
            target.setAge(age);
            target.setGender(gender);

            // Call DAO to update in DB
            boolean success = patientDAO.updatePatient(target);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Patient updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadAllPatients(); // refresh table
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update patient.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ============================================================
    // Handle Delete — confirms and deletes the selected patient
    // row = the row index in the table that was clicked
    // ============================================================
    private void handleDelete(int row) {
        String patientName = tableModel.getValueAt(row, 1).toString();
        String patientPhone = tableModel.getValueAt(row, 2).toString();
        String patientAge = tableModel.getValueAt(row, 3).toString();

        // Ask for confirmation before deleting
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete patient: " + patientName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        // Find the patient's real ID from the DB by matching name + phone + age
        List<Patient> matches = patientDAO.searchPatientsByName(patientName);
        Patient target = null;
        for (Patient p : matches) {
            if (p.getName().equals(patientName)
                    && p.getPhone().equals(patientPhone)
                    && p.getAge() == Integer.parseInt(patientAge)) {
                target = p;
                break;
            }
        }

        if (target == null) {
            JOptionPane.showMessageDialog(this,
                    "Could not find the patient in the database.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call DAO to delete from DB using the real ID
        boolean success = patientDAO.deletePatient(target.getId());

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Patient deleted successfully.",
                    "Deleted",
                    JOptionPane.INFORMATION_MESSAGE);
            loadAllPatients(); // refresh table
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to delete patient.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // Build Form Panel — reusable helper that creates a grid of
    // label + input field pairs, used in Add and Edit dialogs
    // ============================================================
    private JPanel buildFormPanel(String[] labels, JComponent[] fields) {
        JPanel panel = new JPanel(new GridLayout(labels.length, 2, 8, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            panel.add(lbl);
            panel.add(fields[i]);
        }

        return panel;
    }

    // ============================================================
    // ActionCellRenderer — draws the Edit + Delete buttons inside
    // each row's Actions cell using a JPanel with two JButtons.
    //
    // Note: The buttons here are only VISUAL (for display).
    // Actual clicks are handled by the MouseListener on the JTable.
    // ============================================================
    private static class ActionCellRenderer
            implements javax.swing.table.TableCellRenderer {

        // Reusable panel drawn for every cell in the Actions column
        private final JPanel panel   = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        private final JButton btnEdit   = new JButton("Edit");
        private final JButton btnDelete = new JButton("Delete");

        public ActionCellRenderer() {
            // Style Edit button — blue
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 10));
            btnEdit.setBackground(new Color(0xBEE3F8));
            btnEdit.setForeground(new Color(0x2C5282));
            btnEdit.setBorderPainted(false);
            btnEdit.setFocusPainted(false);
            btnEdit.setPreferredSize(new Dimension(52, 24));

            // Style Delete button — red
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 10));
            btnDelete.setBackground(new Color(0xFED7D7));
            btnDelete.setForeground(new Color(0x822727));
            btnDelete.setBorderPainted(false);
            btnDelete.setFocusPainted(false);
            btnDelete.setPreferredSize(new Dimension(52, 24));

            panel.setOpaque(true);
            panel.add(btnEdit);
            panel.add(btnDelete);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {

            // Match row selection background
            panel.setBackground(isSelected
                    ? new Color(0xEBF3FF)
                    : Color.WHITE);

            return panel;
        }
    }
}
