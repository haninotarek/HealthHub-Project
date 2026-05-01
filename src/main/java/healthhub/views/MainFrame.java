package healthhub.views;

import healthhub.utils.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class MainFrame extends JFrame {

    public MainFrame() {
        initFrame();
        initComponents();
    }

    private void initFrame() {
        setTitle("HealthHub Clinic");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);
    }

    // ============== LEFT PANEL (الأزرق) ==============
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.PRIMARY);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalGlue());

        // ===== صورة اللوجو =====
        JLabel iconLabel = new JLabel();
        URL logoURL = getClass().getResource("/images/logo.png");
        if (logoURL != null) {
            ImageIcon originalIcon = new ImageIcon(logoURL);
            Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledImage));
        }
        iconLabel.setPreferredSize(new Dimension(150, 150));
        iconLabel.setMaximumSize(new Dimension(150, 150));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // =======================

        JLabel nameLabel = new JLabel("HealthHub Clinic");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        nameLabel.setForeground(ColorPalette.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subNameLabel = new JLabel("EPNU · Clinic Management System");
        subNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subNameLabel.setForeground(new Color(200, 215, 235));
        subNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(25));
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subNameLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // ============== RIGHT PANEL (الأبيض) ==============
    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        panel.add(Box.createVerticalGlue());

        JLabel welcomeLabel = new JLabel("Welcome");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        welcomeLabel.setForeground(ColorPalette.TEXT_DARK);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sloganLabel = new JLabel("Your health, our priority");
        sloganLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sloganLabel.setForeground(ColorPalette.TEXT_MEDIUM);
        sloganLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(
                "<html><p style='width:280px;'>Manage your clinic with ease. "
                        + "Book appointments, track patients, and deliver better care.</p></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(ColorPalette.TEXT_MEDIUM);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton getStartedBtn = createStyledButton("Get Started  →");
        getStartedBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        getStartedBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoginFrame();
            }
        });

        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(sloganLabel);
        panel.add(Box.createVerticalStrut(25));
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(40));
        panel.add(getStartedBtn);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(ColorPalette.WHITE);
        button.setBackground(ColorPalette.PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ColorPalette.ACCENT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(ColorPalette.PRIMARY);
            }
        });

        return button;
    }

    private void openLoginFrame() {
        new LoginFrame().setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame().setVisible(true);
        });
    }
}