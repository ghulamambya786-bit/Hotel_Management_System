package hotel.management.ui;

import hotel.management.dao.RoomDAO;
import hotel.management.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class RoomPanel extends JPanel {
    private User currentUser;
    private RoomDAO dao = new RoomDAO();
    private DefaultTableModel model;
    private JTable table;
    private int selectedId = -1;

    private JTextField txtRoomNo, txtPrice, txtDesc, txtFloor, txtCap;
    private JComboBox<String> cmbType, cmbStatus;

    public RoomPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(14,18,14,18));
        JLabel title = new JLabel("🛏  Room Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Theme.DARK_BLUE);
        JLabel sub = new JLabel("Manage hotel rooms — Add, Edit, Delete");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12)); sub.setForeground(new Color(130,140,155));
        JPanel tt = new JPanel(new GridLayout(2,1)); tt.setBackground(Theme.WHITE);
        tt.add(title); tt.add(sub);
        header.add(tt, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Left form
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Theme.WHITE);
        formPanel.setPreferredSize(new Dimension(280, 0));
        formPanel.setBorder(BorderFactory.createMatteBorder(0,0,0,1,new Color(220,228,236)));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(6,4,6,4);

        txtRoomNo = Theme.makeField(15); txtPrice = Theme.makeField(15);
        txtDesc   = Theme.makeField(15); txtFloor  = Theme.makeField(15);
        txtCap    = Theme.makeField(15);
        cmbType   = Theme.makeCombo(new String[]{"Single","Double","Suite","Deluxe"});
        cmbStatus = Theme.makeCombo(new String[]{"Available","Booked","Maintenance"});

        addRow(form, g, 0, "Room Number *", txtRoomNo);
        addRow(form, g, 1, "Room Type",     cmbType);
        addRow(form, g, 2, "Price/Night *", txtPrice);
        addRow(form, g, 3, "Status",        cmbStatus);
        addRow(form, g, 4, "Floor",         txtFloor);
        addRow(form, g, 5, "Capacity",      txtCap);
        addRow(form, g, 6, "Description",   txtDesc);

        JPanel btnRow = new JPanel(new GridLayout(2,2,6,6));
        btnRow.setBackground(Theme.WHITE);
        JButton btnAdd  = Theme.makeActionBtn("➕ Add Room",  Theme.GREEN);
        JButton btnUpd  = Theme.makeActionBtn("✏ Update",    Theme.AMBER);
        JButton btnDel  = Theme.makeActionBtn("🗑 Delete",    Theme.RED);
        JButton btnClr  = Theme.makeActionBtn("🔄 Clear",     new Color(120,130,145));
        btnRow.add(btnAdd); btnRow.add(btnUpd); btnRow.add(btnDel); btnRow.add(btnClr);

        g.gridx=0; g.gridy=7; g.gridwidth=2;
        form.add(btnRow, g);

        formPanel.add(form, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID","Room No","Type","Price/Night","Status","Floor","Cap","Description"};
        model = new DefaultTableModel(cols, 0){ public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        Theme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(45);
        table.getColumnModel().getColumn(2).setMaxWidth(80);
        table.getColumnModel().getColumn(5).setMaxWidth(80);
        table.getColumnModel().getColumn(6).setMaxWidth(45);

        // Status color renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel)super.getTableCellRendererComponent(t,val,sel,foc,r,c);
                String v = val != null ? val.toString() : "";
                if ("Available".equals(v)) l.setForeground(Theme.GREEN);
                else if ("Booked".equals(v)) l.setForeground(Theme.ACCENT);
                else l.setForeground(Theme.RED);
                l.setFont(l.getFont().deriveFont(Font.BOLD));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, scroll);
        split.setDividerLocation(280); split.setDividerSize(3); split.setBorder(null);
        add(split, BorderLayout.CENTER);

        // Events
        btnAdd.addActionListener(e -> addRoom());
        btnUpd.addActionListener(e -> updateRoom());
        btnDel.addActionListener(e -> deleteRoom());
        btnClr.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) loadSelected();
        });
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String lbl, JComponent field) {
        JLabel l = new JLabel(lbl); l.setFont(new Font("Segoe UI", Font.BOLD, 12)); l.setForeground(Theme.MED_BLUE);
        g.gridx=0; g.gridy=row; g.gridwidth=1; g.weightx=0.35; p.add(l, g);
        g.gridx=1; g.weightx=0.65; g.gridwidth=1; p.add(field, g);
    }

    private void addRoom() {
        try {
            if (txtRoomNo.getText().trim().isEmpty() || txtPrice.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,"Room number aur price required!"); return;
            }
            Room r = new Room();
            r.setRoomNumber(txtRoomNo.getText().trim());
            r.setRoomType(cmbType.getSelectedItem().toString());
            r.setPricePerNight(Double.parseDouble(txtPrice.getText().trim()));
            r.setStatus(cmbStatus.getSelectedItem().toString());
            r.setFloor(txtFloor.getText().trim());
            r.setCapacity(txtCap.getText().isEmpty() ? 1 : Integer.parseInt(txtCap.getText().trim()));
            r.setDescription(txtDesc.getText().trim());
            if (dao.add(r)) { JOptionPane.showMessageDialog(this,"✅ Room added successfully!"); clearForm(); refresh(); }
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this,"Price/Capacity must be numbers!"); }
    }

    private void updateRoom() {
        if (selectedId<0){ JOptionPane.showMessageDialog(this,"Pehle table se room select karein!"); return; }
        try {
            Room r = new Room(); r.setId(selectedId);
            r.setRoomType(cmbType.getSelectedItem().toString());
            r.setPricePerNight(Double.parseDouble(txtPrice.getText().trim()));
            r.setStatus(cmbStatus.getSelectedItem().toString());
            r.setFloor(txtFloor.getText().trim());
            r.setCapacity(txtCap.getText().isEmpty() ? 1 : Integer.parseInt(txtCap.getText().trim()));
            r.setDescription(txtDesc.getText().trim());
            if (dao.update(r)) { JOptionPane.showMessageDialog(this,"✅ Room updated!"); clearForm(); refresh(); }
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this,"Price/Capacity must be numbers!"); }
    }

    private void deleteRoom() {
        if (selectedId<0){ JOptionPane.showMessageDialog(this,"Pehle table se room select karein!"); return; }
        if (JOptionPane.showConfirmDialog(this,"Is room ko delete karna chahte hain?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
            if (dao.delete(selectedId)) { JOptionPane.showMessageDialog(this,"✅ Room deleted!"); clearForm(); refresh(); }
    }

    private void clearForm() {
        txtRoomNo.setText(""); txtPrice.setText(""); txtDesc.setText("");
        txtFloor.setText(""); txtCap.setText("");
        cmbType.setSelectedIndex(0); cmbStatus.setSelectedIndex(0);
        selectedId=-1; table.clearSelection();
    }

    private void loadSelected() {
        int row = table.getSelectedRow();
        selectedId = Integer.parseInt(model.getValueAt(row,0).toString());
        txtRoomNo.setText(model.getValueAt(row,1).toString());
        cmbType.setSelectedItem(model.getValueAt(row,2).toString());
        txtPrice.setText(model.getValueAt(row,3).toString());
        cmbStatus.setSelectedItem(model.getValueAt(row,4).toString());
        txtFloor.setText(model.getValueAt(row,5) != null ? model.getValueAt(row,5).toString() : "");
        txtCap.setText(model.getValueAt(row,6).toString());
        txtDesc.setText(model.getValueAt(row,7) != null ? model.getValueAt(row,7).toString() : "");
    }

    public void refresh() {
        model.setRowCount(0);
        for (Room r : dao.getAll())
            model.addRow(new Object[]{r.getId(),r.getRoomNumber(),r.getRoomType(),r.getPricePerNight(),r.getStatus(),r.getFloor(),r.getCapacity(),r.getDescription()});
    }
}
