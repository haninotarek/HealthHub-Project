package healthhub.views;

import healthhub.utils.ColorPalette;
import healthhub.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardFrame extends JFrame {

    private static final int SIDEBAR_WIDTH = 220;
    private static final Font FONT_LOGO    = new Font("Segoe UI", Font.BOLD,  18);
    private static final Font FONT_SUB     = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_NAV     = new Font("Segoe UI", Font.BOLD,  17); // ← كبرنا الفونت
    private static final Font FONT_LOGOUT  = new Font("Segoe UI", Font.BOLD,  17); // ← logout فونت

    private final User currentUser;
    private JPanel contentArea;

    public DashboardFrame(User user) {
        this.currentUser = user;
        setupFrame();
        buildUI();
        showPanel(new DashboardPanel());
    }

    private void setupFrame() {
        setTitle("HealthHub Clinic — Dashboard");
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void buildUI() {
        add(buildSidebar(),     BorderLayout.WEST);
        add(buildContentArea(), BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(ColorPalette.PRIMARY);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));

        sidebar.add(buildSidebarTop(),   BorderLayout.NORTH);
        sidebar.add(buildNavButtons(),   BorderLayout.CENTER);
        sidebar.add(buildLogoutButton(), BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel buildSidebarTop() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(ColorPalette.PRIMARY);
        top.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        JLabel lblName = new JLabel("HEALTH HUB");
        lblName.setFont(FONT_LOGO);
        lblName.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Clinic Management System");
        lblSub.setFont(FONT_SUB);
        lblSub.setForeground(new Color(255, 255, 255, 150));

        top.add(lblName);
        top.add(Box.createVerticalStrut(5));
        top.add(lblSub);
        top.add(Box.createVerticalStrut(20));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 50));
        top.add(sep);

        return top;
    }

    private JPanel buildNavButtons() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(ColorPalette.PRIMARY);
        nav.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        nav.add(buildNavButton("Dashboard",    "dashboard.png",   () -> showPanel(new DashboardPanel())));
        nav.add(buildNavButton("Patients",     "petient.png",     () -> showPanel(new PatientUI())));
        nav.add(buildNavButton("Doctors",      "doctor.png",      () -> showPanel(new DoctorPanel())));
        nav.add(buildNavButton("Appointments", "appointment.png", () -> showPanel(new AppointmentsPanel())));

        return nav;
    }

    private JButton buildNavButton(String text, String iconName, Runnable action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // لو hover ارسم خلفية
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        // Icon
        try {
            if (iconName != null && !iconName.isEmpty()) {
                java.net.URL imgURL = getClass().getResource("/images/" + iconName);
                if (imgURL != null) {
                    ImageIcon originalIcon = new ImageIcon(imgURL);
                    Image img = originalIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
                    btn.setIcon(new ImageIcon(img));
                }
            }
        } catch (Exception e) {}

        btn.setFont(FONT_NAV);
        btn.setForeground(new Color(255, 255, 255, 180));
        btn.setBackground(ColorPalette.PRIMARY);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);
        btn.setBorder(BorderFactory.createEmptyBorder(13, 25, 13, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(Color.WHITE);
                btn.repaint();
            }
            public void mouseExited(MouseEvent e) {
                btn.setForeground(new Color(255, 255, 255, 180));
                btn.repaint();
            }
        });

        btn.addActionListener(e -> action.run());
        return btn;
    }

    // ── Logout Button ──────────────────────────────
    private JPanel buildLogoutButton() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(ColorPalette.PRIMARY);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 15, 24, 15)); // padding من الجانبين

        JButton btnLogout = new JButton("Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // خلفية الزرار دايماً
                g2.setColor(new Color(30, 30, 60, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // لما hover يتغير اللون
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 80, 80, 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        // Icon لو موجود
        try {
            java.net.URL imgURL = getClass().getResource("/images/logout.png");
            if (imgURL != null) {
                Image img = new ImageIcon(imgURL).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                btnLogout.setIcon(new ImageIcon(img));
                btnLogout.setIconTextGap(10);
            }
        } catch (Exception e) {}

        btnLogout.setFont(FONT_LOGOUT);
        btnLogout.setForeground(new Color(255, 100, 100));
        btnLogout.setBackground(ColorPalette.PRIMARY);
        btnLogout.setOpaque(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        // الإطار الأحمر حوالين الزرار
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 100, 100, 120), 1, true), // ← إطار أحمر
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        // Hover effect
        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnLogout.setForeground(new Color(255, 60, 60));
                btnLogout.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 80, 80), 1, true), // ← إطار أحمر أقوى
                        BorderFactory.createEmptyBorder(10, 16, 10, 16)
                ));
                btnLogout.repaint();
            }
            public void mouseExited(MouseEvent e) {
                btnLogout.setForeground(new Color(255, 100, 100));
                btnLogout.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 100, 100, 120), 1, true),
                        BorderFactory.createEmptyBorder(10, 16, 10, 16)
                ));
                btnLogout.repaint();
            }
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        bottom.add(btnLogout, BorderLayout.CENTER);
        return bottom;
    }

    private JPanel buildContentArea() {
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(ColorPalette.BACKGROUND);
        return contentArea;
    }

    public void showPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.validate();
        contentArea.repaint();
        this.getContentPane().validate();
    }
}