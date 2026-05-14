package model;

import java.sql.Date;

public class Invoice {
    private int id;
    private int rentalId;
    private Date issuedDate;
    private int days;
    private long pricePerDay;
    private long totalPrice;

    // Extra fields from JOIN
    private String fullName;
    private String motorbikeModel;
    private String licensePlate;
    private Date rentDate;

    public Invoice() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRentalId() { return rentalId; }
    public void setRentalId(int rentalId) { this.rentalId = rentalId; }

    public Date getIssuedDate() { return issuedDate; }
    public void setIssuedDate(Date issuedDate) { this.issuedDate = issuedDate; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public long getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(long pricePerDay) { this.pricePerDay = pricePerDay; }

    public long getTotalPrice() { return totalPrice; }
    public void setTotalPrice(long totalPrice) { this.totalPrice = totalPrice; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMotorbikeModel() { return motorbikeModel; }
    public void setMotorbikeModel(String motorbikeModel) { this.motorbikeModel = motorbikeModel; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public Date getRentDate() { return rentDate; }
    public void setRentDate(Date rentDate) { this.rentDate = rentDate; }
}
