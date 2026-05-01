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

public class BillingPanel extends JPanel {
    private User currentUser;
    private BookingDAO dao = new BookingDAO();
    private DefaultTableModel model;
    private JLabel lblTodayRev, lblMonthRev, lblTotalRev;
    private JTable table;

    public BillingPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout()); setBackground(Theme.BG); buildUI();
    }

    private void buildUI() {
        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(14,18,14,18));
        JLabel title = new JLabel("  Billing & Revenue");
        title.setFont(new Font("Segoe UI",Font.BOLD,20)); title.setForeground(Theme.DARK_BLUE);

        JButton btnPopup = new JButton("🗗  Open in New Window");
        btnPopup.setFont(new Font("Segoe UI",Font.BOLD,14));
        btnPopup.setBackground(new Color(39,174,96));
        btnPopup.setForeground(Color.WHITE);
        btnPopup.setBorderPainted(false); btnPopup.setFocusPainted(false);
        btnPopup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPopup.setPreferredSize(new Dimension(220,38));
        btnPopup.addActionListener(e -> openInNewWindow());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        btnPanel.setBackground(Theme.WHITE);
        btnPanel.add(btnPopup);

        header.add(title, BorderLayout.WEST);
        header.add(btnPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // REVENUE CARDS
        JPanel cards = new JPanel(new GridLayout(1,3,14,0));
        cards.setBackground(Theme.BG);
        cards.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));

        lblTodayRev = new JLabel("Rs. 0");
        lblTodayRev.setFont(new Font("Segoe UI",Font.BOLD,24));
        lblTodayRev.setForeground(Theme.GREEN);
        lblMonthRev = new JLabel("Rs. 0");
        lblMonthRev.setFont(new Font("Segoe UI",Font.BOLD,24));
        lblMonthRev.setForeground(Theme.ACCENT);
        lblTotalRev = new JLabel("Rs. 0");
        lblTotalRev.setFont(new Font("Segoe UI",Font.BOLD,24));
        lblTotalRev.setForeground(Theme.GOLD);

        cards.add(revCard("Today's Revenue",   lblTodayRev, Theme.GREEN));
        cards.add(revCard("This Month Revenue",lblMonthRev, Theme.ACCENT));
        cards.add(revCard("Total Revenue",     lblTotalRev, Theme.GOLD));

        // TABLE
        String[] cols = {"Code","Guest","Room","Check-In","Check-Out","Nights","Amount (Rs.)","Status"};
        model = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        Theme.styleTable(table);

        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c);
                String val = v!=null ? v.toString() : "";
                if("Confirmed".equals(val))    l.setForeground(Theme.ACCENT);
                else if("Checked-In".equals(val))  l.setForeground(Theme.GREEN);
                else if("Checked-Out".equals(val)) l.setForeground(Theme.ORANGE);
                else if("Cancelled".equals(val))   l.setForeground(Theme.RED);
                l.setFont(l.getFont().deriveFont(Font.BOLD));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0,14,14,14));

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.BG);
        center.add(cards, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    // ─── OPEN IN NEW WINDOW ──────────────────────────────
    private void openInNewWindow() {
        new BillingWindow(currentUser);
    }

    // ─── PDF / HTML EXPORT ───────────────────────────────
    private void exportPDF() {
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
                "Report ready!\n\nLocation:\n" + file.getAbsolutePath() +
                "\n\nBrowser mein khulegi — Ctrl+P se PDF save karein.",
                "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            if (Desktop.isDesktopSupported())
                Desktop.getDesktop().browse(file.toURI());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
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
        sb.append("h1{color:#1a3a5c;margin:0;}");
        sb.append(".topbar{background:#1a3a5c;color:#fff;padding:18px 24px;border-radius:8px;margin-bottom:20px;}");
        sb.append(".topbar p{margin:4px 0;font-size:13px;opacity:0.85;}");
        sb.append(".cards{display:flex;gap:14px;margin-bottom:20px;}");
        sb.append(".card{flex:1;padding:14px 18px;border-radius:8px;border-left:5px solid #ccc;}");
        sb.append(".g{border-color:#27ae60;background:#f0faf4;} .b{border-color:#2980b9;background:#eaf4fc;} .y{border-color:#d4af37;background:#fdf9e8;}");
        sb.append(".cv{font-size:20px;font-weight:bold;margin:4px 0;} .g .cv{color:#27ae60;} .b .cv{color:#2980b9;} .y .cv{color:#b8971d;}");
        sb.append(".cl{font-size:12px;color:#666;}");
        sb.append("table{width:100%;border-collapse:collapse;font-size:13px;}");
        sb.append("th{background:#1a3a5c;color:#fff;padding:10px 12px;text-align:left;}");
        sb.append("td{padding:8px 12px;border-bottom:1px solid #eee;}");
        sb.append("tr:nth-child(even) td{background:#f7f8fa;}");
        sb.append(".tot td{background:#1a3a5c!important;color:#fff;font-weight:bold;}");
        sb.append(".foot{margin-top:18px;text-align:center;font-size:11px;color:#aaa;border-top:1px solid #eee;padding-top:10px;}");
        sb.append("@media print{.topbar{-webkit-print-color-adjust:exact;print-color-adjust:exact;}}");
        sb.append("</style></head><body>");

        sb.append("<div class='topbar'><h1>Hotel Management System</h1>");
        sb.append("<p>Revenue Report &nbsp;|&nbsp; Generated: ").append(dtf.format(new Date())).append("</p>");
        sb.append("<p>By: ").append(currentUser.getFullName()).append("</p></div>");

        sb.append("<div class='cards'>");
        sb.append("<div class='card g'><div class='cl'>Today Revenue</div><div class='cv'>Rs. ")
          .append(String.format("%,.0f", dao.getTodayRevenue())).append("</div></div>");
        sb.append("<div class='card b'><div class='cl'>Month Revenue</div><div class='cv'>Rs. ")
          .append(String.format("%,.0f", dao.getMonthRevenue())).append("</div></div>");
        sb.append("<div class='card y'><div class='cl'>Total Revenue</div><div class='cv'>Rs. ")
          .append(String.format("%,.0f", total)).append("</div></div>");
        sb.append("</div>");

        sb.append("<table><tr><th>#</th><th>Code</th><th>Guest</th><th>Room</th>");
        sb.append("<th>Check-In</th><th>Check-Out</th><th>Nights</th><th>Amount (Rs.)</th><th>Status</th></tr>");

        int i=1;
        for (Booking b : list) {
            long nights = (b.getCheckOut().getTime()-b.getCheckIn().getTime())/(86400000L);
            sb.append("<tr><td>").append(i++).append("</td>")
              .append("<td>").append(b.getBookingCode()).append("</td>")
              .append("<td>").append(b.getCustomerName()).append("</td>")
              .append("<td>").append(b.getRoomNumber()).append("</td>")
              .append("<td>").append(df.format(b.getCheckIn())).append("</td>")
              .append("<td>").append(df.format(b.getCheckOut())).append("</td>")
              .append("<td>").append(nights).append("</td>")
              .append("<td><b>").append(String.format("%,.0f", b.getTotalAmount())).append("</b></td>")
              .append("<td>").append(b.getStatus()).append("</td></tr>");
        }
        sb.append("<tr class='tot'><td colspan='7'>TOTAL</td>")
          .append("<td>Rs. ").append(String.format("%,.0f", total)).append("</td><td></td></tr>");
        sb.append("</table>");
        sb.append("<div class='foot'>Hotel Management System &copy; ")
          .append(new SimpleDateFormat("yyyy").format(new Date())).append("</div>");
        sb.append("</body></html>");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) { pw.print(sb); }
    }

    // ─── PRINT TABLE ─────────────────────────────────────
    private void printReport() {
        try {
            table.print(JTable.PrintMode.FIT_WIDTH,
                new java.awt.print.MessageFormat("Hotel Revenue Report"),
                new java.awt.print.MessageFormat("Page {0}"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,"Print error: "+ex.getMessage());
        }
    }

    private JPanel revCard(String label, JLabel valLbl, Color color) {
        JPanel card = new JPanel(new BorderLayout(10,6));
        card.setBackground(Theme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(color.getRed(),color.getGreen(),color.getBlue(),80),2),
            BorderFactory.createEmptyBorder(16,18,16,18)));
        JPanel txt = new JPanel(new BorderLayout(0,4)); txt.setBackground(Theme.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI",Font.PLAIN,13)); lbl.setForeground(new Color(100,110,125));
        txt.add(valLbl, BorderLayout.NORTH); txt.add(lbl, BorderLayout.CENTER);
        card.add(txt, BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        lblTodayRev.setText(String.format("Rs. %,.0f", dao.getTodayRevenue()));
        lblMonthRev.setText(String.format("Rs. %,.0f", dao.getMonthRevenue()));
        model.setRowCount(0);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        double total = 0;
        for (Booking b : dao.getAll()) {
            long nights = (b.getCheckOut().getTime()-b.getCheckIn().getTime())/(86400000L);
            model.addRow(new Object[]{
                b.getBookingCode(), b.getCustomerName(), b.getRoomNumber(),
                df.format(b.getCheckIn()), df.format(b.getCheckOut()), nights,
                String.format("%,.0f", b.getTotalAmount()), b.getStatus()
            });
            if (!"Cancelled".equals(b.getStatus())) total += b.getTotalAmount();
        }
        lblTotalRev.setText(String.format("Rs. %,.0f", total));
    }
}