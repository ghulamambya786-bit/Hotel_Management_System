package hotel.management.ui;

import hotel.management.dao.*;
import hotel.management.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class BookingPanel extends JPanel {
    private User currentUser;
    private BookingDAO bookingDAO = new BookingDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private DefaultTableModel model;
    private JTable table;
    private int selectedId = -1, selectedRoomId = -1;

    // Customer fields
    private JTextField txtCustName, txtCustCnic, txtCustPhone, txtCustCity;

    // Room fields
    private JTextField txtRoomNo, txtRoomPrice;
    private JComboBox<String> cmbRoomType;

    // Booking fields
    private JSpinner spnCheckIn, spnCheckOut;
    private JLabel lblTotal;
    private JTextArea txtNotes;
    private JCheckBox chkAutoCheckIn, chkAutoCheckOut;

    public BookingPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        buildUI();
    }

    private void buildUI() {
        // Initialize checkboxes first to avoid NullPointerException
        chkAutoCheckIn  = new JCheckBox("  Auto Check-In on Booking");
        chkAutoCheckOut = new JCheckBox("  Auto Check-Out on Booking");

        // ── HEADER ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        JLabel title = new JLabel("📋  Booking System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Theme.DARK_BLUE);
        JLabel sub = new JLabel("New customer aur room directly yahan add karein");
        sub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        sub.setForeground(new Color(130, 140, 155));
        JPanel tt = new JPanel(new GridLayout(2, 1));
        tt.setBackground(Theme.WHITE);
        tt.add(title); tt.add(sub);
        header.add(tt, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ── LEFT FORM PANEL (Scrollable) ──
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Theme.WHITE);
        formPanel.setPreferredSize(new Dimension(320, 0));
        formPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 228, 236)));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Theme.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // ── SECTION 1: Customer ──
        form.add(sectionLabel("👤  Customer Details", Theme.ACCENT));

        txtCustName  = makeField(); txtCustCnic  = makeField();
        txtCustPhone = makeField(); txtCustCity  = makeField();

        form.add(fieldRow("Full Name *", txtCustName));
        form.add(fieldRow("CNIC *", txtCustCnic));
        form.add(fieldRow("Phone", txtCustPhone));
        form.add(fieldRow("City", txtCustCity));

        JButton btnExistCust = new JButton("Or Select Existing Customer");
        btnExistCust.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnExistCust.setBackground(new Color(240, 248, 255));
        btnExistCust.setForeground(Theme.ACCENT);
        btnExistCust.setBorderPainted(true);
        btnExistCust.setFocusPainted(false);
        btnExistCust.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExistCust.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExistCust.addActionListener(e -> selectExistingCustomer());
        form.add(Box.createVerticalStrut(4));
        form.add(btnExistCust);
        form.add(Box.createVerticalStrut(10));

        // ── SECTION 2: Room ──
        form.add(sectionLabel("🛏  Room Details", Theme.GREEN));

        txtRoomNo    = makeField(); txtRoomPrice = makeField();
        cmbRoomType  = new JComboBox<>(new String[]{"Single", "Double", "Suite", "Deluxe"});
        cmbRoomType.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        form.add(fieldRow("Room Number *", txtRoomNo));
        form.add(fieldRow("Room Type", cmbRoomType));
        form.add(fieldRow("Price/Night *", txtRoomPrice));

        JButton btnExistRoom = new JButton("Or Select Available Room");
        btnExistRoom.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnExistRoom.setBackground(new Color(240, 255, 245));
        btnExistRoom.setForeground(Theme.GREEN);
        btnExistRoom.setBorderPainted(true);
        btnExistRoom.setFocusPainted(false);
        btnExistRoom.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExistRoom.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExistRoom.addActionListener(e -> selectExistingRoom());
        form.add(Box.createVerticalStrut(4));
        form.add(btnExistRoom);
        form.add(Box.createVerticalStrut(10));

        // ── SECTION 3: Booking Dates ──
        form.add(sectionLabel("📅  Booking Dates", Theme.PURPLE));

        SpinnerDateModel m1 = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        spnCheckIn = new JSpinner(m1);
        spnCheckIn.setEditor(new JSpinner.DateEditor(spnCheckIn, "dd-MM-yyyy"));

        Calendar nx = Calendar.getInstance();
        nx.add(Calendar.DAY_OF_MONTH, 1);
        SpinnerDateModel m2 = new SpinnerDateModel(nx.getTime(), null, null, Calendar.DAY_OF_MONTH);
        spnCheckOut = new JSpinner(m2);
        spnCheckOut.setEditor(new JSpinner.DateEditor(spnCheckOut, "dd-MM-yyyy"));

        form.add(fieldRow("Check-In Date", spnCheckIn));
        form.add(fieldRow("Check-Out Date", spnCheckOut));

        // Auto check-in checkbox
        chkAutoCheckIn = new JCheckBox("  Auto Check-In on Booking");
        chkAutoCheckIn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkAutoCheckIn.setForeground(Theme.TEAL);
        chkAutoCheckIn.setBackground(Theme.WHITE);
        chkAutoCheckIn.setSelected(false);
        chkAutoCheckOut.setSelected(false);

        chkAutoCheckOut = new JCheckBox("  Auto Check-Out on Booking");
        chkAutoCheckOut.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkAutoCheckOut.setForeground(Theme.RED);
        chkAutoCheckOut.setBackground(Theme.WHITE);
        chkAutoCheckOut.setSelected(false);

        // When AutoCheckOut selected, AutoCheckIn also selected
        chkAutoCheckOut.addActionListener(e -> {
            if (chkAutoCheckOut.isSelected()) chkAutoCheckIn.setSelected(true);
        });

        form.add(Box.createVerticalStrut(4));
        form.add(chkAutoCheckIn);
        form.add(Box.createVerticalStrut(4));
        form.add(chkAutoCheckOut);
        form.add(Box.createVerticalStrut(6));

        txtNotes = new JTextArea(2, 15);
        txtNotes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNotes.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220)));
        form.add(fieldRow("Notes", txtNotes));
        form.add(Box.createVerticalStrut(8));

        // Calculate Amount
        JButton btnCalc = new JButton("🧮  Calculate Amount");
        btnCalc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCalc.setBackground(Theme.ACCENT);
        btnCalc.setForeground(Color.WHITE);
        btnCalc.setBorderPainted(false);
        btnCalc.setFocusPainted(false);
        btnCalc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnCalc.addActionListener(e -> calcTotal());
        form.add(btnCalc);
        form.add(Box.createVerticalStrut(6));

        lblTotal = new JLabel("Rs. 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(Theme.GREEN);
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(lblTotal);
        form.add(Box.createVerticalStrut(10));

        // Action buttons
        JPanel btnRow = new JPanel(new GridLayout(2, 2, 6, 6));
        btnRow.setBackground(Theme.WHITE);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));

        JButton btnBook  = Theme.makeActionBtn("✅ Book Now",   Theme.GREEN);
        JButton btnUpd   = Theme.makeActionBtn("✏ Update",     Theme.AMBER);
        JButton btnDel   = Theme.makeActionBtn("🗑 Delete",     Theme.RED);
        JButton btnClr   = Theme.makeActionBtn("🔄 Clear",      new Color(120, 130, 145));
        btnRow.add(btnBook); btnRow.add(btnUpd);
        btnRow.add(btnDel);  btnRow.add(btnClr);
        form.add(btnRow);

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(10);
        formPanel.add(formScroll, BorderLayout.CENTER);

        // ── RIGHT TABLE ──
        String[] cols = {"ID", "Code", "Customer", "Room", "Type", "Check-In", "Check-Out", "Amount", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setMaxWidth(90);

        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                String val = v != null ? v.toString() : "";
                if ("Confirmed".equals(val))    l.setForeground(Theme.ACCENT);
                else if ("Checked-In".equals(val))  l.setForeground(Theme.GREEN);
                else if ("Checked-Out".equals(val)) l.setForeground(Theme.ORANGE);
                else if ("Cancelled".equals(val))   l.setForeground(Theme.RED);
                else l.setForeground(Theme.AMBER);
                l.setFont(l.getFont().deriveFont(Font.BOLD));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, scroll);
        split.setDividerLocation(320);
        split.setDividerSize(3);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        // ── EVENTS ──
        btnBook.addActionListener(e -> addBooking());
        btnUpd.addActionListener(e -> updateStatus());
        btnDel.addActionListener(e -> deleteBooking());
        btnClr.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
                String roomNo = model.getValueAt(row, 3).toString();
                for (Room r : roomDAO.getAll())
                    if (r.getRoomNumber().equals(roomNo)) { selectedRoomId = r.getId(); break; }
            }
        });
    }

    // ── UI Helpers ──
    private JTextField makeField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 220)),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return f;
    }

    private JPanel sectionLabel(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, color),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(color);
        p.add(l);
        return p;
    }

    private JPanel fieldRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 2));
        row.setBackground(Theme.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Theme.MED_BLUE);
        row.add(l, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    // ── Select Existing Customer Dialog ──
    private void selectExistingCustomer() {
        java.util.List<Customer> list = customerDAO.getAll();
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Koi customer exist nahi karta — naya fill karein!");
            return;
        }
        String[] names = list.stream()
            .map(c -> c.getFullName() + " | " + c.getCnic() + " | " + c.getPhone())
            .toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this,
            "Customer select karein:", "Existing Customer",
            JOptionPane.PLAIN_MESSAGE, null, names, names[0]);
        if (selected != null) {
            int idx = Arrays.asList(names).indexOf(selected);
            Customer c = list.get(idx);
            txtCustName.setText(c.getFullName());
            txtCustCnic.setText(c.getCnic());
            txtCustPhone.setText(c.getPhone() != null ? c.getPhone() : "");
            txtCustCity.setText(c.getCity() != null ? c.getCity() : "");
        }
    }

    // ── Select Existing Room Dialog ──
    private void selectExistingRoom() {
        java.util.List<Room> list = roomDAO.getAvailable();
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Koi available room nahi — naya room number fill karein!");
            return;
        }
        String[] rooms = list.stream()
            .map(r -> r.getRoomNumber() + " | " + r.getRoomType() + " | Rs." + r.getPricePerNight())
            .toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this,
            "Available room select karein:", "Available Rooms",
            JOptionPane.PLAIN_MESSAGE, null, rooms, rooms[0]);
        if (selected != null) {
            int idx = Arrays.asList(rooms).indexOf(selected);
            Room r = list.get(idx);
            txtRoomNo.setText(r.getRoomNumber());
            cmbRoomType.setSelectedItem(r.getRoomType());
            txtRoomPrice.setText(String.valueOf(r.getPricePerNight()));
        }
    }

    // ── Calculate Total ──
    private void calcTotal() {
        try {
            String priceStr = txtRoomPrice.getText().trim();
            if (priceStr.isEmpty()) { lblTotal.setText("Price likhein!"); return; }
            double price = Double.parseDouble(priceStr);
            Date ci = (Date) spnCheckIn.getValue();
            Date co = (Date) spnCheckOut.getValue();
            long days = (co.getTime() - ci.getTime()) / (1000 * 60 * 60 * 24);
            if (days <= 0) { lblTotal.setText("Dates invalid!"); return; }
            lblTotal.setText(String.format("Rs. %,.0f  (%d nights x Rs.%.0f)", days * price, days, price));
        } catch (NumberFormatException ex) {
            lblTotal.setText("Price sahi likhein!");
        }
    }

    // ── Add Booking ──
    private void addBooking() {
        // Validate customer
        String custName = txtCustName.getText().trim();
        String custCnic = txtCustCnic.getText().trim();
        if (custName.isEmpty() || custCnic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer ka Name aur CNIC required hai!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate room
        String roomNo    = txtRoomNo.getText().trim();
        String roomPrice = txtRoomPrice.getText().trim();
        if (roomNo.isEmpty() || roomPrice.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room Number aur Price required hai!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate dates
        Date ci = (Date) spnCheckIn.getValue();
        Date co = (Date) spnCheckOut.getValue();
        long days = (co.getTime() - ci.getTime()) / (1000 * 60 * 60 * 24);
        if (days <= 0) {
            JOptionPane.showMessageDialog(this, "Check-out date, Check-in se baad honi chahiye!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(roomPrice);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Room price sahi likhein!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // ── Step 1: Customer find or create ──
            Customer customer = null;
            for (Customer c : customerDAO.getAll()) {
                if (c.getCnic().equalsIgnoreCase(custCnic)) {
                    customer = c;
                    break;
                }
            }
            if (customer == null) {
                // New customer add karo
                Customer newCust = new Customer();
                newCust.setFullName(custName);
                newCust.setCnic(custCnic);
                newCust.setPhone(txtCustPhone.getText().trim());
                newCust.setCity(txtCustCity.getText().trim());
                customerDAO.add(newCust);
                // Get the newly added customer
                for (Customer c : customerDAO.getAll()) {
                    if (c.getCnic().equalsIgnoreCase(custCnic)) {
                        customer = c;
                        break;
                    }
                }
            }

            // ── Step 2: Room find or create ──
            Room room = null;
            for (Room r : roomDAO.getAll()) {
                if (r.getRoomNumber().equalsIgnoreCase(roomNo)) {
                    room = r;
                    break;
                }
            }
            if (room == null) {
                // New room add karo
                Room newRoom = new Room();
                newRoom.setRoomNumber(roomNo);
                newRoom.setRoomType(cmbRoomType.getSelectedItem().toString());
                newRoom.setPricePerNight(price);
                newRoom.setStatus("Available");
                newRoom.setFloor("");
                newRoom.setCapacity(1);
                roomDAO.add(newRoom);
                // Get newly added room
                for (Room r : roomDAO.getAll()) {
                    if (r.getRoomNumber().equalsIgnoreCase(roomNo)) {
                        room = r;
                        break;
                    }
                }
            }

            if (customer == null || room == null) {
                JOptionPane.showMessageDialog(this, "Customer ya Room add karne mein error!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ── Step 3: Create Booking ──
            double totalAmount = days * price;
            Booking b = new Booking();
            b.setCustomerId(customer.getId());
            b.setRoomId(room.getId());
            b.setCheckIn(ci);
            b.setCheckOut(co);
            b.setTotalAmount(totalAmount);
            b.setNotes(txtNotes.getText().trim());

            if (bookingDAO.add(b, currentUser.getId())) {
                // ── Step 4: Auto Check-In / Check-Out ──
                String status = "Confirmed";
                SimpleDateFormat tf = new SimpleDateFormat("hh:mm a");
                String time = tf.format(new Date());

                if (chkAutoCheckIn.isSelected() || chkAutoCheckOut.isSelected()) {
                    // Find the booking we just added
                    for (Booking bk : bookingDAO.getAll()) {
                        if (bk.getCustomerId() == customer.getId() && bk.getRoomId() == room.getId()
                            && "Confirmed".equals(bk.getStatus())) {
                            // Auto Check-In
                            bookingDAO.checkIn(bk.getId(), room.getId(), time);
                            status = "Checked-In";

                            // Auto Check-Out bhi karo
                            if (chkAutoCheckOut.isSelected()) {
                                bookingDAO.checkOut(bk.getId(), room.getId(), time);
                                status = "Checked-Out";
                            }
                            break;
                        }
                    }
                }

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String msg = "Booking Successful!" + "\n\n"
                    + "Customer: " + customer.getFullName() + "\n"
                    + "Room: " + room.getRoomNumber() + " (" + room.getRoomType() + ")\n"
                    + "Check-In: " + sdf.format(ci) + "\n"
                    + "Check-Out: " + sdf.format(co) + "\n"
                    + "Nights: " + days + "\n"
                    + "Total: Rs. " + String.format("%,.0f", totalAmount) + "\n"
                    + "Status: " + status;

                JOptionPane.showMessageDialog(this, msg, "Booking Done!", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refresh();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ── Update Status ──
    private void updateStatus() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Table se booking select karein!"); return; }
        String[] opts = {"Confirmed", "Checked-In", "Checked-Out", "Cancelled", "Pending"};
        String s = (String) JOptionPane.showInputDialog(this, "Naya status:", "Update Status",
            JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
        if (s != null && bookingDAO.updateStatus(selectedId, s, selectedRoomId)) {
            JOptionPane.showMessageDialog(this, "Status updated!");
            refresh();
        }
    }

    // ── Delete Booking ──
    private void deleteBooking() {
        if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Table se booking select karein!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete karein?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            if (bookingDAO.delete(selectedId)) {
                JOptionPane.showMessageDialog(this, "Deleted!");
                clearForm();
                refresh();
            }
    }

    // ── Clear Form ──
    private void clearForm() {
        txtCustName.setText("");  txtCustCnic.setText("");
        txtCustPhone.setText(""); txtCustCity.setText("");
        txtRoomNo.setText("");    txtRoomPrice.setText("");
        cmbRoomType.setSelectedIndex(0);
        txtNotes.setText("");
        lblTotal.setText("Rs. 0.00");
        chkAutoCheckIn.setSelected(false);
        chkAutoCheckOut.setSelected(false);
        Calendar today = Calendar.getInstance();
        spnCheckIn.setValue(today.getTime());
        today.add(Calendar.DAY_OF_MONTH, 1);
        spnCheckOut.setValue(today.getTime());
        selectedId = -1;
        selectedRoomId = -1;
        table.clearSelection();
    }

    // ── Refresh Table ──
    public void refresh() {
        model.setRowCount(0);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        for (Booking b : bookingDAO.getAll()) {
            model.addRow(new Object[]{
                b.getId(), b.getBookingCode(), b.getCustomerName(),
                b.getRoomNumber(), b.getRoomType(),
                df.format(b.getCheckIn()), df.format(b.getCheckOut()),
                String.format("%,.0f", b.getTotalAmount()), b.getStatus()
            });
        }
    }
}