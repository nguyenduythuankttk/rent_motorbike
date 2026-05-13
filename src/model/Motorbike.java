package model;

public class Motorbike {
    private int id;
    private String licensePlate;
    private String model;
    private String brand;
    private long pricePerDay;
    private String status;

    public Motorbike() {}

    public Motorbike(int id, String licensePlate, String model, String brand, long pricePerDay, String status) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.model = model;
        this.brand = brand;
        this.pricePerDay = pricePerDay;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public long getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(long pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return brand + " " + model + " (" + licensePlate + ")"; }
}
