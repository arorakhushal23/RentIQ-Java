package com.rentiq.model;

/**
 * Represents a Car vehicle in the RentIQ system.
 * Extends Vehicle to demonstrate inheritance, constructor chaining using super,
 * method overriding, and runtime polymorphism.
 */
public class Car extends Vehicle {
    private int seatingCapacity;
    private String fuelType;

    // Default constructor
    public Car() {
        super();
        setCategory("Car");
    }

    // Parameterized constructor
    public Car(String vehicleId, String brand, String model, String registrationNumber,
               double pricePerDay, boolean available, int seatingCapacity, String fuelType) {
        super(vehicleId, brand, model, "Car", registrationNumber, pricePerDay, available);
        this.seatingCapacity = seatingCapacity;
        this.fuelType = fuelType;
    }

    public int getSeatingCapacity() {
        return this.seatingCapacity;
    }

    public void setSeatingCapacity(int seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public String getFuelType() {
        return this.fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    @Override
    public void displayVehicleDetails() {
        System.out.println("----- Car Details -----");
        System.out.println("Vehicle ID          : " + getVehicleId());
        System.out.println("Brand & Model       : " + getBrand() + " " + getModel());
        System.out.println("Category            : " + getCategory());
        System.out.println("Registration Number : " + getRegistrationNumber());
        System.out.println("Seating Capacity    : " + this.seatingCapacity + " Seats");
        System.out.println("Fuel Type           : " + this.fuelType);
        System.out.println("Price Per Day       : $" + getPricePerDay());
        System.out.println("Availability        : " + (isAvailable() ? "Available" : "Rented / Unavailable"));
        System.out.println("-----------------------");
    }
}
