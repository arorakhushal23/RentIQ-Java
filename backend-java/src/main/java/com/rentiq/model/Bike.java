package com.rentiq.model;

/**
 * Represents a Motorcycle/Bike vehicle in the RentIQ system.
 * Extends Vehicle to demonstrate inheritance, constructor chaining using super,
 * method overriding, and runtime polymorphism.
 */
public class Bike extends Vehicle {
    private int engineCapacityCc;
    private boolean helmetIncluded;

    // Default constructor
    public Bike() {
        super();
        setCategory("Bike");
    }

    // Parameterized constructor
    public Bike(String vehicleId, String brand, String model, String registrationNumber,
                double pricePerDay, boolean available, int engineCapacityCc, boolean helmetIncluded) {
        super(vehicleId, brand, model, "Bike", registrationNumber, pricePerDay, available);
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
        System.out.println("Availability        : " + (isAvailable() ? "Available" : "Rented / Unavailable"));
        System.out.println("------------------------");
    }
}
