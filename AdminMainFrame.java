package hotel.management.ui;

import hotel.management.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.Timer;

public class AdminMainFrame extends JFrame {

    private User currentUser;
    private JPanel contentArea;
    private CardLayout cardLayout;
    private JLabel lblClock, lblDate;
    private JButton activeNavBtn = null;

    // Panel references for refresh
    private DashboardPanel dashboardPanel;
    private RoomPanel roomPanel;
    private CustomerPanel customerPanel;
    private BookingPanel bookingPanel;
    private CheckInOutPanel checkInOutPanel;
    private BillingPanel billingPanel;
    private ReportsPanel reportsPanel;
    private SettingsPanel settingsPanel;
    private UserMgmtPanel userMgmtPanel;

    public AdminMainFrame(User user) {
        this.currentUser = user;
        setTitle("Hotel Management System - Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 650));
        buildFrame();
        setVisible(true);
        showPage("dashboard");
    }

    private void buildFrame() {
        setLayout(new BorderLayout());

        // TOP BAR
        add(buildTopBar(), BorderLayout.NORTH);

        // SIDEBAR
        add(buildSidebar(), BorderLayout.WEST);

        // CONTENT
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Theme.BG);

        dashboardPanel   = new DashboardPanel(currentUser, this);
        roomPanel        = new RoomPanel(currentUser);
        customerPanel    = new CustomerPanel(currentUser);
        bookingPanel     = new BookingPanel(currentUser);
        checkInOutPanel  = new CheckInOutPanel(currentUser);
        billingPanel     = new BillingPanel(currentUser);
        reportsPanel     = new ReportsPanel(currentUser);
        settingsPanel    = new SettingsPanel(currentUser);
        userMgmtPanel    = new UserMgmtPanel(currentUser);

        contentArea.add(dashboardPanel,  "dashboard");
        contentArea.add(roomPanel,       "rooms");
        contentArea.add(customerPanel,   "customers");
        contentArea.add(bookingPanel,    "bookings");
        contentArea.add(checkInOutPanel, "checkinout");
        contentArea.add(billingPanel,    "billing");
        contentArea.add(reportsPanel,    "reports");
        contentArea.add(settingsPanel,   "settings");
        contentArea.add(userMgmtPanel,   "users");

        add(contentArea, BorderLayout.CENTER);
    }

    // ─────────────────── TOP BAR ───────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.DARK_BLUE);
        bar.setPreferredSize(new Dimension(0, 65));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        // Left: Logo + Title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);

        JPanel logoBox = new JPanel(new GridBagLayout());
        logoBox.setBackground(Theme.MED_BLUE);
        logoBox.setPreferredSize(new Dimension(220, 65));
        JLabel logoLbl = new JLabel("<html><center>🏨</center></html>", JLabel.CENTER);
        logoLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        logoBox.add(logoLbl);

        JPanel titleBox = new JPanel(new BorderLayout());
        titleBox.setOpaque(false);
        titleBox.setBorder(BorderFactory.createEmptyBorder(10,14,10,0));
        JLabel t1 = new JLabel("HOTEL");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t1.setForeground(Theme.WHITE);
        JLabel t2 = new JLabel("MANAGEMENT SYSTEM");
        t2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        t2.setForeground(new Color(174,214,241));
        titleBox.add(t1, BorderLayout.NORTH);
        titleBox.add(t2, BorderLayout.CENTER);

        left.add(logoBox);
        left.add(titleBox);

        // Right: Clock + User + Logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);

        // Clock
        JPanel clockPanel = new JPanel(new GridLayout(2,1));
        clockPanel.setOpaque(false);
        lblDate = new JLabel("", JLabel.RIGHT);
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDate.setForeground(new Color(174,214,241));
        lblClock = new JLabel("", JLabel.RIGHT);
        lblClock.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblClock.setForeground(Theme.WHITE);
        clockPanel.add(lblDate);
        clockPanel.add(lblClock);

