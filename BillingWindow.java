package hotel.management.ui;

import hotel.management.dao.BookingDAO;
import hotel.management.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Desktop;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class BillingWindow extends JFrame {

    private User currentUser;
    private BookingDAO dao = new BookingDAO();
    private DefaultTableModel model;
    private JTable table;
    private JLabel lblTodayRev, lblMonthRev, lblTotalRev;

    public BillingWindow(User user) {
        this.currentUser = user;
        setTitle("Billing & Revenue — Hotel Management System");
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        buildUI();
        loadData();
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // ── TOP BAR ──
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(26, 82, 118));
        topBar.setPreferredSize(new Dimension(0, 55));
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));

        JLabel lblTitle = new JLabel("💰  Billing & Revenue Report");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(212, 175, 55));

        JLabel lblInfo = new JLabel(
            new SimpleDateFormat("dd MMM yyyy").format(new Date()) +
            "  |  " + currentUser.getFullName());
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(180, 210, 240));

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(lblInfo,  BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── CONTENT ──
        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(Theme.BG);

        // Buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnBar.setBackground(Theme.WHITE);
        btnBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 228, 236)));

        JButton btnPrint = makeBtn("🖨 Print", new Color(41, 128, 185));
        JButton btnPDF   = makeBtn("📄 Export PDF", new Color(192, 57, 43));
        JButton btnClose = makeBtn("✖ Close", new Color(120, 130, 145));

        btnBar.add(btnPrint);
        btnBar.add(btnPDF);
        btnBar.add(btnClose);

        // Revenue Cards
        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 0));
        cards.setBackground(Theme.BG);
        cards.setBorder(BorderFactory.createEmptyBorder(14, 14, 10, 14));

        lblTodayRev = new JLabel("Rs. 0");
        lblTodayRev.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTodayRev.setForeground(Theme.GREEN);

        lblMonthRev = new JLabel("Rs. 0");
        lblMonthRev.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblMonthRev.setForeground(Theme.ACCENT);

        lblTotalRev = new JLabel("Rs. 0");
        lblTotalRev.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotalRev.setForeground(Theme.GOLD);

        cards.add(revCard("Today's Revenue",    lblTodayRev, Theme.GREEN));
        cards.add(revCard("This Month Revenue", lblMonthRev, Theme.ACCENT));
        cards.add(revCard("Total Revenue",      lblTotalRev, Theme.GOLD));

        // Table
        String[] cols = {"Code","Guest","Room","Check-In","Check-Out","Nights","Amount (Rs.)","Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        Theme.styleTable(table);

        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                String val = v != null ? v.toString() : "";
                if      ("Confirmed".equals(val))   l.setForeground(Theme.ACCENT);
                else if ("Checked-In".equals(val))  l.setForeground(Theme.GREEN);
                else if ("Checked-Out".equals(val)) l.setForeground(Theme.ORANGE);
                else if ("Cancelled".equals(val))   l.setForeground(Theme.RED);
                else                                l.setForeground(Theme.AMBER);
                l.setFont(l.getFont().deriveFont(Font.BOLD));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));

        JPanel topContent = new JPanel(new BorderLayout());
        topContent.setBackground(Theme.BG);
        topContent.add(btnBar,  BorderLayout.NORTH);
        topContent.add(cards,   BorderLayout.CENTER);

        content.add(topContent, BorderLayout.NORTH);
        content.add(scroll,     BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        // Events
        btnPrint.addActionListener(e -> doPrint());
        btnPDF.addActionListener(e -> doPDF());
        btnClose.addActionListener(e -> dispose());
    }

    private void loadData() {
        lblTodayRev.setText(String.format("Rs. %,.0f", dao.getTodayRevenue()));
        lblMonthRev.setText(String.format("Rs. %,.0f", dao.getMonthRevenue()));
        model.setRowCount(0);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        double total = 0;
        for (Booking b : dao.getAll()) {
            long nights = (b.getCheckOut().getTime() - b.getCheckIn().getTime()) / 86400000L;
            model.addRow(new Object[]{
                b.getBookingCode(), b.getCustomerName(), b.getRoomNumber(),
                df.format(b.getCheckIn()), df.format(b.getCheckOut()),
                nights, String.format("%,.0f", b.getTotalAmount()), b.getStatus()
            });
            if (!"Cancelled".equals(b.getStatus())) total += b.getTotalAmount();
        }
        lblTotalRev.setText(String.format("Rs. %,.0f", total));
    }

    private void doPrint() {
        try {
            table.print(JTable.PrintMode.FIT_WIDTH,
                new java.awt.print.MessageFormat("Hotel Revenue Report"),
                new java.awt.print.MessageFormat("Page {0}"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage());
        }
    }

    private void doPDF() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Report Save Karein");
        fc.setSelectedFile(new File("Revenue_Report_" +
            new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".html"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".html"))
            file = new File(file.getAbsolutePath() + ".html");
        try {
            writeHTML(file);
            JOptionPane.showMessageDialog(this,
                "✅ Report ready!\n\nLocation:\n" + file.getAbsolutePath() +
                "\n\nBrowser mein Ctrl+P se PDF save karein.",
                "Export OK", JOptionPane.INFORMATION_MESSAGE);
            if (Desktop.isDesktopSupported())
                Desktop.getDesktop().browse(file.toURI());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void writeHTML(File file) throws Exception {
        SimpleDateFormat df  = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dtf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        List<Booking> list   = dao.getAll();
        double total = 0;
        for (Booking b : list) if (!"Cancelled".equals(b.getStatus())) total += b.getTotalAmount();

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Revenue Report</title><style>");
        sb.append("body{font-family:Arial,sans-serif;margin:30px;color:#222;}");
        sb.append(".topbar{background:#1a3a5c;color:#fff;padding:18px 24px;border-radius:8px;margin-bottom:20px;}");
        sb.append(".topbar h1{margin:0;font-size:22px;} .topbar p{margin:4px 0;font-size:12px;opacity:0.85;}");
        sb.append(".cards{display:flex;gap:14px;margin-bottom:20px;}");
        sb.append(".card{flex:1;padding:14px 18px;border-radius:8px;border-left:5px solid #ccc;}");
        sb.append(".g{border-color:#27ae60;background:#f0faf4;}.b{border-color:#2980b9;background:#eaf4fc;}.y{border-color:#d4af37;background:#fdf9e8;}");
        sb.append(".cv{font-size:20px;font-weight:bold;margin:4px 0;}.g .cv{color:#27ae60;}.b .cv{color:#2980b9;}.y .cv{color:#b8971d;}");
        sb.append(".cl{font-size:12px;color:#666;}");
        sb.append("table{width:100%;border-collapse:collapse;font-size:13px;}");
        sb.append("th{background:#1a3a5c;color:#fff;padding:10px 12px;text-align:left;}");
        sb.append("td{padding:8px 12px;border-bottom:1px solid #eee;}");
        sb.append("tr:nth-child(even) td{background:#f7f8fa;}");
        sb.append(".tot td{background:#1a3a5c!important;color:#fff;font-weight:bold;}");
        sb.append(".foot{margin-top:18px;text-align:center;font-size:11px;color:#aaa;border-top:1px solid #eee;padding-top:10px;}");
        sb.append("</style></head><body>");
        sb.append("<div class='topbar'><h1>Hotel Management System</h1>");
        sb.append("<p>Revenue Report | Generated: ").append(dtf.format(new Date())).append("</p>");
        sb.append("<p>By: ").append(currentUser.getFullName()).append("</p></div>");
        sb.append("<div class='cards'>");
        sb.append("<div class='card g'><div class='cl'>Today Revenue</div><div class='cv'>Rs. ").append(String.format("%,.0f", dao.getTodayRevenue())).append("</div></div>");
        sb.append("<div class='card b'><div class='cl'>Month Revenue</div><div class='cv'>Rs. ").append(String.format("%,.0f", dao.getMonthRevenue())).append("</div></div>");
        sb.append("<div class='card y'><div class='cl'>Total Revenue</div><div class='cv'>Rs. ").append(String.format("%,.0f", total)).append("</div></div>");
        sb.append("</div>");
        sb.append("<table><tr><th>#</th><th>Code</th><th>Guest</th><th>Room</th><th>Check-In</th><th>Check-Out</th><th>Nights</th><th>Amount (Rs.)</th><th>Status</th></tr>");
        int i = 1;
        for (Booking b : list) {
            long nights = (b.getCheckOut().getTime() - b.getCheckIn().getTime()) / 86400000L;
            sb.append("<tr><td>").append(i++).append("</td><td>").append(b.getBookingCode())
              .append("</td><td>").append(b.getCustomerName())
              .append("</td><td>").append(b.getRoomNumber())
              .append("</td><td>").append(df.format(b.getCheckIn()))
              .append("</td><td>").append(df.format(b.getCheckOut()))
              .append("</td><td>").append(nights)
              .append("</td><td><b>").append(String.format("%,.0f", b.getTotalAmount()))
              .append("</b></td><td>").append(b.getStatus()).append("</td></tr>");
        }
        sb.append("<tr class='tot'><td colspan='7'>TOTAL</td><td>Rs. ").append(String.format("%,.0f", total)).append("</td><td></td></tr>");
        sb.append("</table><div class='foot'>Hotel Management System &copy; ").append(new SimpleDateFormat("yyyy").format(new Date())).append("</div></body></html>");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) { pw.print(sb); }
    }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(150, 34));
        return b;
    }

    private JPanel revCard(String label, JLabel valLbl, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Theme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80), 2),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(100, 110, 125));
        card.add(valLbl, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);
        return card;
    }
}
