package com.rentiq.service;

import com.rentiq.model.Vehicle;

/**
 * Service class for managing vehicle catalog and rental availability.
 * Implements vehicle management operations using standard arrays without Collections.
 * Demonstrates String handling, search, case comparisons, and StringBuilder formatting.
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

    // 2. Display all vehicles
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
            if (vehicles[i].getVehicleId().equalsIgnoreCase(vehicleId.trim())) {
                return vehicles[i];
            }
        }
        return null;
    }

    /**
     * Searches vehicles matching a keyword in brand, model, or category.
     * Demonstrates String manipulation, case-insensitivity, and StringBuilder.
     */
    public int searchVehicles(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("[Search] Please provide a valid non-empty keyword.");
            return 0;
        }
        String cleanKeyword = keyword.trim().toLowerCase();
        StringBuilder results = new StringBuilder();
        results.append("\n--- Search Results for '").append(keyword.trim()).append("' ---\n");

        int matchCount = 0;
        for (int i = 0; i < vehicleCount; i++) {
            Vehicle v = vehicles[i];
            boolean brandMatch = v.getBrand() != null && v.getBrand().toLowerCase().contains(cleanKeyword);
            boolean modelMatch = v.getModel() != null && v.getModel().toLowerCase().contains(cleanKeyword);
            boolean categoryMatch = v.getCategory() != null && v.getCategory().equalsIgnoreCase(keyword.trim());

            if (brandMatch || modelMatch || categoryMatch) {
                matchCount++;
                results.append(String.format("Match #%d: [%s] %s %s (%s) - $%.2f/day - %s\n",
                    matchCount, v.getVehicleId(), v.getBrand(), v.getModel(), v.getCategory(),
                    v.getPricePerDay(), (v.isAvailable() ? "Available" : "Rented")));
            }
        }

        if (matchCount == 0) {
            results.append("No matching vehicles found for: '").append(keyword).append("'\n");
        }
        results.append("------------------------------------------------\n");
        System.out.println(results.toString());
        return matchCount;
    }

    /**
     * Filters vehicles by category (e.g. "Car", "Bike").
     * Demonstrates equalsIgnoreCase string comparison.
     */
    public int filterByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return 0;
        }
        System.out.println("\n--- Vehicles in Category: " + category.trim() + " ---");
        int count = 0;
        for (int i = 0; i < vehicleCount; i++) {
            if (vehicles[i].getCategory() != null && vehicles[i].getCategory().equalsIgnoreCase(category.trim())) {
                count++;
                vehicles[i].displayVehicleDetails();
            }
        }
        if (count == 0) {
            System.out.println("No vehicles found in category '" + category + "'.");
        }
        return count;
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
