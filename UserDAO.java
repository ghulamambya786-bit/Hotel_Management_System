package hotel.management.dao;

import hotel.management.db.DBConnection;
import hotel.management.model.User;
import java.sql.*;
import java.util.*;

public class UserDAO {
    public User login(String username, String password) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {
            ps.setString(1, username); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT * FROM users ORDER BY id")) {
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    public boolean add(User u) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO users(username,password,role,full_name,email) VALUES(?,?,?,?,?)")) {
            ps.setString(1,u.getUsername()); ps.setString(2,u.getPassword());
            ps.setString(3,u.getRole()); ps.setString(4,u.getFullName()); ps.setString(5,u.getEmail());
            return ps.executeUpdate()>0;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public boolean update(User u) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE users SET full_name=?,email=?,role=? WHERE id=?")) {
            ps.setString(1,u.getFullName()); ps.setString(2,u.getEmail());
            ps.setString(3,u.getRole()); ps.setInt(4,u.getId());
            return ps.executeUpdate()>0;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public boolean delete(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setInt(1,id); return ps.executeUpdate()>0;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id")); u.setUsername(rs.getString("username"));
        u.setRole(rs.getString("role")); u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email")); return u;
    }
}
