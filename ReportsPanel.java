package hotel.management.ui;

import hotel.management.dao.*;
import hotel.management.model.User;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportsPanel extends JPanel {
    private User currentUser;
    private BookingDAO bookingDAO = new BookingDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private DefaultTableModel model;

    public ReportsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout()); setBackground(Theme.BG); buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(14,18,14,18));
        JLabel title = new JLabel("📊  Reports");
        title.setFont(new Font("Segoe UI",Font.BOLD,20)); title.setForeground(Theme.DARK_BLUE);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0,14));
        body.setBackground(Theme.BG);
        body.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));

        // Summary cards
        JPanel cards = new JPanel(new GridLayout(1,4,12,0));
        cards.setBackground(Theme.BG);

        JLabel lTR = new JLabel("0"); lTR.setFont(new Font("Segoe UI",Font.BOLD,22)); lTR.setForeground(Theme.ACCENT);
        JLabel lTB = new JLabel("0"); lTB.setFont(new Font("Segoe UI",Font.BOLD,22)); lTB.setForeground(Theme.GREEN);
        JLabel lTC = new JLabel("0"); lTC.setFont(new Font("Segoe UI",Font.BOLD,22)); lTC.setForeground(Theme.ORANGE);
        JLabel lRev = new JLabel("0"); lRev.setFont(new Font("Segoe UI",Font.BOLD,22)); lRev.setForeground(Theme.GOLD);

        cards.add(rptCard("Total Rooms","🛏",lTR,Theme.ACCENT));
        cards.add(rptCard("Total Bookings","📋",lTB,Theme.GREEN));
        cards.add(rptCard("Total Customers","👥",lTC,Theme.ORANGE));
        cards.add(rptCard("Month Revenue","💰",lRev,Theme.GOLD));

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        btnRow.setBackground(Theme.BG);
        JButton btnAll    = Theme.makeActionBtn("All Bookings",    Theme.ACCENT);
        JButton btnToday  = Theme.makeActionBtn("Today's",         Theme.GREEN);
        JButton btnCheckedIn = Theme.makeActionBtn("Checked In",   Theme.TEAL);
        JButton btnRooms  = Theme.makeActionBtn("Room Report",     Theme.PURPLE);
        btnRow.add(btnAll); btnRow.add(btnToday); btnRow.add(btnCheckedIn); btnRow.add(btnRooms);

        // Table
        String[] cols = {"Code","Guest","Room","Type","Check-In","Check-Out","Amount","Status","Date"};
        model = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        JTable table = new JTable(model); Theme.styleTable(table);
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(null);

        body.add(cards, BorderLayout.NORTH);
        JPanel mid = new JPanel(new BorderLayout(0,8)); mid.setBackground(Theme.BG);
        mid.add(btnRow, BorderLayout.NORTH); mid.add(scroll, BorderLayout.CENTER);
        body.add(mid, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        btnAll.addActionListener(e -> loadAll());
        btnToday.addActionListener(e -> loadToday());
        btnCheckedIn.addActionListener(e -> loadCheckedIn());
        btnRooms.addActionListener(e -> loadRooms());

        SwingUtilities.invokeLater(()-> {
            lTR.setText(String.valueOf(roomDAO.getTotal()));
            lTB.setText(String.valueOf(bookingDAO.getTotal()));
            lTC.setText(String.valueOf(customerDAO.getTotal()));
            lRev.setText(String.format("Rs.%,.0f", bookingDAO.getMonthRevenue()));
        });
    }

    private JPanel rptCard(String label, String icon, JLabel valLbl, Color color) {
        JPanel c = new JPanel(new BorderLayout(8,4));
        c.setBackground(Theme.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,228,236)), BorderFactory.createEmptyBorder(12,14,12,14)));
        JLabel ic = new JLabel(icon); ic.setFont(new Font("Segoe UI Emoji",Font.PLAIN,28));
        JPanel t = new JPanel(new BorderLayout()); t.setBackground(Theme.WHITE);
        JLabel lb = new JLabel(label); lb.setFont(new Font("Segoe UI",Font.PLAIN,11)); lb.setForeground(new Color(110,120,135));
        t.add(valLbl,BorderLayout.NORTH); t.add(lb,BorderLayout.CENTER);
        c.add(ic,BorderLayout.WEST); c.add(t,BorderLayout.CENTER); return c;
    }

    private void loadAll() {
        model.setRowCount(0);
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy");
        for (hotel.management.model.Booking b : bookingDAO.getAll())
            model.addRow(new Object[]{b.getBookingCode(),b.getCustomerName(),b.getRoomNumber(),b.getRoomType(),df.format(b.getCheckIn()),df.format(b.getCheckOut()),String.format("%.0f",b.getTotalAmount()),b.getStatus(),df.format(b.getCheckIn())});
    }

    private void loadToday() {
        model.setRowCount(0);
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy");
        String today = df.format(new Date());
        for (hotel.management.model.Booking b : bookingDAO.getTodayCheckIns())
            model.addRow(new Object[]{b.getBookingCode(),b.getCustomerName(),b.getRoomNumber(),b.getRoomType(),df.format(b.getCheckIn()),df.format(b.getCheckOut()),String.format("%.0f",b.getTotalAmount()),b.getStatus(),today});
    }

    private void loadCheckedIn() {
        model.setRowCount(0);
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy");
        for (hotel.management.model.Booking b : bookingDAO.getAll())
            if ("Checked-In".equals(b.getStatus()))
                model.addRow(new Object[]{b.getBookingCode(),b.getCustomerName(),b.getRoomNumber(),b.getRoomType(),df.format(b.getCheckIn()),df.format(b.getCheckOut()),String.format("%.0f",b.getTotalAmount()),b.getStatus(),""});
    }

    private void loadRooms() {
        model.setRowCount(0);
        for (hotel.management.model.Room r : roomDAO.getAll())
            model.addRow(new Object[]{r.getRoomNumber(),r.getRoomType(),r.getFloor(),"","","",String.format("%.0f",r.getPricePerNight()),r.getStatus(),""});
    }

    public void refresh() { loadAll(); }
}
