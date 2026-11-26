package com.rentiq.model;

public class Car extends Vehicle {
    private int seatingCapacity;
    private String fuelType;

    public Car() {
        super();
        setCategory("Car");
    }

    public Car(String vehicleId, String brand, String model, String registrationNumber,
               double pricePerDay, boolean available, int seatingCapacity, String fuelType) {
        this(vehicleId, brand, model, registrationNumber, pricePerDay, available, seatingCapacity, fuelType, 5.0);
    }

    public Car(String vehicleId, String brand, String model, String registrationNumber,
               double pricePerDay, boolean available, int seatingCapacity, String fuelType, double rating) {
        super(vehicleId, brand, model, "Car", registrationNumber, pricePerDay, available, rating);
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
    public double calculateRentalCost(int days) {
        if (days <= 0) {
            return 0.0;
        }
        return getPricePerDay() * days;
    }

    @Override
    public void displayVehicleDetails() {
        String statusText;
        if (isAvailable()) {
            statusText = "Available";
        } else {
            statusText = "Rented / Unavailable";
        }

        System.out.println("----- Car Details -----");
        System.out.println("Vehicle ID          : " + getVehicleId());
        System.out.println("Brand & Model       : " + getName());
        System.out.println("Category            : " + getCategory());
        System.out.println("Registration Number : " + getRegistrationNumber());
        System.out.println("Seating Capacity    : " + this.seatingCapacity + " Seats");
        System.out.println("Fuel Type           : " + this.fuelType);
        System.out.println("Price Per Day       : $" + getPricePerDay());
        System.out.println("Customer Rating     : " + String.format("%.1f", getRating()) + " / 5.0");
        System.out.println("Availability        : " + statusText);
        System.out.println("-----------------------");
    }
}
