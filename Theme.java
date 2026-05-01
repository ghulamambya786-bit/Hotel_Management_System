package hotel.management.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class Theme {
    public static final Color DARK_BLUE  = new Color(13, 27, 62);
    public static final Color NAV_BG     = new Color(10, 25, 55);
    public static final Color NAV_HOVER  = new Color(26, 82, 118);
    public static final Color NAV_SEL    = new Color(41, 128, 185);
    public static final Color MED_BLUE   = new Color(26, 82, 118);
    public static final Color ACCENT     = new Color(41, 128, 185);
    public static final Color GOLD       = new Color(212, 175, 55);
    public static final Color BG         = new Color(240, 244, 248);
    public static final Color WHITE      = Color.WHITE;
    public static final Color GREEN      = new Color(39, 174, 96);
    public static final Color ORANGE     = new Color(230, 126, 34);
    public static final Color RED        = new Color(192, 57, 43);
    public static final Color PURPLE     = new Color(142, 68, 173);
    public static final Color TEAL       = new Color(22, 160, 133);
    public static final Color AMBER      = new Color(243, 156, 18);
    public static final Color CARD_BG    = Color.WHITE;

    public static JButton makeActionBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(WHITE);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(120, 34));
        return b;
    }

    public static void styleTable(JTable t) {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(30);
        t.setIntercellSpacing(new Dimension(8, 4));
        t.setShowGrid(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(new Color(230,235,240));
        t.setSelectionBackground(new Color(210, 228, 245));
        t.setSelectionForeground(DARK_BLUE);
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setBackground(MED_BLUE); h.setForeground(WHITE);
        h.setPreferredSize(new Dimension(0, 36));
        h.setBorder(BorderFactory.createEmptyBorder());
    }

    public static JPanel sectionTitle(String title, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(10,14,6,14));
        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(color);
        p.add(l);
        return p;
    }

    public static JTextField makeField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200,210,220)),
            BorderFactory.createEmptyBorder(5,8,5,8)));
        return f;
    }

    public static JComboBox<String> makeCombo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return c;
    }

    public static JPanel statCard(String icon, String value, String label, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 6)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setColor(WHITE); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(color); g2.fillRoundRect(0,0,6,getHeight(),4,4);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12,18,12,12));

        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(54,54));
        JLabel iconLbl = new JLabel(icon, JLabel.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        iconPanel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        iconPanel.add(iconLbl);

        JPanel textPanel = new JPanel(new BorderLayout(0,2));
        textPanel.setOpaque(false);
        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valLbl.setForeground(color);
        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLbl.setForeground(new Color(120,130,145));
        JLabel viewLbl = new JLabel("View Details →");
        viewLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        viewLbl.setForeground(color);

        textPanel.add(valLbl, BorderLayout.NORTH);
        textPanel.add(lblLbl, BorderLayout.CENTER);
        textPanel.add(viewLbl, BorderLayout.SOUTH);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }
}
