package hotel.management.ui;

import hotel.management.dao.*;
import hotel.management.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private User currentUser;
    private AdminMainFrame mainFrame;
    private RoomDAO roomDAO = new RoomDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private BookingDAO bookingDAO = new BookingDAO();

    // Stat labels
    private JLabel valTotalRooms, valBookedRooms, valTotalCustomers,
                   valTodayBookings, valCheckedIn, valCheckedOutToday;

    // Tables
    private DefaultTableModel checkInModel, checkOutModel, recentModel;

    // Bottom stats
    private JLabel valAvailRooms, valTodayRev, valMonthRev, valTotalCust2;
    private JLabel valOccupancy, valTotalFloors, valMaintenance;

    public DashboardPanel(User user, AdminMainFrame frame) {
        this.currentUser = user;
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        buildUI();
    }

    private void buildUI() {
        // Scrollable
        JPanel inner = new JPanel(new BorderLayout(0, 12));
        inner.setBackground(Theme.BG);
        inner.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // PAGE TITLE
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(Theme.BG);
        JLabel title = new JLabel("  Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Theme.DARK_BLUE);
        JLabel sub = new JLabel("  Overview of hotel operations");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(120,130,145));
        JPanel titleText = new JPanel(new GridLayout(2,1));
        titleText.setBackground(Theme.BG);
        titleText.add(title); titleText.add(sub);
        titleRow.add(titleText, BorderLayout.WEST);
        inner.add(titleRow, BorderLayout.NORTH);

        // MAIN SCROLL
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.BG);

        // ── STAT CARDS ROW ──
        JPanel statsRow = new JPanel(new GridLayout(1, 6, 10, 0));
        statsRow.setBackground(Theme.BG);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        valTotalRooms      = new JLabel("0");
        valBookedRooms     = new JLabel("0");
        valTotalCustomers  = new JLabel("0");
        valTodayBookings   = new JLabel("0");
        valCheckedIn       = new JLabel("0");
        valCheckedOutToday = new JLabel("0");

        statsRow.add(buildStatCard("🛏", valTotalRooms,      "Total Rooms",       Theme.ACCENT,  "rooms"));
        statsRow.add(buildStatCard("✅", valBookedRooms,     "Booked Rooms",      Theme.GREEN,   "rooms"));
        statsRow.add(buildStatCard("👥", valTotalCustomers,  "Total Customers",   Theme.ORANGE,  "customers"));
        statsRow.add(buildStatCard("📅", valTodayBookings,   "Today Bookings",    Theme.PURPLE,  "bookings"));
        statsRow.add(buildStatCard("🔑", valCheckedIn,       "Checked In",        Theme.TEAL,    "checkinout"));
        statsRow.add(buildStatCard("🚪", valCheckedOutToday, "Checked Out Today", Theme.RED,     "checkinout"));

        body.add(statsRow);
        body.add(Box.createVerticalStrut(14));

        // ── QUICK ACCESS ──
        JPanel qaLabel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        qaLabel.setBackground(Theme.BG);
        JLabel qa = new JLabel("Quick Access");
        qa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        qa.setForeground(Theme.DARK_BLUE);
        qaLabel.add(qa);
        body.add(qaLabel);
        body.add(Box.createVerticalStrut(8));

        JPanel qaRow = new JPanel(new GridLayout(1, 7, 10, 0));
        qaRow.setBackground(Theme.BG);
        qaRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        qaRow.add(quickCard("🛏", "Room\nManagement",     Theme.ACCENT,  "rooms"));
        qaRow.add(quickCard("👥", "Customer\nManagement", Theme.ORANGE,  "customers"));
        qaRow.add(quickCard("📋", "Booking\nSystem",      Theme.PURPLE,  "bookings"));
        qaRow.add(quickCard("🔑", "Check In",             Theme.TEAL,    "checkinout"));
        qaRow.add(quickCard("🚪", "Check Out",            Theme.RED,     "checkinout"));
        qaRow.add(quickCard("💰", "Billing",              Theme.GREEN,   "billing"));
        qaRow.add(quickCard("➕", "Add Booking",          Theme.GOLD,    "bookings"));
        body.add(qaRow);
        body.add(Box.createVerticalStrut(14));

        // ── THREE TABLES ROW ──
        JPanel tablesRow = new JPanel(new GridLayout(1, 3, 12, 0));
        tablesRow.setBackground(Theme.BG);
        tablesRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        // Today's Check In table
        checkInModel = new DefaultTableModel(new String[]{"Booking ID","Guest Name","Room No","Check In Time"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        tablesRow.add(buildTableCard("📥 Today's Check In", checkInModel, Theme.TEAL));

        // Today's Check Out table
        checkOutModel = new DefaultTableModel(new String[]{"Booking ID","Guest Name","Room No","Check Out Time"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        tablesRow.add(buildTableCard("📤 Today's Check Out", checkOutModel, Theme.RED));

        // Recent Bookings table
        recentModel = new DefaultTableModel(new String[]{"Booking ID","Guest Name","Room No","Check In","Check Out","Status"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        tablesRow.add(buildTableCard("📋 Recent Bookings", recentModel, Theme.PURPLE));

        body.add(tablesRow);
        body.add(Box.createVerticalStrut(14));

        // ── BOTTOM STATS BAR ──
        JPanel bottomBar = new JPanel(new GridLayout(1, 7, 10, 0));
        bottomBar.setBackground(Theme.BG);
        bottomBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));

        valOccupancy   = new JLabel("0%");
        valAvailRooms  = new JLabel("0");
        valTodayRev    = new JLabel("Rs. 0");
        valMonthRev    = new JLabel("Rs. 0");
        valTotalCust2  = new JLabel("0");
        valTotalFloors = new JLabel("0");
        valMaintenance = new JLabel("0");

        bottomBar.add(bottomCard("Occupancy",          valOccupancy,   "📊", Theme.PURPLE));
        bottomBar.add(bottomCard("Available Rooms",    valAvailRooms,  "🛏", Theme.GREEN));
        bottomBar.add(bottomCard("Total Floors",       valTotalFloors, "🏢", Theme.ACCENT));
        bottomBar.add(bottomCard("Maintenance",        valMaintenance, "🔧", Theme.RED));
        bottomBar.add(bottomCard("Today Revenue",      valTodayRev,    "💵", Theme.AMBER));
        bottomBar.add(bottomCard("Month Revenue",      valMonthRev,    "💰", Theme.GOLD));
        bottomBar.add(bottomCard("Total Customers",    valTotalCust2,  "👥", Theme.ORANGE));
        body.add(bottomBar);

        inner.add(body, BorderLayout.CENTER);
        JScrollPane scroll = new JScrollPane(inner);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll);
    }

    private JPanel buildStatCard(String icon, JLabel valLbl, String label, Color color, String page) {
        JPanel card = new JPanel(new BorderLayout(8,4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.WHITE); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(color); g2.fillRoundRect(0,getHeight()-6,getWidth(),6,4,4);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12,14,14,14));

        // Icon circle
        JPanel iconCircle = new JPanel(new GridBagLayout());
        iconCircle.setPreferredSize(new Dimension(52,52));
        iconCircle.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
        iconCircle.setBorder(BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60), 2));
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconCircle.add(iconLbl);

        JPanel right = new JPanel(new BorderLayout(0,2));
        right.setOpaque(false);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valLbl.setForeground(color);
        JLabel lblText = new JLabel(label);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblText.setForeground(new Color(110,120,135));
        JLabel viewDtl = new JLabel("View Details →");
        viewDtl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        viewDtl.setForeground(color);
        right.add(valLbl,  BorderLayout.NORTH);
        right.add(lblText, BorderLayout.CENTER);
        right.add(viewDtl, BorderLayout.SOUTH);

        card.add(iconCircle, BorderLayout.WEST);
        card.add(right, BorderLayout.CENTER);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){ mainFrame.showPage(page); }
        });
        return card;
    }

    private JPanel quickCard(String icon, String label, Color color, String page) {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel_() != null && getModel_().isRollover()) g2.setColor(color.darker());
                else g2.setColor(color);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
            }
            private ButtonModel getModel_() { return null; }
        };
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel inner2 = new JPanel(new BorderLayout(0,4));
        inner2.setOpaque(false);
        JLabel iconLbl = new JLabel(icon, JLabel.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLbl.setForeground(Theme.WHITE);
        String[] parts = label.split("\n");
        JPanel textPanel = new JPanel(new GridLayout(parts.length,1));
        textPanel.setOpaque(false);
        for (String p : parts) {
            JLabel l = new JLabel(p, JLabel.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 11));
            l.setForeground(Theme.WHITE);
            textPanel.add(l);
        }
        inner2.add(iconLbl, BorderLayout.NORTH);
        inner2.add(textPanel, BorderLayout.CENTER);
        card.add(inner2);

        card.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){ mainFrame.showPage(page); }
            public void mouseEntered(java.awt.event.MouseEvent e){ card.setBackground(color.darker()); card.repaint(); }
            public void mouseExited(java.awt.event.MouseEvent e){ card.setBackground(color); card.repaint(); }
        });
        card.setBackground(color);
        return card;
    }

    private JPanel buildTableCard(String title, DefaultTableModel model, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220,228,236)));

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Theme.WHITE);
        titleBar.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(color);
        titleBar.add(lbl, BorderLayout.WEST);

        JTable table = new JTable(model);
        Theme.styleTable(table);
        table.getTableHeader().setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 200));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);

        // View All button
        JButton viewAll = new JButton("View All →");
        viewAll.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        viewAll.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
        viewAll.setForeground(color);
        viewAll.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        viewAll.setFocusPainted(false);
        viewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.add(titleBar, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(viewAll, BorderLayout.SOUTH);
        return card;
    }

    private JPanel bottomCard(String label, JLabel valLbl, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout(8,4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setColor(Theme.WHITE); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),40));
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,228,236)),
            BorderFactory.createEmptyBorder(10,14,10,14)));

        JLabel iconLbl = new JLabel(icon, JLabel.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLbl.setPreferredSize(new Dimension(40,40));

        JPanel txt = new JPanel(new BorderLayout(0,2));
        txt.setOpaque(false);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valLbl.setForeground(color);
        JLabel lbl2 = new JLabel(label);
        lbl2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl2.setForeground(new Color(100,110,125));
        txt.add(valLbl, BorderLayout.NORTH);
        txt.add(lbl2, BorderLayout.CENTER);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(txt, BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        int[] roomStats = roomDAO.getStats();
        int totalRooms = roomDAO.getTotal();
        int totalCust = customerDAO.getTotal();
        int todayBook = bookingDAO.getTodayBookings();
        int checkedIn = bookingDAO.getCheckedIn();
        int checkedOut = bookingDAO.getCheckedOutToday();
        double todayRev = bookingDAO.getTodayRevenue();
        double monthRev = bookingDAO.getMonthRevenue();

        valTotalRooms.setText(String.valueOf(totalRooms));
        valBookedRooms.setText(String.valueOf(roomStats[1]));
        valTotalCustomers.setText(String.valueOf(totalCust));
        valTodayBookings.setText(String.valueOf(todayBook));
        valCheckedIn.setText(String.valueOf(checkedIn));
        valCheckedOutToday.setText(String.valueOf(checkedOut));

        valAvailRooms.setText(String.valueOf(roomStats[0]));
        valTodayRev.setText(String.format("Rs. %,.0f", todayRev));
        valMonthRev.setText(String.format("Rs. %,.0f", monthRev));
        valTotalCust2.setText(String.valueOf(totalCust));
        valTotalFloors.setText(String.valueOf(roomDAO.getTotalFloors()));
        valMaintenance.setText(String.valueOf(roomDAO.getMaintenance()));

        int occ = totalRooms > 0 ? (roomStats[1]*100/totalRooms) : 0;
        valOccupancy.setText(occ + "%");

        // Load check-in table
        checkInModel.setRowCount(0);
        SimpleDateFormat tf = new SimpleDateFormat("hh:mm a");
        for (Booking b : bookingDAO.getTodayCheckIns()) {
            checkInModel.addRow(new Object[]{
                b.getBookingCode(), b.getCustomerName(), b.getRoomNumber(),
                b.getCheckInTime() != null ? b.getCheckInTime() : "Pending"
            });
        }
        // Load check-out table
        checkOutModel.setRowCount(0);
        for (Booking b : bookingDAO.getTodayCheckOuts()) {
            checkOutModel.addRow(new Object[]{
                b.getBookingCode(), b.getCustomerName(), b.getRoomNumber(),
                b.getCheckOutTime() != null ? b.getCheckOutTime() : "Pending"
            });
        }
        // Recent bookings
        recentModel.setRowCount(0);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        int count = 0;
        for (Booking b : bookingDAO.getAll()) {
            if (count++ >= 6) break;
            recentModel.addRow(new Object[]{
                b.getBookingCode(), b.getCustomerName(), b.getRoomNumber(),
                df.format(b.getCheckIn()), df.format(b.getCheckOut()), b.getStatus()
            });
        }
    }
}
