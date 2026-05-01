package hotel.management.ui;

import hotel.management.dao.*;
import hotel.management.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

public class UserMainFrame extends JFrame {
    private User currentUser;
    private JPanel contentArea;
    private CardLayout cardLayout;
    private RoomDAO roomDAO = new RoomDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private BookingDAO bookingDAO = new BookingDAO();

    public UserMainFrame(User user) {
        this.currentUser = user;
        setTitle("Hotel Management System - User View");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 600));
        buildFrame();
        setVisible(true);
    }

    private void buildFrame() {
        setLayout(new BorderLayout());
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Theme.BG);
        contentArea.add(buildDashboard(), "dashboard");
        contentArea.add(buildViewPanel("🛏 Rooms",   new String[]{"ID","Room No","Type","Price","Status","Floor","Cap"}, "rooms"),   "rooms");
        contentArea.add(buildViewPanel("👥 Customers",new String[]{"ID","Name","CNIC","Phone","Email","City"},           "customers"),"customers");
        contentArea.add(buildViewPanel("📋 Bookings", new String[]{"Code","Guest","Room","Check-In","Check-Out","Amount","Status"},"bookings"),"bookings");
        add(contentArea, BorderLayout.CENTER);
        cardLayout.show(contentArea, "dashboard");
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(20, 90, 40));
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JLabel title = new JLabel("🏨  HOTEL MANAGEMENT SYSTEM  —  USER VIEW (Read Only)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Theme.GOLD);
        bar.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        JLabel user = new JLabel("👤 " + currentUser.getFullName() + "  |  USER");
        user.setFont(new Font("Segoe UI", Font.BOLD, 13));
        user.setForeground(Theme.WHITE);
        JButton logout = new JButton("Logout");
        logout.setBackground(Theme.RED); logout.setForeground(Theme.WHITE);
        logout.setBorderPainted(false); logout.setFocusPainted(false);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> { dispose(); new LoginFrame(); });
        right.add(user); right.add(logout);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSidebar() {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(Theme.NAV_BG); s.setPreferredSize(new Dimension(200, 0));
        s.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel note = new JLabel("  👁 View Only Mode", JLabel.LEFT);
        note.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        note.setForeground(new Color(255, 200, 100));
        note.setMaximumSize(new Dimension(200, 30));
        s.add(note); s.add(Box.createVerticalStrut(8));

        s.add(sideBtn("🏠  Dashboard",  "dashboard"));
        s.add(sideBtn("🛏  View Rooms", "rooms"));
        s.add(sideBtn("👥  View Customers","customers"));
        s.add(sideBtn("📋  View Bookings","bookings"));
        s.add(Box.createVerticalGlue());
        return s;
    }

    private JButton sideBtn(String text, String page) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setForeground(new Color(180, 200, 230)); b.setBackground(Theme.NAV_BG);
        b.setBorderPainted(false); b.setFocusPainted(false); b.setContentAreaFilled(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(200, 44)); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(Theme.NAV_HOVER); b.setContentAreaFilled(true); }
            public void mouseExited(MouseEvent e) { b.setContentAreaFilled(false); }
        });
        b.addActionListener(e -> cardLayout.show(contentArea, page));
        return b;
    }

    private JPanel buildDashboard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("📊  Welcome, " + currentUser.getFullName() + "!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22)); title.setForeground(Theme.DARK_BLUE);
        p.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setBackground(Theme.BG); cards.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        int[] rs = roomDAO.getStats();
        cards.add(userCard("Available Rooms", String.valueOf(rs[0]), "🛏", Theme.GREEN));
        cards.add(userCard("Total Bookings",  String.valueOf(bookingDAO.getTotal()), "📋", Theme.ACCENT));
        cards.add(userCard("Total Customers", String.valueOf(customerDAO.getTotal()), "👥", Theme.ORANGE));
        p.add(cards, BorderLayout.CENTER);

        JLabel note = new JLabel("⚠  You are logged in as USER. Contact admin for any changes.", JLabel.CENTER);
        note.setFont(new Font("Segoe UI", Font.BOLD, 13)); note.setForeground(Theme.RED);
        note.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        p.add(note, BorderLayout.SOUTH);
        return p;
    }

    private JPanel userCard(String label, String val, String icon, Color color) {
        JPanel c = new JPanel(new BorderLayout(10, 6));
        c.setBackground(Theme.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(color.getRed(),color.getGreen(),color.getBlue(),100),2),
            BorderFactory.createEmptyBorder(20,20,20,20)));
        JLabel ic = new JLabel(icon, JLabel.CENTER); ic.setFont(new Font("Segoe UI Emoji",Font.PLAIN,36));
        JLabel v = new JLabel(val, JLabel.CENTER); v.setFont(new Font("Segoe UI",Font.BOLD,40)); v.setForeground(color);
        JLabel lb = new JLabel(label, JLabel.CENTER); lb.setFont(new Font("Segoe UI",Font.PLAIN,13)); lb.setForeground(new Color(100,110,125));
        JPanel right = new JPanel(new BorderLayout()); right.setBackground(Theme.WHITE);
        right.add(v, BorderLayout.CENTER); right.add(lb, BorderLayout.SOUTH);
        c.add(ic, BorderLayout.WEST); c.add(right, BorderLayout.CENTER); return c;
    }

    private JPanel buildViewPanel(String title, String[] cols, String type) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Theme.BG); p.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        JLabel t = new JLabel(title); t.setFont(new Font("Segoe UI",Font.BOLD,20)); t.setForeground(Theme.DARK_BLUE);
        JLabel ro = new JLabel("  👁 READ ONLY — Admin access required to make changes");
        ro.setFont(new Font("Segoe UI",Font.ITALIC,12)); ro.setForeground(Theme.RED);
        header.add(t, BorderLayout.WEST); header.add(ro, BorderLayout.EAST);
        p.add(header, BorderLayout.NORTH);

        DefaultTableModel m = new DefaultTableModel(cols, 0){ public boolean isCellEditable(int r,int c){return false;} };
        JTable table = new JTable(m); Theme.styleTable(table);
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(null);
        p.add(scroll, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            if ("rooms".equals(type))
                for (hotel.management.model.Room r : roomDAO.getAll())
                    m.addRow(new Object[]{r.getId(),r.getRoomNumber(),r.getRoomType(),r.getPricePerNight(),r.getStatus(),r.getFloor(),r.getCapacity()});
            else if ("customers".equals(type))
                for (hotel.management.model.Customer c : customerDAO.getAll())
                    m.addRow(new Object[]{c.getId(),c.getFullName(),c.getCnic(),c.getPhone(),c.getEmail(),c.getCity()});
            else if ("bookings".equals(type))
                for (hotel.management.model.Booking b : bookingDAO.getAll())
                    m.addRow(new Object[]{b.getBookingCode(),b.getCustomerName(),b.getRoomNumber(),df.format(b.getCheckIn()),df.format(b.getCheckOut()),String.format("%.0f",b.getTotalAmount()),b.getStatus()});
        });
        return p;
    }
}
