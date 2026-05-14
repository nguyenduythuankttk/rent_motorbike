package model;

import java.sql.Date;

public class Rental {
    private int id;
    private int userId;
    private int motorbikeId;
    private Date rentDate;
    private Date returnDate;
    private Date actualReturnDate;
    private long totalPrice;
    private String status;

    // Extra fields from JOIN
    private String username;
    private String fullName;
    private String motorbikeModel;
    private String licensePlate;
    private long pricePerDay;

    public Rental() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getMotorbikeId() { return motorbikeId; }
    public void setMotorbikeId(int motorbikeId) { this.motorbikeId = motorbikeId; }

    public Date getRentDate() { return rentDate; }
    public void setRentDate(Date rentDate) { this.rentDate = rentDate; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    public Date getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(Date actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    public long getTotalPrice() { return totalPrice; }
    public void setTotalPrice(long totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMotorbikeModel() { return motorbikeModel; }
    public void setMotorbikeModel(String motorbikeModel) { this.motorbikeModel = motorbikeModel; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public long getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(long pricePerDay) { this.pricePerDay = pricePerDay; }
}
