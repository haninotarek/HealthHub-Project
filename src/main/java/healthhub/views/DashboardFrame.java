package healthhub.views;

import healthhub.utils.ColorPalette;
import healthhub.models.User;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private static final int SIDEBAR_WIDTH = 220;
    private static final Font FONT_LOGO    = new Font("Segoe UI", Font.BOLD,  18);
    private static final Font FONT_SUB     = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_NAV     = new Font("Segoe UI", Font.BOLD,  13);

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
        setSize(1000, 700);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());
    }

    private void buildUI() {
        add(buildSidebar(),     BorderLayout.WEST);
        add(buildContentArea(), BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(ColorPalette.PRIMARY);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));

        sidebar.add(buildSidebarTop(),    BorderLayout.NORTH);
        sidebar.add(buildNavButtons(),    BorderLayout.CENTER);
        sidebar.add(buildLogoutButton(),  BorderLayout.SOUTH);

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
                if (isOpaque()) {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
            }
        };

        // --- التعديل الذكي لمنع رسائل الخطأ في الكونسول ---
        try {
            if (iconName != null && !iconName.isEmpty()) {
                java.net.URL imgURL = getClass().getResource("/images/" + iconName);
                if (imgURL != null) {
                    ImageIcon originalIcon = new ImageIcon(imgURL);
                    Image img = originalIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    btn.setIcon(new ImageIcon(img));
                } else {
                    System.err.println("❌ Image not found: /images/" + iconName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ------------------------------------------------

        btn.setFont(FONT_NAV);
        btn.setForeground(new Color(255, 255, 255, 180));
        btn.setBackground(ColorPalette.PRIMARY);
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(255, 255, 255, 30));
                btn.setForeground(Color.WHITE);
                btn.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ColorPalette.PRIMARY);
                btn.setForeground(new Color(255, 255, 255, 180));
                btn.repaint();
            }
        });

        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JPanel buildLogoutButton() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(ColorPalette.PRIMARY);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // هنا بعتنا null عشان مفيش أيقونة حالياً والكونسول ميزعلش
        JButton btnLogout = buildNavButton("Logout", null, () -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        btnLogout.setForeground(new Color(255, 100, 100));

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
        contentArea.revalidate();
        contentArea.repaint();
    }
}