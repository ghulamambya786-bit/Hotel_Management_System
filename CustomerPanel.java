package hotel.management.ui;

import hotel.management.dao.CustomerDAO;
import hotel.management.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class CustomerPanel extends JPanel {
    private User currentUser;
    private CustomerDAO dao = new CustomerDAO();
    private DefaultTableModel model;
    private JTable table;
    private int selectedId = -1;
    private JTextField txtName, txtCnic, txtPhone, txtEmail, txtAddr, txtCity;

    public CustomerPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout()); setBackground(Theme.BG); buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(14,18,14,18));
        JLabel title = new JLabel("👥  Customer Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20)); title.setForeground(Theme.DARK_BLUE);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Theme.WHITE);
        formPanel.setPreferredSize(new Dimension(280,0));
        formPanel.setBorder(BorderFactory.createMatteBorder(0,0,0,1,new Color(220,228,236)));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        GridBagConstraints g = new GridBagConstraints();
        g.fill=GridBagConstraints.HORIZONTAL; g.insets=new Insets(6,4,6,4);

        txtName  = Theme.makeField(15); txtCnic  = Theme.makeField(15);
        txtPhone = Theme.makeField(15); txtEmail = Theme.makeField(15);
        txtAddr  = Theme.makeField(15); txtCity  = Theme.makeField(15);

        addRow(form,g,0,"Full Name *",txtName);
        addRow(form,g,1,"CNIC *",txtCnic);
        addRow(form,g,2,"Phone",txtPhone);
        addRow(form,g,3,"Email",txtEmail);
        addRow(form,g,4,"City",txtCity);
        addRow(form,g,5,"Address",txtAddr);

        JPanel btnRow = new JPanel(new GridLayout(2,2,6,6));
        btnRow.setBackground(Theme.WHITE);
        JButton btnAdd = Theme.makeActionBtn("➕ Add",    Theme.GREEN);
        JButton btnUpd = Theme.makeActionBtn("✏ Update", Theme.AMBER);
        JButton btnDel = Theme.makeActionBtn("🗑 Delete", Theme.RED);
        JButton btnClr = Theme.makeActionBtn("🔄 Clear",  new Color(120,130,145));
        btnRow.add(btnAdd); btnRow.add(btnUpd); btnRow.add(btnDel); btnRow.add(btnClr);
        g.gridx=0; g.gridy=6; g.gridwidth=2; form.add(btnRow, g);
        formPanel.add(form, BorderLayout.NORTH);

        String[] cols = {"ID","Full Name","CNIC","Phone","Email","City","Address"};
        model = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model); Theme.styleTable(table);
        JScrollPane scroll = new JScrollPane(table); scroll.setBorder(null);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, scroll);
        split.setDividerLocation(280); split.setDividerSize(3); split.setBorder(null);
        add(split, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addCustomer());
        btnUpd.addActionListener(e -> updateCustomer());
        btnDel.addActionListener(e -> deleteCustomer());
        btnClr.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow()>=0) loadSelected();
        });
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String lbl, JComponent f) {
        JLabel l = new JLabel(lbl); l.setFont(new Font("Segoe UI",Font.BOLD,12)); l.setForeground(Theme.MED_BLUE);
        g.gridx=0; g.gridy=row; g.weightx=0.35; g.gridwidth=1; p.add(l,g);
        g.gridx=1; g.weightx=0.65; p.add(f,g);
    }

    private void addCustomer() {
        if (txtName.getText().trim().isEmpty()||txtCnic.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,"Name aur CNIC required!"); return;
        }
        Customer c = new Customer();
        c.setFullName(txtName.getText().trim()); c.setCnic(txtCnic.getText().trim());
        c.setPhone(txtPhone.getText().trim()); c.setEmail(txtEmail.getText().trim());
        c.setAddress(txtAddr.getText().trim()); c.setCity(txtCity.getText().trim());
        if (dao.add(c)) { JOptionPane.showMessageDialog(this,"✅ Customer added!"); clearForm(); refresh(); }
    }

    private void updateCustomer() {
        if (selectedId<0){ JOptionPane.showMessageDialog(this,"Pehle customer select karein!"); return; }
        Customer c = new Customer(); c.setId(selectedId);
        c.setFullName(txtName.getText().trim()); c.setPhone(txtPhone.getText().trim());
        c.setEmail(txtEmail.getText().trim()); c.setAddress(txtAddr.getText().trim());
        c.setCity(txtCity.getText().trim());
        if (dao.update(c)) { JOptionPane.showMessageDialog(this,"✅ Customer updated!"); clearForm(); refresh(); }
    }

    private void deleteCustomer() {
        if (selectedId<0){ JOptionPane.showMessageDialog(this,"Pehle customer select karein!"); return; }
        if (JOptionPane.showConfirmDialog(this,"Delete karna chahte hain?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
            if (dao.delete(selectedId)) { JOptionPane.showMessageDialog(this,"✅ Deleted!"); clearForm(); refresh(); }
    }

    private void clearForm() {
        txtName.setText(""); txtCnic.setText(""); txtPhone.setText("");
        txtEmail.setText(""); txtAddr.setText(""); txtCity.setText("");
        selectedId=-1; table.clearSelection();
    }

    private void loadSelected() {
        int row = table.getSelectedRow();
        selectedId = Integer.parseInt(model.getValueAt(row,0).toString());
        txtName.setText(s(model.getValueAt(row,1))); txtCnic.setText(s(model.getValueAt(row,2)));
        txtPhone.setText(s(model.getValueAt(row,3))); txtEmail.setText(s(model.getValueAt(row,4)));
        txtCity.setText(s(model.getValueAt(row,5))); txtAddr.setText(s(model.getValueAt(row,6)));
    }

    private String s(Object o){ return o!=null?o.toString():""; }

    public void refresh() {
        model.setRowCount(0);
        for (hotel.management.model.Customer c : dao.getAll())
            model.addRow(new Object[]{c.getId(),c.getFullName(),c.getCnic(),c.getPhone(),c.getEmail(),c.getCity(),c.getAddress()});
    }
}
