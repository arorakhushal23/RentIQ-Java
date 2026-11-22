package com.rentiq.model;

/**
 * Represents a Motorcycle/Bike vehicle in the RentIQ system.
 * Extends abstract Vehicle class to implement concrete rental cost calculation,
 * bike-specific attributes, ratings, and multi-criteria sorting.
 */
public class Bike extends Vehicle {
    private int engineCapacityCc;
    private boolean helmetIncluded;

    // Default constructor
    public Bike() {
        super();
        setCategory("Bike");
    }

    // Parameterized constructor without explicit rating
    public Bike(String vehicleId, String brand, String model, String registrationNumber,
                double pricePerDay, boolean available, int engineCapacityCc, boolean helmetIncluded) {
        this(vehicleId, brand, model, registrationNumber, pricePerDay, available, engineCapacityCc, helmetIncluded, 5.0);
    }

    // Full Parameterized constructor with rating
    public Bike(String vehicleId, String brand, String model, String registrationNumber,
                double pricePerDay, boolean available, int engineCapacityCc, boolean helmetIncluded, double rating) {
        super(vehicleId, brand, model, "Bike", registrationNumber, pricePerDay, available, rating);
        this.engineCapacityCc = engineCapacityCc;
        this.helmetIncluded = helmetIncluded;
    }

    public int getEngineCapacityCc() {
        return this.engineCapacityCc;
    }

    public void setEngineCapacityCc(int engineCapacityCc) {
        this.engineCapacityCc = engineCapacityCc;
    }

    public boolean isHelmetIncluded() {
        return this.helmetIncluded;
    }

    public void setHelmetIncluded(boolean helmetIncluded) {
        this.helmetIncluded = helmetIncluded;
    }

    /**
     * Implementation of abstract method from Vehicle.
     * Calculates bike rental cost: pricePerDay * days.
     */
    @Override
    public double calculateRentalCost(int days) {
        if (days <= 0) {
            return 0.0;
        }
        return getPricePerDay() * days;
    }

    @Override
    public void displayVehicleDetails() {
        System.out.println("----- Bike Details -----");
        System.out.println("Vehicle ID          : " + getVehicleId());
        System.out.println("Brand & Model       : " + getBrand() + " " + getModel());
        System.out.println("Category            : " + getCategory());
        System.out.println("Registration Number : " + getRegistrationNumber());
        System.out.println("Engine Capacity     : " + this.engineCapacityCc + " cc");
        System.out.println("Helmet Included     : " + (this.helmetIncluded ? "Yes" : "No"));
        System.out.println("Price Per Day       : $" + getPricePerDay());
        System.out.println("Customer Rating     : " + String.format("%.1f", getRating()) + " / 5.0");
        System.out.println("Availability        : " + (isAvailable() ? "Available" : "Rented / Unavailable"));
        System.out.println("------------------------");
    }
}
