package hotel.management.dao;

import hotel.management.db.DBConnection;
import hotel.management.model.Room;
import java.sql.*;
import java.util.*;

public class RoomDAO {
    public List<Room> getAll() {
        List<Room> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT * FROM rooms ORDER BY room_number")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    public List<Room> getAvailable() {
        List<Room> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT * FROM rooms WHERE status='Available' ORDER BY room_number")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    public boolean add(Room r) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO rooms(room_number,room_type,price_per_night,status,description,floor,capacity) VALUES(?,?,?,?,?,?,?)")) {
            ps.setString(1,r.getRoomNumber()); ps.setString(2,r.getRoomType());
            ps.setDouble(3,r.getPricePerNight()); ps.setString(4,r.getStatus());
            ps.setString(5,r.getDescription()); ps.setString(6,r.getFloor()); ps.setInt(7,r.getCapacity());
            return ps.executeUpdate()>0;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public boolean update(Room r) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE rooms SET room_type=?,price_per_night=?,status=?,description=?,floor=?,capacity=? WHERE id=?")) {
            ps.setString(1,r.getRoomType()); ps.setDouble(2,r.getPricePerNight());
            ps.setString(3,r.getStatus()); ps.setString(4,r.getDescription());
            ps.setString(5,r.getFloor()); ps.setInt(6,r.getCapacity()); ps.setInt(7,r.getId());
            return ps.executeUpdate()>0;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public boolean delete(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM rooms WHERE id=?")) {
            ps.setInt(1,id); return ps.executeUpdate()>0;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public boolean updateStatus(int id, String status) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE rooms SET status=? WHERE id=?")) {
            ps.setString(1,status); ps.setInt(2,id); return ps.executeUpdate()>0;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public int[] getStats() {
        int[] s = new int[3];
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT status,COUNT(*) n FROM rooms GROUP BY status")) {
            while (rs.next()) {
                String st = rs.getString("status");
                if ("Available".equals(st)) s[0]=rs.getInt("n");
                else if ("Booked".equals(st)) s[1]=rs.getInt("n");
                else s[2]=rs.getInt("n");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return s;
    }
    public int getTotal() {
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM rooms")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    public int getTotalFloors() {
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(DISTINCT floor) FROM rooms WHERE floor IS NOT NULL AND floor != ''")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    public int getMaintenance() {
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM rooms WHERE status='Maintenance'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    private Room map(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setId(rs.getInt("id")); r.setRoomNumber(rs.getString("room_number"));
        r.setRoomType(rs.getString("room_type")); r.setPricePerNight(rs.getDouble("price_per_night"));
        r.setStatus(rs.getString("status")); r.setDescription(rs.getString("description"));
        r.setFloor(rs.getString("floor")); r.setCapacity(rs.getInt("capacity"));
        return r;
    }
}
