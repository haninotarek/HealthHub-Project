package healthhub.views;

import healthhub.utils.ColorPalette;
import healthhub.utils.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;

public class DashboardPanel extends JPanel {

    // ============================================================
    // Constants
    // ============================================================
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_VALUE  = new Font("Segoe UI", Font.BOLD,  28);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_CELL   = new Font("Segoe UI", Font.PLAIN, 12);

    // ============================================================
    // Stat Labels
    // ============================================================
    private final JLabel lblPatients     = new JLabel("0");
    private final JLabel lblDoctors      = new JLabel("0");
    private final JLabel lblAppointments = new JLabel("0");
    private final JLabel lblScheduled    = new JLabel("0");

    // ============================================================
    // Table
    // ============================================================
    private final DefaultTableModel tableModel;

    // ============================================================
    // Constructor
    // ============================================================
    public DashboardPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(ColorPalette.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tableModel = createTableModel();

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);

        loadStats();
        loadRecentAppointments();
    }

    // ============================================================
    // TopBar
    // ============================================================
    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel lblPage = new JLabel("Dashboard");
        lblPage.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblPage.setForeground(ColorPalette.PRIMARY);

        JLabel lblAdmin = new JLabel("  👤 Admin  ");
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
    // Main Content — Stats + Table
    // ============================================================
    private JPanel buildMainContent() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        content.add(buildStatsRow(),  BorderLayout.NORTH);
        content.add(buildTableCard(), BorderLayout.CENTER);

        return content;
    }

    // ============================================================
    // Stats Row — 4 Cards
    // ============================================================
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 100));

        row.add(buildStatCard("Total Patients",     lblPatients,     ColorPalette.PRIMARY));
        row.add(buildStatCard("Total Doctors",      lblDoctors,      ColorPalette.PRIMARY));
        row.add(buildStatCard("Appointments",       lblAppointments, ColorPalette.PRIMARY));
        row.add(buildStatCard("Scheduled",          lblScheduled,    ColorPalette.PRIMARY));

        return row;
    }

    // ============================================================
    // Stat Card
    // ============================================================
    private JPanel buildStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(ColorPalette.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE5E7EB)),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(ColorPalette.TEXT_MEDIUM);

        valueLabel.setFont(FONT_VALUE);
        valueLabel.setForeground(color);

        card.add(lblTitle,   BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // ============================================================
    // Table Card
    // ============================================================
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ColorPalette.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xE5E7EB)));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ColorPalette.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel lblTitle = new JLabel("Recent Appointments");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(ColorPalette.PRIMARY);
        header.add(lblTitle, BorderLayout.WEST);

        // Table
        JTable table = new JTable(tableModel);
        styleTable(table);

        card.add(header,                 BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        return card;
    }

    // ============================================================
    // Table Model
    // ============================================================
    private DefaultTableModel createTableModel() {
        String[] columns = {"#", "Patient", "Doctor", "Date", "Time", "Status"};
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }

    // ============================================================
    // Style Table
    // ============================================================
    private void styleTable(JTable table) {
        table.getTableHeader().setFont(FONT_HEADER);
        table.getTableHeader().setBackground(new Color(0xF7F8FA));
        table.getTableHeader().setForeground(ColorPalette.TEXT_MEDIUM);

        table.setFont(FONT_CELL);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xEBF3FF));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    // ============================================================
    // Load Stats من الـ DB
    // ============================================================
    private void loadStats() {
        try (Connection conn = DBConnection.getConnection()) {
            lblPatients.setText(queryCount(conn,
                    "SELECT COUNT(*) FROM patients"));
            lblDoctors.setText(queryCount(conn,
                    "SELECT COUNT(*) FROM doctors"));
            lblAppointments.setText(queryCount(conn,
                    "SELECT COUNT(*) FROM appointments"));
            lblScheduled.setText(queryCount(conn,
                    "SELECT COUNT(*) FROM appointments WHERE status = 'Scheduled'"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // Query Count
    // ============================================================
    private String queryCount(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            return rs.next() ? String.valueOf(rs.getInt(1)) : "0";
        }
    }

    // ============================================================
    // Load Recent Appointments
    // ============================================================
    private void loadRecentAppointments() {
        String sql = """
            SELECT TOP 10
                a.id,
                p.name AS patient_name,
                d.name AS doctor_name,
                a.date,
                a.time,
                a.status
            FROM appointments a
            JOIN patients p ON a.patient_id = p.id
            JOIN doctors  d ON a.doctor_id  = d.id
            ORDER BY a.date DESC, a.time DESC
            """;

        try (Connection conn = DBConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            tableModel.setRowCount(0);

            int row = 1;
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        row++,
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("date"),
                        rs.getTime("time").toString().substring(0, 5),
                        rs.getString("status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}