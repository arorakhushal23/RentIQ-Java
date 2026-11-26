package com.rentiq.model;

public abstract class Vehicle implements Comparable<Vehicle> {
    private String vehicleId;
    private String brand;
    private String model;
    private String category;
    private String registrationNumber;
    private double pricePerDay;
    private boolean available;
    private double rating;

    public Vehicle() {
        this.available = true;
        this.rating = 5.0;
    }

    public Vehicle(String vehicleId, String brand, String model, String category,
                   String registrationNumber, double pricePerDay, boolean available) {
        this(vehicleId, brand, model, category, registrationNumber, pricePerDay, available, 5.0);
    }

    public Vehicle(String vehicleId, String brand, String model, String category,
                   double pricePerDay, boolean available) {
        this(vehicleId, brand, model, category, "N/A", pricePerDay, available, 5.0);
    }

    public Vehicle(String vehicleId, String brand, String model, String category,
                   String registrationNumber, double pricePerDay, boolean available, double rating) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.registrationNumber = registrationNumber;
        this.pricePerDay = pricePerDay;
        this.available = available;
        this.rating = rating;
    }

    @Override
    public int compareTo(Vehicle other) {
        if (other == null || other.vehicleId == null) {
            return 1;
        }
        if (this.vehicleId == null) {
            return -1;
        }
        return this.vehicleId.compareToIgnoreCase(other.vehicleId);
    }

    public String getName() {
        if (this.brand != null && this.model != null) {
            return this.brand + " " + this.model;
        } else if (this.brand != null) {
            return this.brand;
        } else if (this.model != null) {
            return this.model;
        }
        return "";
    }

    public boolean bookVehicle() {
        if (this.available) {
            this.available = false;
            return true;
        }
        return false;
    }

    public boolean cancelBooking() {
        if (!this.available) {
            this.available = true;
            return true;
        }
        return false;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public abstract double calculateRentalCost(int days);

    public String getVehicleId() {
        return this.vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getBrand() {
        return this.brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public double getPricePerDay() {
        return this.pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public boolean getAvailable() {
        return this.available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getRating() {
        return this.rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void updateAvailability(boolean available) {
        this.available = available;
    }

    public void displayVehicleDetails() {
        String statusText;
        if (this.available) {
            statusText = "Available";
        } else {
            statusText = "Rented / Unavailable";
        }

        System.out.println("----- Vehicle Details -----");
        System.out.println("Vehicle ID          : " + this.vehicleId);
        System.out.println("Brand & Model       : " + getName());
        System.out.println("Category            : " + this.category);
        System.out.println("Registration Number : " + this.registrationNumber);
        System.out.println("Price Per Day       : $" + this.pricePerDay);
        System.out.println("Customer Rating     : " + String.format("%.1f", this.rating) + " / 5.0");
        System.out.println("Availability        : " + statusText);
        System.out.println("---------------------------");
    }
}
