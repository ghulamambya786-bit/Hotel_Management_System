package hotel.management.ui;

import hotel.management.dao.UserDAO;
import hotel.management.model.User;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblMsg;
    private UserDAO userDAO = new UserDAO();

    static final Color DARK_BLUE  = new Color(13, 27, 62);
    static final Color MED_BLUE   = new Color(26, 82, 118);
    static final Color ACCENT     = new Color(41, 128, 185);
    static final Color GOLD       = new Color(212, 175, 55);
    static final Color WHITE      = Color.WHITE;
    static final Color LIGHT_GRAY = new Color(236, 240, 241);

    public LoginFrame() {
        setTitle("Hotel Management System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0,0, DARK_BLUE, 0, getHeight(), MED_BLUE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // ---- LOGO AREA ----
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(480, 200));

        JPanel logoBox = new JPanel(new BorderLayout(0, 6));
        logoBox.setOpaque(false);

        JLabel lblIcon = new JLabel("★★★", JLabel.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblIcon.setForeground(GOLD);

        JLabel lblHotel = new JLabel("HOTEL", JLabel.CENTER);
        lblHotel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblHotel.setForeground(WHITE);

        JLabel lblMgmt = new JLabel("MANAGEMENT SYSTEM", JLabel.CENTER);
        lblMgmt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMgmt.setForeground(new Color(174, 214, 241));

        JPanel topLabels = new JPanel(new BorderLayout());
        topLabels.setOpaque(false);
        topLabels.add(lblIcon, BorderLayout.NORTH);
        topLabels.add(lblHotel, BorderLayout.CENTER);
        topLabels.add(lblMgmt, BorderLayout.SOUTH);
        logoBox.add(topLabels, BorderLayout.CENTER);
        logoPanel.add(logoBox);

        // ---- LOGIN CARD ----
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        card.setPreferredSize(new Dimension(380, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Title
        JLabel lblTitle = new JLabel("Sign In to Your Account", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(MED_BLUE);
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        card.add(lblTitle, gbc);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(200,200,200));
        gbc.gridy=1;
        card.add(sep, gbc);

        // Username label
        JLabel lblU = new JLabel("Username");
        lblU.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblU.setForeground(MED_BLUE);
        gbc.gridy=2; gbc.gridwidth=2;
        card.add(lblU, gbc);

        // Username field
        txtUsername = new JTextField();
        styleField(txtUsername);
        gbc.gridy=3;
        card.add(txtUsername, gbc);

        // Password label
        JLabel lblP = new JLabel("Password");
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblP.setForeground(MED_BLUE);
        gbc.gridy=4;
        card.add(lblP, gbc);

        // Password field
        txtPassword = new JPasswordField();
        styleField(txtPassword);
        gbc.gridy=5;
        card.add(txtPassword, gbc);

        // Message
        lblMsg = new JLabel(" ", JLabel.CENTER);
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMsg.setForeground(new Color(192, 57, 43));
        gbc.gridy=6;
        card.add(lblMsg, gbc);

        // Login button
        JButton btnLogin = new JButton("LOGIN") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(MED_BLUE.darker());
                else if (getModel().isRollover()) g2.setColor(ACCENT.brighter());
                else g2.setColor(ACCENT);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()-fm.stringWidth(getText()))/2;
                int y = (getHeight()+fm.getAscent()-fm.getDescent())/2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(300, 42));
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy=7;
        card.add(btnLogin, gbc);

        // Hint
        JLabel hint = new JLabel("Admin: admin/admin123  |  User: user/user123", JLabel.CENTER);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(130,130,130));
        gbc.gridy=8;
        card.add(hint, gbc);

        // Wrap card in centered panel
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.add(card);

        root.add(logoPanel, BorderLayout.NORTH);
        root.add(cardWrapper, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2024 Hotel Management System. All rights reserved.", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setForeground(new Color(150,170,200));
        footer.setBorder(BorderFactory.createEmptyBorder(8,0,10,0));
        root.add(footer, BorderLayout.SOUTH);

        add(root);

        // Events
        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { if (e.getKeyCode()==KeyEvent.VK_ENTER) doLogin(); }
        });
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(300, 38));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180,200,220), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        f.setBackground(new Color(245,250,255));
    }

    private void doLogin() {
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword()).trim();
        if (u.isEmpty()||p.isEmpty()) { lblMsg.setText("Username aur password required!"); return; }
        lblMsg.setForeground(ACCENT);
        lblMsg.setText("Connecting...");
        User user = userDAO.login(u, p);
        if (user != null) {
            lblMsg.setForeground(new Color(39,174,96));
            lblMsg.setText("Login successful!");
            dispose();
            if (user.isAdmin()) new AdminMainFrame(user);
            else new UserMainFrame(user);
        } else {
            lblMsg.setForeground(new Color(192,57,43));
            lblMsg.setText("Invalid username or password!");
            txtPassword.setText("");
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception ignored){}
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
