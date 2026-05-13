package dao;

import db.DBConnection;
import model.Motorbike;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MotorbikeDAO {

    public List<Motorbike> getAll() {
        List<Motorbike> list = new ArrayList<>();
        String sql = "SELECT * FROM Motorbikes ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapMotorbike(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Motorbike> getAvailable() {
        List<Motorbike> list = new ArrayList<>();
        String sql = "SELECT * FROM Motorbikes WHERE status = 'Sẵn sàng' ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapMotorbike(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Motorbike> searchAvailableByMaxPrice(long maxPrice) {
        List<Motorbike> list = new ArrayList<>();
        String sql = "SELECT * FROM Motorbikes WHERE status = 'Sẵn sàng' AND price_per_day <= ? ORDER BY price_per_day";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, maxPrice);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapMotorbike(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Motorbike> search(String keyword) {
        List<Motorbike> list = new ArrayList<>();
        String sql = "SELECT * FROM Motorbikes WHERE model LIKE ? OR brand LIKE ? OR license_plate LIKE ? ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapMotorbike(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Motorbike getById(int id) {
        String sql = "SELECT * FROM Motorbikes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapMotorbike(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean add(Motorbike m) {
        String sql = "INSERT INTO Motorbikes (license_plate, model, brand, price_per_day, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getLicensePlate());
            ps.setString(2, m.getModel());
            ps.setString(3, m.getBrand());
            ps.setLong(4, m.getPricePerDay());
            ps.setString(5, m.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Motorbike m) {
        String sql = "UPDATE Motorbikes SET license_plate=?, model=?, brand=?, price_per_day=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getLicensePlate());
            ps.setString(2, m.getModel());
            ps.setString(3, m.getBrand());
            ps.setLong(4, m.getPricePerDay());
            ps.setString(5, m.getStatus());
            ps.setInt(6, m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Motorbikes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE Motorbikes SET status = ? WHERE id = ?";
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

    private Motorbike mapMotorbike(ResultSet rs) throws SQLException {
        Motorbike m = new Motorbike();
        m.setId(rs.getInt("id"));
        m.setLicensePlate(rs.getString("license_plate"));
        m.setModel(rs.getString("model"));
        m.setBrand(rs.getString("brand"));
        m.setPricePerDay(rs.getLong("price_per_day"));
        m.setStatus(rs.getString("status"));
        return m;
    }
}
