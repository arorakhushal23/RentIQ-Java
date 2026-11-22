package com.rentiq.service;

import com.rentiq.model.Vehicle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Service class for managing vehicle catalog, rental availability, recently viewed tracking,
 * and category analysis using Java Collections Framework (ArrayList, LinkedList, HashSet, LinkedHashSet, TreeSet).
 */
public class VehicleService {
    // Dynamic fleet storage using ArrayList
    private List<Vehicle> vehicles;

    // Recently viewed vehicles tracked via LinkedList (acts as MRU list)
    private LinkedList<Vehicle> recentlyViewedVehicles;
    private static final int MAX_RECENTLY_VIEWED = 10;

    public VehicleService() {
        this.vehicles = new ArrayList<>();
        this.recentlyViewedVehicles = new LinkedList<>();
    }

    public VehicleService(int initialCapacity) {
        this.vehicles = new ArrayList<>(initialCapacity);
        this.recentlyViewedVehicles = new LinkedList<>();
    }

    // 1. Add vehicle
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            System.out.println("[Error] Cannot add a null vehicle.");
            return false;
        }
        vehicles.add(vehicle);
        System.out.println("[Service] Added vehicle: " + vehicle.getBrand() + " " + vehicle.getModel() + " [" + vehicle.getVehicleId() + "]");
        return true;
    }

    // 2. Display all vehicles
    public void displayAllVehicles() {
        System.out.println("\n========== RentIQ Vehicle Fleet (" + vehicles.size() + " vehicles) ==========");
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles currently in fleet.");
            System.out.println("============================================================\n");
            return;
        }
        for (Vehicle v : vehicles) {
            v.displayVehicleDetails();
        }
        System.out.println("============================================================\n");
    }

    // 3. Search vehicle by ID
    public Vehicle searchVehicleById(String vehicleId) {
        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            return null;
        }
        String cleanId = vehicleId.trim();
        for (Vehicle v : vehicles) {
            if (v.getVehicleId().equalsIgnoreCase(cleanId)) {
                return v;
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
        for (Vehicle v : vehicles) {
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
     */
    public int filterByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return 0;
        }
        System.out.println("\n--- Vehicles in Category: " + category.trim() + " ---");
        int count = 0;
        for (Vehicle v : vehicles) {
            if (v.getCategory() != null && v.getCategory().equalsIgnoreCase(category.trim())) {
                count++;
                v.displayVehicleDetails();
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

    // 5. Display vehicle details and record to recently viewed list
    public void displayVehicleDetails(String vehicleId) {
        Vehicle vehicle = searchVehicleById(vehicleId);
        if (vehicle != null) {
            recordRecentlyViewed(vehicle);
            vehicle.displayVehicleDetails();
        } else {
            System.out.println("[Service] Vehicle with ID '" + vehicleId + "' not found.");
        }
    }

    // ==========================================================
    // LinkedList Implementation: Recently Viewed Vehicles
    // ==========================================================

    /**
     * Adds a vehicle to the recently viewed LinkedList.
     * Moves existing entry to the head of the list (Most Recently Used).
     */
    public void recordRecentlyViewed(Vehicle vehicle) {
        if (vehicle == null) {
            return;
        }
        // Remove prior occurrence to prevent duplicates in recently viewed queue
        recentlyViewedVehicles.removeIf(v -> v.getVehicleId().equalsIgnoreCase(vehicle.getVehicleId()));
        // Add to front of LinkedList (O(1) operation)
        recentlyViewedVehicles.addFirst(vehicle);
        // Maintain fixed maximum capacity
        if (recentlyViewedVehicles.size() > MAX_RECENTLY_VIEWED) {
            recentlyViewedVehicles.removeLast();
        }
    }

    /**
     * Removes a vehicle from the recently viewed LinkedList by ID.
     */
    public boolean removeRecentlyViewed(String vehicleId) {
        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            return false;
        }
        return recentlyViewedVehicles.removeIf(v -> v.getVehicleId().equalsIgnoreCase(vehicleId.trim()));
    }

    /**
     * Clears all entries from the recently viewed LinkedList.
     */
    public void clearRecentlyViewed() {
        recentlyViewedVehicles.clear();
    }

    public List<Vehicle> getRecentlyViewedVehicles() {
        return Collections.unmodifiableList(this.recentlyViewedVehicles);
    }

    /**
     * Displays all recently viewed vehicles in MRU order.
     */
    public void displayRecentlyViewedVehicles() {
        System.out.println("\n========== Recently Viewed Vehicles (LinkedList - MRU Order) ==========");
        if (recentlyViewedVehicles.isEmpty()) {
            System.out.println("No recently viewed vehicles recorded yet.");
            System.out.println("========================================================================\n");
            return;
        }
        int rank = 1;
        for (Vehicle v : recentlyViewedVehicles) {
            System.out.printf("%d. [%s] %s %s (%s) - $%.2f/day - %s\n",
                rank++, v.getVehicleId(), v.getBrand(), v.getModel(), v.getCategory(),
                v.getPricePerDay(), (v.isAvailable() ? "Available" : "Rented"));
        }
        System.out.println("========================================================================\n");
    }

    // ==========================================================
    // Set Implementations: HashSet, LinkedHashSet, TreeSet
    // ==========================================================

    /**
     * Returns unique vehicle categories using HashSet.
     * Demonstrates constant-time O(1) performance and duplicate elimination with non-guaranteed order.
     */
    public Set<String> getCategoryHashSet() {
        Set<String> categories = new HashSet<>();
        for (Vehicle v : vehicles) {
            if (v.getCategory() != null && !v.getCategory().trim().isEmpty()) {
                categories.add(v.getCategory().trim());
            }
        }
        return categories;
    }

    /**
     * Returns unique vehicle categories using LinkedHashSet.
     * Demonstrates duplicate elimination while preserving insertion order.
     */
    public Set<String> getCategoryLinkedHashSet() {
        Set<String> categories = new LinkedHashSet<>();
        for (Vehicle v : vehicles) {
            if (v.getCategory() != null && !v.getCategory().trim().isEmpty()) {
                categories.add(v.getCategory().trim());
            }
        }
        return categories;
    }

    /**
     * Returns unique vehicle categories using TreeSet.
     * Demonstrates duplicate elimination and natural sorted order (alphabetical).
     */
    public Set<String> getCategoryTreeSet() {
        Set<String> categories = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Vehicle v : vehicles) {
            if (v.getCategory() != null && !v.getCategory().trim().isEmpty()) {
                categories.add(v.getCategory().trim());
            }
        }
        return categories;
    }

    /**
     * Displays a comparative overview of all 3 Set implementations.
     */
    public void displayCategoryOverview() {
        System.out.println("\n================= Vehicle Categories (Set Implementations) =================");
        System.out.println("1. HashSet (Unique, Fast Lookup): " + getCategoryHashSet());
        System.out.println("2. LinkedHashSet (Unique, Preserves Insertion Order): " + getCategoryLinkedHashSet());
        System.out.println("3. TreeSet (Unique, Sorted Alphabetically): " + getCategoryTreeSet());
        System.out.println("=============================================================================\n");
    }

    public int getVehicleCount() {
        return this.vehicles.size();
    }

    public List<Vehicle> getAllVehicles() {
        return Collections.unmodifiableList(this.vehicles);
    }
}
