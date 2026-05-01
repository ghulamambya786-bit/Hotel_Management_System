package hotel.management.dao;

import hotel.management.db.DBConnection;
import hotel.management.model.Booking;
import java.sql.*;
import java.util.*;

public class BookingDAO {
    public List<Booking> getAll() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*,c.full_name cname,r.room_number rnum,r.room_type rtype " +
                     "FROM bookings b JOIN customers c ON b.customer_id=c.id JOIN rooms r ON b.room_id=r.id ORDER BY b.created_at DESC";
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    public List<Booking> getTodayCheckIns() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*,c.full_name cname,r.room_number rnum,r.room_type rtype " +
                     "FROM bookings b JOIN customers c ON b.customer_id=c.id JOIN rooms r ON b.room_id=r.id " +
                     "WHERE b.check_in=CURDATE() AND b.status IN('Confirmed','Checked-In') ORDER BY b.check_in_time";
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    public List<Booking> getTodayCheckOuts() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*,c.full_name cname,r.room_number rnum,r.room_type rtype " +
                     "FROM bookings b JOIN customers c ON b.customer_id=c.id JOIN rooms r ON b.room_id=r.id " +
                     "WHERE b.status='Checked-In' ORDER BY b.check_out ASC";
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    public boolean add(Booking b, int userId) {
        String code = "BKG" + String.format("%03d", (int)(Math.random()*900+100));
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                "INSERT INTO bookings(booking_code,customer_id,room_id,check_in,check_out,total_amount,status,booked_by,notes) VALUES(?,?,?,?,?,?,'Confirmed',?,?)")) {
            ps.setString(1,code); ps.setInt(2,b.getCustomerId()); ps.setInt(3,b.getRoomId());
            ps.setDate(4,new java.sql.Date(b.getCheckIn().getTime()));
            ps.setDate(5,new java.sql.Date(b.getCheckOut().getTime()));
            ps.setDouble(6,b.getTotalAmount()); ps.setInt(7,userId); ps.setString(8,b.getNotes());
            boolean ok = ps.executeUpdate()>0;
            if (ok) updateRoomStatus(c, b.getRoomId(), "Booked");
            return ok;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public boolean checkIn(int bookingId, int roomId, String time) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE bookings SET status='Checked-In',check_in_time=? WHERE id=?")) {
            ps.setString(1,time); ps.setInt(2,bookingId);
            boolean ok = ps.executeUpdate()>0;
            if (ok) updateRoomStatus(c, roomId, "Booked");
            return ok;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public boolean checkOut(int bookingId, int roomId, String time) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE bookings SET status='Checked-Out',check_out_time=? WHERE id=?")) {
            ps.setString(1,time); ps.setInt(2,bookingId);
            boolean ok = ps.executeUpdate()>0;
            if (ok) updateRoomStatus(c, roomId, "Available");
            return ok;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public boolean updateStatus(int id, String status, int roomId) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE bookings SET status=? WHERE id=?")) {
            ps.setString(1,status); ps.setInt(2,id);
            boolean ok = ps.executeUpdate()>0;
            if (ok && ("Checked-Out".equals(status)||"Cancelled".equals(status)))
                updateRoomStatus(c, roomId, "Available");
            return ok;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public boolean delete(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM bookings WHERE id=?")) {
            ps.setInt(1,id); return ps.executeUpdate()>0;
        } catch (SQLException e) { e.printStackTrace(); } return false;
    }
    public int getTotal() {
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM bookings WHERE status!='Cancelled'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    public int getTodayBookings() {
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM bookings WHERE DATE(created_at)=CURDATE()")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    public int getCheckedIn() {
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM bookings WHERE status='Checked-In'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    public int getCheckedOutToday() {
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM bookings WHERE status='Checked-Out' AND DATE(created_at)=CURDATE()")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    public double getTodayRevenue() {
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT IFNULL(SUM(total_amount),0) FROM bookings WHERE DATE(created_at)=CURDATE() AND status!='Cancelled'")) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    public double getMonthRevenue() {
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery("SELECT IFNULL(SUM(total_amount),0) FROM bookings WHERE MONTH(created_at)=MONTH(NOW()) AND YEAR(created_at)=YEAR(NOW()) AND status!='Cancelled'")) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    private void updateRoomStatus(Connection c, int roomId, String status) throws SQLException {
        PreparedStatement ps = c.prepareStatement("UPDATE rooms SET status=? WHERE id=?");
        ps.setString(1,status); ps.setInt(2,roomId); ps.executeUpdate();
    }
    private Booking map(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id")); b.setCustomerId(rs.getInt("customer_id")); b.setRoomId(rs.getInt("room_id"));
        b.setBookingCode(rs.getString("booking_code")); b.setCustomerName(rs.getString("cname"));
        b.setRoomNumber(rs.getString("rnum")); b.setRoomType(rs.getString("rtype"));
        b.setCheckIn(rs.getDate("check_in")); b.setCheckOut(rs.getDate("check_out"));
        b.setCheckInTime(rs.getString("check_in_time")); b.setCheckOutTime(rs.getString("check_out_time"));
        b.setTotalAmount(rs.getDouble("total_amount")); b.setStatus(rs.getString("status"));
        b.setNotes(rs.getString("notes")); return b;
    }
}
