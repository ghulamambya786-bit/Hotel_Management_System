package hotel.management.dao;

import hotel.management.db.DBConnection;
import hotel.management.model.Customer;
import java.sql.*;
import java.util.*;

public class CustomerDAO {
    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT * FROM customers ORDER BY full_name")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    public boolean add(Customer cu) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO customers(full_name,cnic,phone,email,address,city) VALUES(?,?,?,?,?,?)")) {
            ps.setString(1,cu.getFullName()); ps.setString(2,cu.getCnic());
            ps.setString(3,cu.getPhone()); ps.setString(4,cu.getEmail());
            ps.setString(5,cu.getAddress()); ps.setString(6,cu.getCity());
            return ps.executeUpdate()>0;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public boolean update(Customer cu) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE customers SET full_name=?,phone=?,email=?,address=?,city=? WHERE id=?")) {
            ps.setString(1,cu.getFullName()); ps.setString(2,cu.getPhone());
            ps.setString(3,cu.getEmail()); ps.setString(4,cu.getAddress());
            ps.setString(5,cu.getCity()); ps.setInt(6,cu.getId());
            return ps.executeUpdate()>0;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public boolean delete(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM customers WHERE id=?")) {
            ps.setInt(1,id); return ps.executeUpdate()>0;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public int getTotal() {
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM customers")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    private Customer map(ResultSet rs) throws SQLException {
        Customer cu = new Customer();
        cu.setId(rs.getInt("id")); cu.setFullName(rs.getString("full_name"));
        cu.setCnic(rs.getString("cnic")); cu.setPhone(rs.getString("phone"));
        cu.setEmail(rs.getString("email")); cu.setAddress(rs.getString("address"));
        cu.setCity(rs.getString("city")); return cu;
    }
}
