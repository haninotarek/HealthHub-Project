package healthhub.views;
// كل دي بنعمل import  لكل ال مكتبات ال هنستخدمها
import healthhub.utils.ColorPalette;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
// عملنا class وخليناه بيرث من  JFrame
public class MainFrame extends JFrame {
    // constructor
    public MainFrame() {
        initFrame(); //  بتظبط إعدادات window نفسها
        initComponents(); //  بتضيف الـ Components (الأزرار، اللابلز...)
    }

    private void initFrame() {
        setTitle("HealthHub Clinic");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       // frame.addWindowListener(new WindowAdapter()...) دي ك AWT
        setLocationRelativeTo(null);                          // بتخلي النافذة تظهر في نص الشاشة وال null  للشاشه كلها مش لحاجه معينه .
        setResizable(true);                                   // نكبر ونصغر ال  window
        // التعديل: عشان يفتح مالي الشاشة
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));   // بنعمل main panel with GridLayout 1 row 2 colums
        JPanel leftPanel = createLeftPanel(); // دا ال هنحط فيه اسم العياده واللوجو
        JPanel rightPanel = createRightPanel();  // دا ال welcome + button
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.PRIMARY);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  // بنحط العناصر كلها فوق بعض
        panel.add(Box.createVerticalGlue());                      // مساحة فاضية مرنة بتدفع العناصر للنص. بتاخد كل المساحة الفاضية المتاحة.

        JLabel iconLabel = new JLabel();    // بنعمل label  عشان نحط فيه الصوره
        URL logoURL = getClass().getResource("/images/logo.png");
        if (logoURL != null) {    // هنا بنعمل condetion  لو الصوره موجوه
            ImageIcon originalIcon = new ImageIcon(logoURL); //  هنعمل ImageIcon
            Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);   //  هنا بنظبط المقاس والكوالتي
            iconLabel.setIcon(new ImageIcon(scaledImage));
        }
        iconLabel.setPreferredSize(new Dimension(150, 150));
        iconLabel.setMaximumSize(new Dimension(150, 150));
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

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
        panel.add(Box.createVerticalGlue());

        JLabel welcomeLabel = new JLabel("Welcome");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD,55));
        welcomeLabel.setForeground(ColorPalette.TEXT_DARK);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sloganLabel = new JLabel("Your health, our priority");
        sloganLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        sloganLabel.setForeground(ColorPalette.TEXT_MEDIUM);
        sloganLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><p style='width:280px;'>Manage your clinic with ease...</p></html>");
        //  الـ JLabel بيدعم HTML، فبنستخدمه عشان نخلي النص يلف على أكتر من سطر بعرض 280 بكسل. لو كتبتي نص عادي مش هيلف.


        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        descLabel.setForeground(ColorPalette.TEXT_MEDIUM);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // هنا بنعمل button   ب استخدام ميثود متعرفه قبل كدا
        JButton getStartedBtn = createStyledButton("Get Started  →");
        getStartedBtn.addActionListener(e -> openLoginFrame());

        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(sloganLabel);
        panel.add(Box.createVerticalStrut(25));
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(40));
        panel.add(getStartedBtn);
        panel.add(Box.createVerticalGlue());
        return panel;
    }// بنرص العناصر عمودياً مع مسافات بينها

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(ColorPalette.WHITE);
        button.setBackground(ColorPalette.PRIMARY);
        button.setFocusPainted(false);           // اخفي الإطار اللي بيظهر لما تختاري الزرار بـ Tab
        button.setBorderPainted(false);          //اخفي الإطار العادي
        button.setContentAreaFilled(false);     // ما تملأش الخلفية الافتراضية (إحنا بنرسم بنفسنا)


        button.setOpaque(false);                //  الزرار شفاف (عشان الحواف الدائرية تبان)


        //حجم الزرار 200×50
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        //Cursor.HAND_CURSOR → لما الماوس فوق الزرار يبقى شكل اليد (زي اللينكات في الويب).
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }


    //  الانتقال لصفحة Login
    private void openLoginFrame() {
        LoginFrame login = new LoginFrame();
        // التعديل: عشان ينقل حالة التكبير للصفحة اللي بعدها
        login.setExtendedState(this.getExtendedState());
        login.setVisible(true);
        this.dispose();       // مش خيقفل البرنامج بس هيقفل ال ويندو دي
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MainFrame().setVisible(true));
    }
}