package dao;

import db.DBConnection;
import model.Rental;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    public boolean add(Rental r) {
        String sql = "INSERT INTO Rentals (user_id, motorbike_id, rent_date, return_date, total_price, status) VALUES (?, ?, ?, ?, ?, 'Chờ xử lý')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getUserId());
            ps.setInt(2, r.getMotorbikeId());
            ps.setDate(3, r.getRentDate());
            ps.setDate(4, r.getReturnDate());
            ps.setLong(5, r.getTotalPrice());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Rental> getAll() {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT r.*, u.username, u.full_name, m.model, m.license_plate " +
                     "FROM Rentals r " +
                     "JOIN Users u ON r.user_id = u.id " +
                     "JOIN Motorbikes m ON r.motorbike_id = m.id " +
                     "ORDER BY r.id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRental(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Rental> getByUser(int userId) {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT r.*, u.username, u.full_name, m.model, m.license_plate " +
                     "FROM Rentals r " +
                     "JOIN Users u ON r.user_id = u.id " +
                     "JOIN Motorbikes m ON r.motorbike_id = m.id " +
                     "WHERE r.user_id = ? ORDER BY r.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRental(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE Rentals SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public long getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_price), 0) FROM Rentals WHERE status = 'Đã thanh toán'";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countAll() {
        return countBySQL("SELECT COUNT(*) FROM Rentals");
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Rentals WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countBySQL(String sql) {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Rental mapRental(ResultSet rs) throws SQLException {
        Rental r = new Rental();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setMotorbikeId(rs.getInt("motorbike_id"));
        r.setRentDate(rs.getDate("rent_date"));
        r.setReturnDate(rs.getDate("return_date"));
        r.setTotalPrice(rs.getLong("total_price"));
        r.setStatus(rs.getString("status"));
        r.setUsername(rs.getString("username"));
        r.setFullName(rs.getString("full_name"));
        r.setMotorbikeModel(rs.getString("model"));
        r.setLicensePlate(rs.getString("license_plate"));
        return r;
    }
}
