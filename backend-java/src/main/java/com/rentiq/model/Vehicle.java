package com.rentiq.model;

/**
 * Abstract class representing a generic vehicle in the RentIQ system.
 * Implements Bookable to provide concrete booking lifecycle management,
 * and implements Comparable<Vehicle> for natural ordering by vehicle ID.
 * Supports rating and custom Comparators for multi-criteria sorting.
 */
public abstract class Vehicle implements Bookable, Comparable<Vehicle> {
    private String vehicleId;
    private String brand;
    private String model;
    private String category;
    private String registrationNumber;
    private double pricePerDay;
    private boolean available;
    private double rating; // Rating score (e.g. 1.0 to 5.0)

    // Default constructor
    public Vehicle() {
        this.available = true;
        this.rating = 5.0;
    }

    // Parameterized constructor with default rating
    public Vehicle(String vehicleId, String brand, String model, String category, String registrationNumber, double pricePerDay, boolean available) {
        this(vehicleId, brand, model, category, registrationNumber, pricePerDay, available, 5.0);
    }

    // Overloaded constructor without registrationNumber
    public Vehicle(String vehicleId, String brand, String model, String category, double pricePerDay, boolean available) {
        this(vehicleId, brand, model, category, "N/A", pricePerDay, available, 5.0);
    }

    // Full Parameterized constructor with rating
    public Vehicle(String vehicleId, String brand, String model, String category, String registrationNumber, double pricePerDay, boolean available, double rating) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.registrationNumber = registrationNumber;
        this.pricePerDay = pricePerDay;
        this.available = available;
        this.rating = rating;
    }

    // Comparable<Vehicle> natural ordering by vehicle ID (case-insensitive)
    @Override
    public int compareTo(Vehicle other) {
        if (other == null) {
            return 1;
        }
        if (this.vehicleId == null && other.vehicleId == null) {
            return 0;
        }
        if (this.vehicleId == null) {
            return -1;
        }
        if (other.vehicleId == null) {
            return 1;
        }
        return this.vehicleId.compareToIgnoreCase(other.vehicleId);
    }

    /**
     * Returns combined vehicle name (Brand + Model).
     */
    public String getName() {
        return (this.brand != null ? this.brand : "") + " " + (this.model != null ? this.model : "");
    }

    // Bookable interface implementation
    @Override
    public boolean bookVehicle() {
        if (this.available) {
            this.available = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelBooking() {
        if (!this.available) {
            this.available = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean isAvailable() {
        return this.available;
    }

    /**
     * Abstract method for calculating rental cost based on rental duration.
     * Each concrete vehicle subclass implements its category-specific pricing logic.
     * @param days Number of rental days.
     * @return Calculated total rental cost.
     */
    public abstract double calculateRentalCost(int days);

    // Getters and Setters
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

    // Method to update availability
    public void updateAvailability(boolean available) {
        this.available = available;
    }

    // Concrete method to display vehicle details
    public void displayVehicleDetails() {
        System.out.println("----- Vehicle Details -----");
        System.out.println("Vehicle ID          : " + this.vehicleId);
        System.out.println("Brand & Model       : " + this.brand + " " + this.model);
        System.out.println("Category            : " + this.category);
        System.out.println("Registration Number : " + this.registrationNumber);
        System.out.println("Price Per Day       : $" + this.pricePerDay);
        System.out.println("Customer Rating     : " + String.format("%.1f", this.rating) + " / 5.0");
        System.out.println("Availability        : " + (this.available ? "Available" : "Rented / Unavailable"));
        System.out.println("---------------------------");
    }
}
