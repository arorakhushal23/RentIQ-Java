package com.rentiq.model;

/**
 * Represents a vehicle in the RentIQ system.
 * Demonstrates Core Java fundamentals: classes, objects, encapsulation,
 * access modifiers, constructors, and the 'this' keyword.
 */
public class Vehicle {
    private String vehicleId;
    private String brand;
    private String model;
    private String category;
    private String registrationNumber;
    private double pricePerDay;
    private boolean available;

    // Default constructor
    public Vehicle() {
        this.available = true;
    }

    // Parameterized constructor
    public Vehicle(String vehicleId, String brand, String model, String category, String registrationNumber, double pricePerDay, boolean available) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.registrationNumber = registrationNumber;
        this.pricePerDay = pricePerDay;
        this.available = available;
    }

    // Overloaded constructor without registrationNumber
    public Vehicle(String vehicleId, String brand, String model, String category, double pricePerDay, boolean available) {
        this(vehicleId, brand, model, category, "N/A", pricePerDay, available);
    }

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

    public boolean isAvailable() {
        return this.available;
    }

    public boolean getAvailable() {
        return this.available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Method to update availability
    public void updateAvailability(boolean available) {
        this.available = available;
    }

    // Method to display vehicle details
    public void displayVehicleDetails() {
        System.out.println("----- Vehicle Details -----");
        System.out.println("Vehicle ID          : " + this.vehicleId);
        System.out.println("Brand & Model       : " + this.brand + " " + this.model);
        System.out.println("Category            : " + this.category);
        System.out.println("Registration Number : " + this.registrationNumber);
        System.out.println("Price Per Day       : $" + this.pricePerDay);
        System.out.println("Availability        : " + (this.available ? "Available" : "Rented / Unavailable"));
        System.out.println("---------------------------");
    }
}
