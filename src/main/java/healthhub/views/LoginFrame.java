package healthhub.views;

import healthhub.utils.ColorPalette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginFrame() {
        initFrame();
        initComponents();
    }

    private void initFrame() {
        setTitle("HealthHub Clinic - Login");
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

    // ============== LEFT PANEL (نفس MainFrame بالظبط) ==============
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.PRIMARY);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalGlue());

        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillOval(0, 0, getWidth(), getHeight());

                g2.setColor(ColorPalette.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 70));
                FontMetrics fm = g2.getFontMetrics();
                String text = "+";
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, x, y);

                g2.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(120, 120));
        iconLabel.setMaximumSize(new Dimension(120, 120));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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

    // ============== RIGHT PANEL (الفورم) ==============
    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(0, 50, 0, 50));

        panel.add(Box.createVerticalGlue());

        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(ColorPalette.TEXT_DARK);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Sign in to your account to continue");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(ColorPalette.TEXT_MEDIUM);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usernameLabel.setForeground(ColorPalette.TEXT_DARK);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD5E0), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(ColorPalette.TEXT_DARK);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD5E0), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(ColorPalette.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login Button
        JButton loginBtn = createStyledButton("Login");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        getRootPane().setDefaultButton(loginBtn);

        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subLabel);
        panel.add(Box.createVerticalStrut(30));

        panel.add(usernameLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(15));

        panel.add(passwordLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(8));

        panel.add(errorLabel);
        panel.add(Box.createVerticalStrut(15));

        panel.add(loginBtn);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        // مؤقتاً لحد ما AuthService يجهز
        if (username.equals("admin") && password.equals("admin123")) {
            openDashboard();
        } else {
            showError("Invalid username or password");
            passwordField.setText("");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    private void openDashboard() {
        JOptionPane.showMessageDialog(this,
                "Login successful! Dashboard coming next!",
                "HealthHub",
                JOptionPane.INFORMATION_MESSAGE);

        // لما تعملي DashboardFrame:
        // new DashboardFrame().setVisible(true);
        // this.dispose();
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
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(200, 45));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
}