        // Update clock immediately then every second
        SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat tf2 = new SimpleDateFormat("hh:mm:ss a");
        lblDate.setText("📅 " + df2.format(new Date()));
        lblClock.setText("🕐 " + tf2.format(new Date()));

        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat tf = new SimpleDateFormat("hh:mm:ss a");
            lblDate.setText("📅 " + df.format(new Date()));
            lblClock.setText("🕐 " + tf.format(new Date()));
        });
        timer.start();

        // User info
        JPanel userInfo = new JPanel(new GridLayout(2,1));
        userInfo.setOpaque(false);
        JLabel uName = new JLabel("Welcome, " + currentUser.getFullName(), JLabel.RIGHT);
        uName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        uName.setForeground(Theme.WHITE);
        JLabel uRole = new JLabel("Administrator", JLabel.RIGHT);
        uRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        uRole.setForeground(Theme.GOLD);
        userInfo.add(uName); userInfo.add(uRole);

        // Avatar
        JLabel avatar = new JLabel("👤", JLabel.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        avatar.setForeground(Theme.WHITE);

        // Logout
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(Theme.RED);
        btnLogout.setForeground(Theme.WHITE);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setPreferredSize(new Dimension(80,32));
        btnLogout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,"Logout karna chahte hain?","Logout",JOptionPane.YES_NO_OPTION);
            if (c==JOptionPane.YES_OPTION) { dispose(); new LoginFrame(); }
        });

        right.add(clockPanel);
        right.add(new JSeparator(JSeparator.VERTICAL));
        right.add(avatar);
        right.add(userInfo);
        right.add(new JSeparator(JSeparator.VERTICAL));
        right.add(btnLogout);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ─────────────────── SIDEBAR ───────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.NAV_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(navBtn("🏠", "Dashboard",   "dashboard"));
        sidebar.add(navBtn("🛏", "Room Management","rooms"));
        sidebar.add(navBtn("👥", "Customer Management","customers"));
        sidebar.add(navBtn("📋", "Booking System","bookings"));
        sidebar.add(navBtn("🔄", "Check In / Check Out","checkinout"));
        sidebar.add(navBtn("💰", "Billing","billing"));
        sidebar.add(navBtn("📊", "Reports","reports"));
        sidebar.add(navBtn("⚙", "Settings","settings"));
        sidebar.add(navBtn("👤", "User Management","users"));
        sidebar.add(Box.createVerticalGlue());

        JLabel ver = new JLabel("v1.0 Hotel Management", JLabel.CENTER);
        ver.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        ver.setForeground(new Color(100,120,150));
        ver.setAlignmentX(Component.CENTER_ALIGNMENT);
        ver.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        sidebar.add(ver);
        return sidebar;
    }

    private JButton navBtn(String icon, String label, String page) {
        JButton btn = new JButton(icon + "  " + label) {
            boolean selected = false;
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                if (selected || getModel().isRollover()) {
                    g2.setColor(selected ? Theme.NAV_SEL : Theme.NAV_HOVER);
                    g2.fillRect(0,0,getWidth(),getHeight());
                    if (selected) {
                        g2.setColor(Theme.GOLD);
                        g2.fillRect(0,0,4,getHeight());
                    }
                }
                super.paintComponent(g);
            }
            public void setSelected(boolean s) { selected=s; repaint(); }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(180,200,230));
        btn.setBackground(Theme.NAV_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(220, 46));
        btn.setPreferredSize(new Dimension(220, 46));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 10));

        btn.addActionListener(e -> {
            if (activeNavBtn != null) ((JButton)activeNavBtn).setSelected(false);
            btn.setSelected(true);
            activeNavBtn = btn;
            showPage(page);
        });
        return btn;
    }

    void showPage(String page) {
        switch(page) {
            case "dashboard":  dashboardPanel.refresh(); break;
            case "rooms":      roomPanel.refresh(); break;
            case "customers":  customerPanel.refresh(); break;
            case "bookings":   bookingPanel.refresh(); break;
            case "checkinout": checkInOutPanel.refresh(); break;
            case "billing":    billingPanel.refresh(); break;
            case "reports":    reportsPanel.refresh(); break;
            case "users":      userMgmtPanel.refresh(); break;
        }
        cardLayout.show(contentArea, page);
    }
}