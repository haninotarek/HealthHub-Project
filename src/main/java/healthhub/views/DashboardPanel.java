package healthhub.views;

import healthhub.utils.ColorPalette;
import healthhub.utils.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.awt.RenderingHints;
import java.util.Map;
import java.util.LinkedHashMap;

public class DashboardPanel extends JPanel {

    private static final Color PRIMARY    = new Color(0x11529A);
    private static final Color PRIMARY_LT = new Color(0x296FBB);
    private static final Color BG         = new Color(0xFAFAFA);
    private static final Color BLACK      = new Color(0x000000);
    private static final Color GRAY       = new Color(0xC2C3C3);
    private static final Color WHITE      = Color.WHITE;

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_VALUE  = new Font("Segoe UI", Font.BOLD,  28);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_CELL   = new Font("Segoe UI", Font.PLAIN, 12);

    private final JLabel lblPatients     = new JLabel("0");
    private final JLabel lblDoctors      = new JLabel("0");
    private final JLabel lblAppointments = new JLabel("0");
    private final JLabel lblScheduled    = new JLabel("0");

    private final DefaultTableModel tableModel;
    private JTable table;

    private final JFrame parentFrame;

    public DashboardPanel() {
        this(null);
    }

    public DashboardPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(0, 20));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tableModel = createTableModel();

        add(buildTopBar(),      BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);

        loadStats();
        loadRecentAppointments();
    }

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel lblPage = new JLabel("Dashboard");
        lblPage.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPage.setForeground(PRIMARY);

        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightSide.setOpaque(false);

        String pcUsername = System.getProperty("user.name");
        JLabel lblAdmin = new JLabel(pcUsername);
        lblAdmin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAdmin.setForeground(new Color(0x333333));

        rightSide.add(lblAdmin);
        rightSide.add(createProfileCircle(pcUsername));

        topBar.add(lblPage,   BorderLayout.WEST);
        topBar.add(rightSide, BorderLayout.EAST);

        return topBar;
    }

    private JPanel buildMainContent() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.add(buildStatsRow(), BorderLayout.NORTH);

        JPanel bottomSection = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomSection.setOpaque(false);
        bottomSection.add(buildTableCard());
        bottomSection.add(buildChartCard());

        content.add(bottomSection, BorderLayout.CENTER);
        return content;
    }

    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 100));

        row.add(buildStatCard("Total Patients",  lblPatients,     "patients"));
        row.add(buildStatCard("Total Doctors",   lblDoctors,      "doctors"));
        row.add(buildStatCard("Appointments",    lblAppointments, "appointments"));
        row.add(buildStatCard("Scheduled",       lblScheduled,    "appointments"));

        return row;
    }

    private JPanel buildStatCard(String title, JLabel valueLabel, String targetPanel) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(new Color(0x555555));

        valueLabel.setFont(FONT_VALUE);
        valueLabel.setForeground(PRIMARY);

        card.add(lblTitle,   BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(245, 248, 255));
                card.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(WHITE);
                card.repaint();
            }
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                navigateTo(targetPanel);
            }
        });

        return card;
    }

    // ── التغيير الجوهري: MainFrame → DashboardFrame في 3 أماكن ──
    private void navigateTo(String panelKey) {
        // طريقة 1: parentFrame
        if (parentFrame instanceof DashboardFrame) {
            ((DashboardFrame) parentFrame).navigateTo(panelKey);
            return;
        }
        // طريقة 2: نتسلق الـ hierarchy
        Container parent = getParent();
        while (parent != null) {
            if (parent instanceof DashboardFrame) {
                ((DashboardFrame) parent).navigateTo(panelKey);
                return;
            }
            parent = parent.getParent();
        }
        // طريقة 3: fallback
        for (Window w : Window.getWindows()) {
            if (w instanceof DashboardFrame) {
                ((DashboardFrame) w).navigateTo(panelKey);
                return;
            }
        }
    }

    private JPanel buildTableCard() {
        JPanel container = new JPanel(new BorderLayout(0, 8));
        container.setOpaque(false);

        JLabel lblTitle = new JLabel("Recent Appointments");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 2, 6, 0));
        container.add(lblTitle, BorderLayout.NORTH);

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(PRIMARY_LT);
                    c.setForeground(WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? WHITE : BG);
                    c.setForeground(BLACK);
                }
                return c;
            }
        };

        table.setFont(FONT_CELL);
        table.setRowHeight(34);
        table.setShowGrid(true);
        table.setGridColor(new Color(0xE8E8E8));
        table.setSelectionBackground(PRIMARY_LT);
        table.setSelectionForeground(WHITE);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                l.setBackground(PRIMARY);
                l.setForeground(WHITE);
                l.setFont(FONT_HEADER);
                l.setHorizontalAlignment(CENTER);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, PRIMARY_LT));
                return l;
            }
        });
        header.setPreferredSize(new Dimension(0, 38));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(GRAY, 1));
        scroll.getViewport().setBackground(WHITE);
        container.add(scroll, BorderLayout.CENTER);

        return container;
    }

    private JPanel buildChartCard() {
        JPanel container = new JPanel(new BorderLayout(0, 8));
        container.setOpaque(false);

        JLabel lblTitle = new JLabel("Appointment Distribution");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 2, 6, 0));
        container.add(lblTitle, BorderLayout.NORTH);

        JPanel chartWrapper = new JPanel(new BorderLayout()) {
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
        chartWrapper.setOpaque(false);
        chartWrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartWrapper.add(createSimpleBarChart(), BorderLayout.CENTER);

        container.add(chartWrapper, BorderLayout.CENTER);
        return container;
    }

    private JPanel createSimpleBarChart() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Map<String, Integer> chartData = getChartData();
                if (chartData.isEmpty()) {
                    g2.setColor(new Color(0xADADB2));
                    g2.drawString("No Data Available", 20, 30);
                    return;
                }

                int barWidth        = 42;
                int gap             = 35;
                int x               = 40;
                int chartAreaHeight = getHeight() - 60;
                int maxVal = chartData.values().stream().max(Integer::compare).orElse(1);

                Color[] colors = {
                        new Color(0x0984E3),
                        new Color(0x00B894),
                        new Color(0xFDCB6E),
                        new Color(0x6C5CE7),
                        new Color(0xE17055)
                };

                int i = 0;
                for (Map.Entry<String, Integer> entry : chartData.entrySet()) {
                    String label = entry.getKey();
                    int value    = entry.getValue();

                    int barHeight = (value * chartAreaHeight) / maxVal;
                    if (barHeight < 5 && value > 0) barHeight = 5;

                    g2.setColor(new Color(242, 242, 242));
                    g2.fillRoundRect(x, 15, barWidth, chartAreaHeight, 10, 10);

                    g2.setColor(colors[i % colors.length]);
                    g2.fillRoundRect(x, 15 + (chartAreaHeight - barHeight), barWidth, barHeight, 10, 10);

                    g2.setColor(new Color(0x2D3436));
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    String displayLabel = label.length() > 9 ? label.substring(0, 8) + "." : label;

                    FontMetrics fm = g2.getFontMetrics();
                    int labelX = x + (barWidth / 2) - (fm.stringWidth(displayLabel) / 2);
                    g2.drawString(displayLabel, labelX, getHeight() - 10);

                    x += barWidth + gap;
                    i++;
                    if (i >= 5) break;
                }
                g2.dispose();
            }
        };
    }

    private Map<String, Integer> getChartData() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT d.specialization, COUNT(a.id) as total " +
                "FROM doctors d " +
                "LEFT JOIN appointments a ON d.id = a.doctor_id " +
                "GROUP BY d.specialization";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String spec = rs.getString("specialization");
                if (spec != null) data.put(spec, rs.getInt("total"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    private DefaultTableModel createTableModel() {
        String[] columns = {"#", "Patient", "Doctor", "Date", "Time", "Status"};
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private void loadStats() {
        try (Connection conn = DBConnection.getConnection()) {
            lblPatients.setText(queryCount(conn, "SELECT COUNT(*) FROM patients"));
            lblDoctors.setText(queryCount(conn, "SELECT COUNT(*) FROM doctors"));
            lblAppointments.setText(queryCount(conn, "SELECT COUNT(*) FROM appointments"));
            lblScheduled.setText(queryCount(conn, "SELECT COUNT(*) FROM appointments WHERE status = 'Scheduled'"));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private String queryCount(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? String.valueOf(rs.getInt(1)) : "0";
        }
    }

    private void loadRecentAppointments() {
        String sql = "SELECT TOP 10 a.id, p.name AS p_name, d.name AS d_name, a.date, a.time, a.status " +
                "FROM appointments a JOIN patients p ON a.patient_id = p.id JOIN doctors d ON a.doctor_id = d.id " +
                "ORDER BY a.date DESC, a.time DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            tableModel.setRowCount(0);
            int row = 1;
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        row++,
                        rs.getString("p_name"),
                        rs.getString("d_name"),
                        rs.getString("date"),
                        rs.getTime("time").toString().substring(0, 5),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private JPanel createProfileCircle(String name) {
        String initial = (name != null && !name.isEmpty()) ? name.substring(0, 1).toUpperCase() : "A";
        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_LT);
                g2.fillOval(0, 0, 35, 35);
                g2.setColor(WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initial, (35 - fm.stringWidth(initial)) / 2, ((35 - fm.getHeight()) / 2) + fm.getAscent());
                g2.dispose();
            }
        };
        circle.setPreferredSize(new Dimension(35, 35));
        circle.setOpaque(false);
        return circle;
    }
}