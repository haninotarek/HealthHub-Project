package healthhub.views;

import healthhub.utils.ColorPalette;
import healthhub.utils.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.awt.geom.*; // مهمة للرسم
import java.awt.RenderingHints;

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

    public DashboardPanel() {
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

        // كلمة Dashboard اللي على الشمال
        JLabel lblPage = new JLabel("Dashboard");
        lblPage.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPage.setForeground(PRIMARY);

        // الجزء اللي على اليمين (الاسم والدائرة)
        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightSide.setOpaque(false);

        // كلمة Admin أو اسمك
        JLabel lblAdmin = new JLabel("Admin");
        lblAdmin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAdmin.setForeground(new Color(0x333333));

        // بننادي على ميثود الدائرة اللي عملناها في الخطوة الأولى
        // تقدري تغيري "Kariman" لأي اسم تحبيه
        JPanel profileIcon = createProfileCircle("Kariman");

        rightSide.add(lblAdmin);
        rightSide.add(profileIcon);

        topBar.add(lblPage,  BorderLayout.WEST);
        topBar.add(rightSide, BorderLayout.EAST);

        return topBar;
    }
    private JPanel buildMainContent() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.add(buildStatsRow(),  BorderLayout.NORTH);
        content.add(buildTableCard(), BorderLayout.CENTER);
        return content;
    }

    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 100));

        row.add(buildStatCard("Total Patients",  lblPatients));
        row.add(buildStatCard("Total Doctors",   lblDoctors));
        row.add(buildStatCard("Appointments",    lblAppointments));
        row.add(buildStatCard("Scheduled",       lblScheduled));

        return row;
    }

    private JPanel buildStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // تأثير تغيير اللون عند مرور الماوس
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(GRAY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(WHITE); // اللون الافتراضي
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR)); // تغيير شكل الماوس ليد

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(new Color(0x555555));

        valueLabel.setFont(FONT_VALUE);
        valueLabel.setForeground(PRIMARY);

        card.add(lblTitle,   BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        // إضافة التفاعل (الضغط والمرور)
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(245, 248, 255)); // لون خفيف عند المرور
                card.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(WHITE); // العودة للأبيض
                card.repaint();
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // البحث عن الـ Frame الأساسي لتبديل الصفحة
                Window ancestor = SwingUtilities.getWindowAncestor(card);
                if (ancestor instanceof DashboardFrame) {
                    DashboardFrame frame = (DashboardFrame) ancestor;
                    if (title.contains("Patients")) frame.showPanel(new PatientUI());
                    else if (title.contains("Doctors")) frame.showPanel(new DoctorPanel());
                    else if (title.contains("Appointments")) frame.showPanel(new AppointmentsPanel());
                    else if (title.contains("Scheduled")) frame.showPanel(new AppointmentsPanel());
                }
            }
        });

        return card;
    }
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(BG);

        JLabel lblTitle = new JLabel("Recent Appointments");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(PRIMARY);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(4, 2, 6, 0));
        card.add(lblTitle, BorderLayout.NORTH);

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(PRIMARY_LT);
                    c.setForeground(WHITE);
                } else if (col != 5) {
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
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(PRIMARY_LT);
        table.setSelectionForeground(WHITE);
        table.setFillsViewportHeight(true);
        table.setBackground(WHITE);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
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
        header.setBackground(PRIMARY);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);

        table.getColumnModel().getColumn(5).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                        super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                        setHorizontalAlignment(CENTER);
                        setFont(new Font("Segoe UI", Font.BOLD, 11));
                        if (!sel) {
                            String s = val != null ? val.toString() : "";
                            switch (s) {
                                case "Scheduled" -> { setForeground(PRIMARY);              setBackground(new Color(0xE3EDF8)); }
                                case "Completed" -> { setForeground(new Color(0x2E7D32)); setBackground(new Color(0xE8F5E9)); }
                                case "Cancelled" -> { setForeground(new Color(0xC62828)); setBackground(new Color(0xFFEBEE)); }
                                default          -> { setForeground(BLACK);               setBackground(WHITE); }
                            }
                        }
                        return this;
                    }
                }
        );

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(GRAY, 1));
        scroll.getViewport().setBackground(BG);

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private DefaultTableModel createTableModel() {
        String[] columns = {"#", "Patient", "Doctor", "Date", "Time", "Status"};
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

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

    private String queryCount(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            return rs.next() ? String.valueOf(rs.getInt(1)) : "0";
        }
    }

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
    // ميثود مسؤولة عن رسم دائرة البروفايل
    private JPanel createProfileCircle(String name) {
        // بناخد أول حرف من الاسم ونخليه Capital
        String initial = (name != null && !name.isEmpty()) ? name.substring(0, 1).toUpperCase() : "A";

        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                // تفعيل تنعيم الحواف عشان الدائرة تطلع مظبوطة مش مشرشرة
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. رسم الدائرة (باللون الأزرق الفاتح بتاعك)
                g2.setColor(new Color(0x296FBB));
                g2.fillOval(0, 0, 35, 35);

                // 2. تنسيق الخط ولونه (أبيض)
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));

                // 3. حسابات حسابية عشان الحرف يجي في نص الدائرة بالظبط
                FontMetrics fm = g2.getFontMetrics();
                int x = (35 - fm.stringWidth(initial)) / 2;
                int y = ((35 - fm.getHeight()) / 2) + fm.getAscent();

                g2.drawString(initial, x, y);
                g2.dispose();
            }
        };

        circle.setPreferredSize(new Dimension(35, 35));
        circle.setOpaque(false); // عشان الخلفية اللي ورا الدائرة متبانش مربعة
        return circle;
    }
}