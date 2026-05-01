package hotel.management.ui;

import hotel.management.model.User;
import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public SettingsPanel(User user) {
        setLayout(new BorderLayout()); setBackground(Theme.BG);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.WHITE); header.setBorder(BorderFactory.createEmptyBorder(14,18,14,18));
        JLabel title = new JLabel("⚙  Settings");
        title.setFont(new Font("Segoe UI",Font.BOLD,20)); title.setForeground(Theme.DARK_BLUE);
        header.add(title, BorderLayout.WEST); add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout()); body.setBackground(Theme.BG);
        GridBagConstraints g = new GridBagConstraints(); g.insets=new Insets(12,12,12,12);
        g.fill=GridBagConstraints.HORIZONTAL;

        JPanel card = new JPanel(new GridBagLayout()); card.setBackground(Theme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,228,236)), BorderFactory.createEmptyBorder(20,30,20,30)));

        GridBagConstraints gc = new GridBagConstraints(); gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(8,4,8,4);

        addSetting(card,gc,0,"Hotel Name:","Grand Palace Hotel");
        addSetting(card,gc,1,"Database Host:","localhost");
        addSetting(card,gc,2,"Database Name:","hotel_db");
        addSetting(card,gc,3,"Currency:","PKR (Rs.)");
        addSetting(card,gc,4,"Check-In Time:","12:00 PM");
        addSetting(card,gc,5,"Check-Out Time:","11:00 AM");

        JButton save = Theme.makeActionBtn("💾 Save Settings", Theme.GREEN);
        save.setPreferredSize(new Dimension(200,38));
        gc.gridx=0; gc.gridy=6; gc.gridwidth=2; card.add(save,gc);
        save.addActionListener(e -> JOptionPane.showMessageDialog(this,"Settings saved!"));

        body.add(card,g); add(body, BorderLayout.CENTER);
    }

    private void addSetting(JPanel p, GridBagConstraints g, int row, String label, String val) {
        JLabel l = new JLabel(label); l.setFont(new Font("Segoe UI",Font.BOLD,13)); l.setForeground(Theme.MED_BLUE);
        g.gridx=0; g.gridy=row; g.weightx=0.3; g.gridwidth=1; p.add(l,g);
        JTextField f = Theme.makeField(20); f.setText(val);
        g.gridx=1; g.weightx=0.7; p.add(f,g);
    }

    public void refresh(){}
}
