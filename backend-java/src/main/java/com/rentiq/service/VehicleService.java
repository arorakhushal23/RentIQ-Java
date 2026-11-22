package com.rentiq.service;

import com.rentiq.model.Vehicle;

/**
 * Service class for managing vehicle catalog and rental availability.
 * Implements vehicle management operations using standard arrays without Collections.
 */
public class VehicleService {
    private Vehicle[] vehicles;
    private int vehicleCount;
    private static final int DEFAULT_CAPACITY = 10;

    public VehicleService() {
        this(DEFAULT_CAPACITY);
    }

    public VehicleService(int capacity) {
        this.vehicles = new Vehicle[capacity];
        this.vehicleCount = 0;
    }

    // 1. Add vehicle
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            System.out.println("[Error] Cannot add a null vehicle.");
            return false;
        }
        if (vehicleCount >= vehicles.length) {
            // Expand array capacity dynamically using standard array copying
            Vehicle[] expanded = new Vehicle[vehicles.length * 2];
            for (int i = 0; i < vehicles.length; i++) {
                expanded[i] = vehicles[i];
            }
            vehicles = expanded;
        }
        vehicles[vehicleCount] = vehicle;
        vehicleCount++;
        System.out.println("[Service] Added vehicle: " + vehicle.getBrand() + " " + vehicle.getModel() + " [" + vehicle.getVehicleId() + "]");
        return true;
    }

    // 2. Display all vehicles (demonstrates runtime polymorphism)
    public void displayAllVehicles() {
        System.out.println("\n========== RentIQ Vehicle Fleet (" + vehicleCount + " vehicles) ==========");
        if (vehicleCount == 0) {
            System.out.println("No vehicles currently in fleet.");
            System.out.println("============================================================\n");
            return;
        }
        for (int i = 0; i < vehicleCount; i++) {
            vehicles[i].displayVehicleDetails();
        }
        System.out.println("============================================================\n");
    }

    // 3. Search vehicle by ID
    public Vehicle searchVehicleById(String vehicleId) {
        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            return null;
        }
        for (int i = 0; i < vehicleCount; i++) {
            if (vehicles[i].getVehicleId().equalsIgnoreCase(vehicleId)) {
                return vehicles[i];
            }
        }
        return null;
    }

    // 4. Update vehicle availability
    public boolean updateVehicleAvailability(String vehicleId, boolean available) {
        Vehicle vehicle = searchVehicleById(vehicleId);
        if (vehicle != null) {
            vehicle.updateAvailability(available);
            System.out.println("[Service] Availability updated for " + vehicleId + " -> " + (available ? "Available" : "Rented / Unavailable"));
            return true;
        }
        System.out.println("[Service] Vehicle with ID '" + vehicleId + "' not found.");
        return false;
    }

    // 5. Display vehicle details
    public void displayVehicleDetails(String vehicleId) {
        Vehicle vehicle = searchVehicleById(vehicleId);
        if (vehicle != null) {
            vehicle.displayVehicleDetails();
        } else {
            System.out.println("[Service] Vehicle with ID '" + vehicleId + "' not found.");
        }
    }

    public int getVehicleCount() {
        return this.vehicleCount;
    }
}
