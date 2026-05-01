package hotel.management.ui;

import hotel.management.dao.BookingDAO;
import hotel.management.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CheckInOutPanel extends JPanel {
    private User currentUser;
    private BookingDAO dao = new BookingDAO();
    private DefaultTableModel checkInModel, checkOutModel;
    private JTable checkInTable, checkOutTable;

    public CheckInOutPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout()); setBackground(Theme.BG); buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(14,18,14,18));
        JLabel title = new JLabel("🔄  Check In / Check Out");
        title.setFont(new Font("Segoe UI",Font.BOLD,20)); title.setForeground(Theme.DARK_BLUE);
        JLabel sub = new JLabel("Process guest check-ins and check-outs");
        sub.setFont(new Font("Segoe UI",Font.PLAIN,12)); sub.setForeground(new Color(130,140,155));
        JPanel tt = new JPanel(new GridLayout(2,1)); tt.setBackground(Theme.WHITE);
        tt.add(title); tt.add(sub); header.add(tt, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildCheckInPanel(), buildCheckOutPanel());
        split.setDividerLocation(0.5); split.setResizeWeight(0.5);
        split.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        split.setBackground(Theme.BG);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildCheckInPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.WHITE);
        p.setBorder(BorderFactory.createLineBorder(new Color(22,160,133), 2));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Theme.TEAL);
        titleBar.setBorder(BorderFactory.createEmptyBorder(10,14,10,14));
        JLabel lbl = new JLabel("📥  Today's Check In — Confirmed Bookings");
        lbl.setFont(new Font("Segoe UI",Font.BOLD,14)); lbl.setForeground(Color.WHITE);
        titleBar.add(lbl, BorderLayout.WEST);

        String[] cols = {"ID","Code","Guest Name","Room No","Room Type","Check-In Date","Amount","Status"};
        checkInModel = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        checkInTable = new JTable(checkInModel); Theme.styleTable(checkInTable);
        checkInTable.getTableHeader().setBackground(Theme.TEAL);
        JScrollPane scroll = new JScrollPane(checkInTable); scroll.setBorder(null);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnPanel.setBackground(new Color(240,252,250));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(200,230,225)));

        JButton btnCheckIn = new JButton("✅  Process Check In");
        btnCheckIn.setFont(new Font("Segoe UI",Font.BOLD,13));
        btnCheckIn.setBackground(Theme.TEAL); btnCheckIn.setForeground(Color.WHITE);
        btnCheckIn.setBorderPainted(false); btnCheckIn.setFocusPainted(false);
        btnCheckIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckIn.setPreferredSize(new Dimension(200,38));

        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setFont(new Font("Segoe UI",Font.PLAIN,12));
        btnRefresh.setBackground(new Color(220,235,230)); btnRefresh.setForeground(Theme.TEAL);
        btnRefresh.setBorderPainted(false); btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setPreferredSize(new Dimension(110,38));

        btnPanel.add(btnCheckIn); btnPanel.add(btnRefresh);

        p.add(titleBar, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);

        btnCheckIn.addActionListener(e -> processCheckIn());
        btnRefresh.addActionListener(e -> refresh());
        return p;
    }

    private JPanel buildCheckOutPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.WHITE);
        p.setBorder(BorderFactory.createLineBorder(Theme.RED, 2));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Theme.RED);
        titleBar.setBorder(BorderFactory.createEmptyBorder(10,14,10,14));
        JLabel lbl = new JLabel("📤  Today's Check Out — Checked-In Guests");
        lbl.setFont(new Font("Segoe UI",Font.BOLD,14)); lbl.setForeground(Color.WHITE);
        titleBar.add(lbl, BorderLayout.WEST);

        String[] cols = {"ID","Code","Guest Name","Room No","Room Type","Check-Out Date","Amount","Status"};
        checkOutModel = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        checkOutTable = new JTable(checkOutModel); Theme.styleTable(checkOutTable);
        checkOutTable.getTableHeader().setBackground(Theme.RED);
        JScrollPane scroll = new JScrollPane(checkOutTable); scroll.setBorder(null);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnPanel.setBackground(new Color(252,240,240));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1,0,0,0,new Color(230,200,200)));

        JButton btnCheckOut = new JButton("🚪  Process Check Out");
        btnCheckOut.setFont(new Font("Segoe UI",Font.BOLD,13));
        btnCheckOut.setBackground(Theme.RED); btnCheckOut.setForeground(Color.WHITE);
        btnCheckOut.setBorderPainted(false); btnCheckOut.setFocusPainted(false);
        btnCheckOut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckOut.setPreferredSize(new Dimension(200,38));

        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setFont(new Font("Segoe UI",Font.PLAIN,12));
        btnRefresh.setBackground(new Color(235,220,220)); btnRefresh.setForeground(Theme.RED);
        btnRefresh.setBorderPainted(false); btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setPreferredSize(new Dimension(110,38));

        btnPanel.add(btnCheckOut); btnPanel.add(btnRefresh);

        p.add(titleBar, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);

        btnCheckOut.addActionListener(e -> processCheckOut());
        btnRefresh.addActionListener(e -> refresh());
        return p;
    }

    private void processCheckIn() {
        int row = checkInTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Table se booking select karein!"); return; }
        int id = Integer.parseInt(checkInModel.getValueAt(row,0).toString());
        int roomId = getRoomIdFromCheckInRow(row);
        String status = checkInModel.getValueAt(row,7).toString();
        if ("Checked-In".equals(status)) { JOptionPane.showMessageDialog(this,"Yeh guest pehle se check in hai!"); return; }

        String time = new SimpleDateFormat("hh:mm a").format(new Date());
        int conf = JOptionPane.showConfirmDialog(this,
            "Guest: " + checkInModel.getValueAt(row,2) + "\nRoom: " + checkInModel.getValueAt(row,3) +
            "\nCheck-In Time: " + time + "\n\nCheck In karein?", "Confirm Check In", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            if (dao.checkIn(id, roomId, time)) {
                JOptionPane.showMessageDialog(this,"✅ Guest successfully checked in!\nTime: " + time);
                refresh();
            }
        }
    }

    private void processCheckOut() {
        int row = checkOutTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Table se booking select karein!"); return; }
        int id = Integer.parseInt(checkOutModel.getValueAt(row,0).toString());
        int roomId = getRoomIdFromCheckOutRow(row);

        String time = new SimpleDateFormat("hh:mm a").format(new Date());
        double amount = 0;
        try { amount = Double.parseDouble(checkOutModel.getValueAt(row,6).toString()); } catch(Exception ignored){}

        int conf = JOptionPane.showConfirmDialog(this,
            "Guest: " + checkOutModel.getValueAt(row,2) + "\nRoom: " + checkOutModel.getValueAt(row,3) +
            "\nTotal Bill: Rs. " + String.format("%,.2f",amount) +
            "\nCheck-Out Time: " + time + "\n\nCheck Out karein?", "Confirm Check Out", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            if (dao.checkOut(id, roomId, time)) {
                JOptionPane.showMessageDialog(this,"✅ Guest checked out!\nRoom ab available hai.\nBill: Rs." + String.format("%,.2f",amount));
                refresh();
            }
        }
    }

    private int getRoomIdFromCheckInRow(int row) {
        String roomNo = checkInModel.getValueAt(row,3).toString();
        for (Booking b : dao.getAll()) if (b.getRoomNumber().equals(roomNo)) return b.getRoomId();
        return -1;
    }
    private int getRoomIdFromCheckOutRow(int row) {
        String roomNo = checkOutModel.getValueAt(row,3).toString();
        for (Booking b : dao.getAll()) if (b.getRoomNumber().equals(roomNo)) return b.getRoomId();
        return -1;
    }

    public void refresh() {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        checkInModel.setRowCount(0);
        for (Booking b : dao.getTodayCheckIns())
            checkInModel.addRow(new Object[]{b.getId(),b.getBookingCode(),b.getCustomerName(),b.getRoomNumber(),b.getRoomType(),df.format(b.getCheckIn()),String.format("%.0f",b.getTotalAmount()),b.getStatus()});
        checkOutModel.setRowCount(0);
        for (Booking b : dao.getTodayCheckOuts())
            checkOutModel.addRow(new Object[]{b.getId(),b.getBookingCode(),b.getCustomerName(),b.getRoomNumber(),b.getRoomType(),df.format(b.getCheckOut()),String.format("%.0f",b.getTotalAmount()),b.getStatus()});
    }
}
