package hotel.management.ui;

import hotel.management.dao.UserDAO;
import hotel.management.model.User;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class UserMgmtPanel extends JPanel {
    private User currentUser;
    private UserDAO dao = new UserDAO();
    private DefaultTableModel model;
    private JTable table;
    private int selectedId = -1;
    private JTextField txtUser, txtPass, txtName, txtEmail;
    private JComboBox<String> cmbRole;

    public UserMgmtPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Theme.DARK_BLUE);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Theme.WHITE);
        formPanel.setPreferredSize(new Dimension(280, 0));
        formPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 228, 236)));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 4, 6, 4);

        txtUser  = Theme.makeField(15);
        txtPass  = Theme.makeField(15);
        txtName  = Theme.makeField(15);
        txtEmail = Theme.makeField(15);
        cmbRole  = Theme.makeCombo(new String[]{"user", "admin"});

        addRow(form, g, 0, "Username *", txtUser);
        addRow(form, g, 1, "Password *", txtPass);
        addRow(form, g, 2, "Full Name",  txtName);
        addRow(form, g, 3, "Email",      txtEmail);
        addRow(form, g, 4, "Role",       cmbRole);

        JPanel btnRow = new JPanel(new GridLayout(2, 2, 6, 6));
        btnRow.setBackground(Theme.WHITE);
        JButton btnAdd = Theme.makeActionBtn("Add",    Theme.GREEN);
        JButton btnUpd = Theme.makeActionBtn("Update", Theme.AMBER);
        JButton btnDel = Theme.makeActionBtn("Delete", Theme.RED);
        JButton btnClr = Theme.makeActionBtn("Clear",  new Color(120, 130, 145));
        btnRow.add(btnAdd); btnRow.add(btnUpd);
        btnRow.add(btnDel); btnRow.add(btnClr);
        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        form.add(btnRow, g);
        formPanel.add(form, BorderLayout.NORTH);

        String[] cols = {"ID", "Username", "Full Name", "Email", "Role"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        Theme.styleTable(table);

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                if ("admin".equals(v)) l.setForeground(Theme.RED);
                else l.setForeground(Theme.ACCENT);
                l.setFont(l.getFont().deriveFont(Font.BOLD));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, scroll);
        split.setDividerLocation(280);
        split.setDividerSize(3);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addUser());
        btnUpd.addActionListener(e -> updateUser());
        btnDel.addActionListener(e -> deleteUser());
        btnClr.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0)
                loadSelected();
        });
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String lbl, JComponent f) {
        JLabel l = new JLabel(lbl);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Theme.MED_BLUE);
        g.gridx = 0; g.gridy = row; g.weightx = 0.35; g.gridwidth = 1;
        p.add(l, g);
        g.gridx = 1; g.weightx = 0.65;
        p.add(f, g);
    }

    private void addUser() {
        String username = txtUser.getText().trim();
        String password = txtPass.getText().trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username likhein!", "Warning", JOptionPane.WARNING_MESSAGE);
            txtUser.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password likhein!", "Warning", JOptionPane.WARNING_MESSAGE);
            txtPass.requestFocus();
            return;
        }

        // Check if username already exists
        for (User u : dao.getAll()) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(this,
                    "Username '" + username + "' already exist karta hai!\nDosra username likhein.",
                    "Duplicate Username", JOptionPane.ERROR_MESSAGE);
                txtUser.selectAll();
                txtUser.requestFocus();
                return;
            }
        }

        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setFullName(txtName.getText().trim());
        u.setEmail(txtEmail.getText().trim());
        u.setRole(cmbRole.getSelectedItem().toString());

        if (dao.add(u)) {
            JOptionPane.showMessageDialog(this,
                "User add ho gaya!" + "\nUsername: " + username + "\nRole: " + u.getRole(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refresh();
        } else {
            JOptionPane.showMessageDialog(this,
                "User add nahi hua! Dobara try karein.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUser() {
        if (selectedId < 0) {
            JOptionPane.showMessageDialog(this, "Pehle table se user select karein!");
            return;
        }
        User u = new User();
        u.setId(selectedId);
        u.setFullName(txtName.getText().trim());
        u.setEmail(txtEmail.getText().trim());
        u.setRole(cmbRole.getSelectedItem().toString());
        if (dao.update(u)) {
            JOptionPane.showMessageDialog(this, "User update ho gaya!");
            clearForm();
            refresh();
        }
    }

    private void deleteUser() {
        if (selectedId < 0) {
            JOptionPane.showMessageDialog(this, "Pehle table se user select karein!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Is user ko delete karna chahte hain?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(selectedId)) {
                JOptionPane.showMessageDialog(this, "User delete ho gaya!");
                clearForm();
                refresh();
            }
        }
    }

    private void clearForm() {
        txtUser.setText("");
        txtPass.setText("");
        txtName.setText("");
        txtEmail.setText("");
        cmbRole.setSelectedIndex(0);
        selectedId = -1;
        table.clearSelection();
    }

    private void loadSelected() {
        int row = table.getSelectedRow();
        selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
        txtUser.setText(s(model.getValueAt(row, 1)));
        txtName.setText(s(model.getValueAt(row, 2)));
        txtEmail.setText(s(model.getValueAt(row, 3)));
        cmbRole.setSelectedItem(s(model.getValueAt(row, 4)));
    }

    private String s(Object o) { return o != null ? o.toString() : ""; }

    public void refresh() {
        model.setRowCount(0);
        for (User u : dao.getAll())
            model.addRow(new Object[]{
                u.getId(), u.getUsername(), u.getFullName(), u.getEmail(), u.getRole()
            });
    }
}