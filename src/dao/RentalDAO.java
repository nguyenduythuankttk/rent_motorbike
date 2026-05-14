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

    /**
     * Tạo đơn thuê và cập nhật trạng thái xe trong một transaction.
     * WHERE status='Sẵn sàng' đảm bảo xe chưa bị đặt bởi user khác (race condition).
     * Trả về false nếu xe không còn sẵn sàng hoặc có lỗi.
     */
    public boolean addAndLockBike(Rental r) {
        String sqlRental = "INSERT INTO Rentals (user_id, motorbike_id, rent_date, return_date, total_price, status) VALUES (?, ?, ?, ?, ?, 'Chờ xử lý')";
        String sqlBike   = "UPDATE Motorbikes SET status = 'Đang thuê' WHERE id = ? AND status = 'Sẵn sàng'";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlRental);
                 PreparedStatement ps2 = conn.prepareStatement(sqlBike)) {

                ps1.setInt(1, r.getUserId());
                ps1.setInt(2, r.getMotorbikeId());
                ps1.setDate(3, r.getRentDate());
                ps1.setDate(4, r.getReturnDate());
                ps1.setLong(5, r.getTotalPrice());
                ps1.executeUpdate();

                ps2.setInt(1, r.getMotorbikeId());
                int bikeUpdated = ps2.executeUpdate();
                if (bikeUpdated == 0) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
    }

    public List<Rental> getAll() {
        List<Rental> list = new ArrayList<>();
        String sql = "SELECT r.*, u.username, u.full_name, m.model, m.license_plate, m.price_per_day " +
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
        String sql = "SELECT r.*, u.username, u.full_name, m.model, m.license_plate, m.price_per_day " +
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

    public boolean confirmReturn(int rentalId, int motorbikeId, java.sql.Date actualReturnDate, long newTotalPrice) {
        String sqlRental = "UPDATE Rentals SET status = 'Đã thanh toán', actual_return_date = ?, total_price = ? WHERE id = ?";
        String sqlBike   = "UPDATE Motorbikes SET status = 'Sẵn sàng' WHERE id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlRental);
                 PreparedStatement ps2 = conn.prepareStatement(sqlBike)) {
                ps1.setDate(1, actualReturnDate);
                ps1.setLong(2, newTotalPrice);
                ps1.setInt(3, rentalId);
                ps1.executeUpdate();
                ps2.setInt(1, motorbikeId);
                ps2.executeUpdate();
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
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
        r.setActualReturnDate(rs.getDate("actual_return_date"));
        r.setTotalPrice(rs.getLong("total_price"));
        r.setStatus(rs.getString("status"));
        r.setUsername(rs.getString("username"));
        r.setFullName(rs.getString("full_name"));
        r.setMotorbikeModel(rs.getString("model"));
        r.setLicensePlate(rs.getString("license_plate"));
        r.setPricePerDay(rs.getLong("price_per_day"));
        return r;
    }
}
