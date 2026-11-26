package com.rentiq.model;

public class Bike extends Vehicle {
    private int engineCapacityCc;
    private boolean helmetIncluded;

    public Bike() {
        super();
        setCategory("Bike");
    }

    public Bike(String vehicleId, String brand, String model, String registrationNumber,
                double pricePerDay, boolean available, int engineCapacityCc, boolean helmetIncluded) {
        this(vehicleId, brand, model, registrationNumber, pricePerDay, available, engineCapacityCc, helmetIncluded, 5.0);
    }

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

        String helmetText;
        if (this.helmetIncluded) {
            helmetText = "Yes";
        } else {
            helmetText = "No";
        }

        System.out.println("----- Bike Details -----");
        System.out.println("Vehicle ID          : " + getVehicleId());
        System.out.println("Brand & Model       : " + getName());
        System.out.println("Category            : " + getCategory());
        System.out.println("Registration Number : " + getRegistrationNumber());
        System.out.println("Engine Capacity     : " + this.engineCapacityCc + " cc");
        System.out.println("Helmet Included     : " + helmetText);
        System.out.println("Price Per Day       : $" + getPricePerDay());
        System.out.println("Customer Rating     : " + String.format("%.1f", getRating()) + " / 5.0");
        System.out.println("Availability        : " + statusText);
        System.out.println("------------------------");
    }
}
