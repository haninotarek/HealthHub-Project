package healthhub.views;

import healthhub.utils.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        // الـ Panel الرئيسي مقسوم نصين
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // الجزء الشمال - اللوجو والاسم
        JPanel leftPanel = createLeftPanel();

        // الجزء اليمين - الترحيب وزرار البداية
        JPanel rightPanel = createRightPanel();

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.PRIMARY);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalGlue());

        // أيقونة على شكل دائرة فيها حرف H
        JLabel iconLabel = new JLabel("+", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ColorPalette.WHITE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(ColorPalette.PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 80));
                FontMetrics fm = g2.getFontMetrics();
                String text = "+";
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(140, 140));
        iconLabel.setMaximumSize(new Dimension(140, 140));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // اسم العيادة
        JLabel nameLabel = new JLabel("HealthHub");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        nameLabel.setForeground(ColorPalette.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subNameLabel = new JLabel("C L I N I C");
        subNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subNameLabel.setForeground(ColorPalette.WHITE);
        subNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(25));
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subNameLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

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
        sloganLabel.setForeground(ColorPalette.TEXT_LIGHT);
        sloganLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(
                "<html><p style='width:280px;'>Manage your clinic with ease. "
                        + "Book appointments, track patients, and deliver better care.</p></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(ColorPalette.TEXT_LIGHT);
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

        // hover effect
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
        // لحد ما تعملي LoginFrame، سيبيها رسالة مؤقتة
        JOptionPane.showMessageDialog(this,
                "Login page coming next!",
                "HealthHub",
                JOptionPane.INFORMATION_MESSAGE);

        // لما تعملي LoginFrame شيلي اللي فوق واكتبي:
        // new LoginFrame().setVisible(true);
        // this.dispose();
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