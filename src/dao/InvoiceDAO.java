package dao;

import db.DBConnection;
import model.Invoice;

import java.sql.*;

public class InvoiceDAO {

    public boolean save(int rentalId, Date issuedDate, int days, long pricePerDay, long totalPrice) {
        String sql = "INSERT INTO Invoices (rental_id, issued_date, days, price_per_day, total_price) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE issued_date=VALUES(issued_date), days=VALUES(days), " +
                     "price_per_day=VALUES(price_per_day), total_price=VALUES(total_price)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rentalId);
            ps.setDate(2, issuedDate);
            ps.setInt(3, days);
            ps.setLong(4, pricePerDay);
            ps.setLong(5, totalPrice);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("InvoiceDAO.save: " + e.getMessage());
            return false;
        }
    }

    public Invoice getByRentalId(int rentalId) {
        String sql = "SELECT i.*, u.full_name, m.model AS motorbike_model, m.license_plate, r.rent_date " +
                     "FROM Invoices i " +
                     "JOIN Rentals r ON i.rental_id = r.id " +
                     "JOIN Users u ON r.user_id = u.id " +
                     "JOIN Motorbikes m ON r.motorbike_id = m.id " +
                     "WHERE i.rental_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rentalId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Invoice inv = new Invoice();
                inv.setId(rs.getInt("id"));
                inv.setRentalId(rs.getInt("rental_id"));
                inv.setIssuedDate(rs.getDate("issued_date"));
                inv.setDays(rs.getInt("days"));
                inv.setPricePerDay(rs.getLong("price_per_day"));
                inv.setTotalPrice(rs.getLong("total_price"));
                inv.setFullName(rs.getString("full_name"));
                inv.setMotorbikeModel(rs.getString("motorbike_model"));
                inv.setLicensePlate(rs.getString("license_plate"));
                inv.setRentDate(rs.getDate("rent_date"));
                return inv;
            }
        } catch (SQLException e) {
            System.err.println("InvoiceDAO.getByRentalId: " + e.getMessage());
        }
        return null;
    }

    public boolean existsByRentalId(int rentalId) {
        String sql = "SELECT 1 FROM Invoices WHERE rental_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rentalId);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }
}
