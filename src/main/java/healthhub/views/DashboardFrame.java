package healthhub.views;

import healthhub.utils.ColorPalette;
import healthhub.models.User;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    // ============================================================
    // Constants
    // ============================================================
    private static final int SIDEBAR_WIDTH = 200;
    private static final Font FONT_LOGO    = new Font("Segoe UI", Font.BOLD,  16);
    private static final Font FONT_SUB     = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_NAV     = new Font("Segoe UI", Font.BOLD,  13);

    // ============================================================
    // Fields
    // ============================================================
    private final User currentUser;       // اليوزر اللي دخل
    private JPanel contentArea;           // المنطقة اللي بتتغير

    // ============================================================
    // Constructor
    // ============================================================
    public DashboardFrame(User user) {
        this.currentUser = user;

        setupFrame();
        buildUI();
        showPanel(new DashboardPanel()); // الشاشة الأولى
    }

    // ============================================================
    // Frame Settings
    // ============================================================
    private void setupFrame() {
        setTitle("HealthHub Clinic — Dashboard");
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // في النص
        setLayout(new BorderLayout());
    }

    // ============================================================
    // Build UI — Sidebar + Content Area
    // ============================================================
    private void buildUI() {
        add(buildSidebar(),     BorderLayout.WEST);
        add(buildContentArea(), BorderLayout.CENTER);
    }

    // ============================================================
    // Sidebar
    // ============================================================
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(ColorPalette.PRIMARY);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));

        sidebar.add(buildSidebarTop(),    BorderLayout.NORTH);
        sidebar.add(buildNavButtons(),    BorderLayout.CENTER);
        sidebar.add(buildLogoutButton(),  BorderLayout.SOUTH);

        return sidebar;
    }

    // ============================================================
    // Sidebar — Logo
    // ============================================================
    private JPanel buildSidebarTop() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(ColorPalette.PRIMARY);
        top.setBorder(BorderFactory.createEmptyBorder(24, 20, 16, 20));

        JLabel lblName = new JLabel("HealthHub");
        lblName.setFont(FONT_LOGO);
        lblName.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("EPNU Clinic System");
        lblSub.setFont(FONT_SUB);
        lblSub.setForeground(new Color(255, 255, 255, 128));

        top.add(lblName);
        top.add(Box.createVerticalStrut(4));
        top.add(lblSub);
        top.add(Box.createVerticalStrut(16));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        top.add(sep);

        return top;
    }

    // ============================================================
    // Sidebar — Nav Buttons
    // ============================================================
    private JPanel buildNavButtons() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(ColorPalette.PRIMARY);
        nav.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        nav.add(buildNavButton("📊  Dashboard",    () -> showPanel(new DashboardPanel())));
        nav.add(buildNavButton("👥  Patients",     () -> showPanel(new PatientUI())));
        nav.add(buildNavButton("🩺  Doctors",      () -> showPanel(new DoctorPanel())));
        nav.add(buildNavButton("■  Appointments", () -> showPanel(new AppointmentsPanel())));
        return nav;
    }

    // ============================================================
    // Nav Button — زرار واحد في الـ Sidebar
    // ============================================================
    private JButton buildNavButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_NAV);
        btn.setForeground(new Color(255, 255, 255, 180));
        btn.setBackground(ColorPalette.PRIMARY);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(255, 255, 255, 20));
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ColorPalette.PRIMARY);
                btn.setForeground(new Color(255, 255, 255, 180));
            }
        });

        // Action
        btn.addActionListener(e -> action.run());

        return btn;
    }

    // ============================================================
    // Logout Button
    // ============================================================
    private JPanel buildLogoutButton() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(ColorPalette.PRIMARY);
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 0, 16, 0));

        JButton btnLogout = new JButton("🚪  Logout");
        btnLogout.setFont(FONT_NAV);
        btnLogout.setForeground(ColorPalette.DANGER);
        btnLogout.setBackground(ColorPalette.PRIMARY);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        bottom.add(btnLogout, BorderLayout.CENTER);
        return bottom;
    }

    // ============================================================
    // Content Area — المنطقة اللي بتتغير
    // ============================================================
    private JPanel buildContentArea() {
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(ColorPalette.BACKGROUND);
        return contentArea;
    }

    // showPanel — بتبدل المحتوى

    public void showPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }
}