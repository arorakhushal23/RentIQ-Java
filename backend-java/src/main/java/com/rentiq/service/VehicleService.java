package com.rentiq.service;

import com.rentiq.model.Vehicle;
import com.rentiq.util.VehicleComparators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Service class for managing vehicle catalog and rental availability.
 * Integrates:
 * - ArrayList: dynamic fleet storage.
 * - HashMap: O(1) Vehicle ID to Vehicle lookup with duplicate prevention.
 * - LinkedHashMap: insertion-ordered vehicle tracking.
 * - TreeMap: naturally sorted vehicle ID index.
 * - LinkedList: recently viewed vehicles (MRU cache).
 * - Iterator: safe element removal of unavailable/discontinued vehicles.
 * - ListIterator: bidirectional forward and backward traversal.
 * - Comparable & Comparator: natural and multi-criteria sorting (Price, Name, Rating).
 */
public class VehicleService {
    private List<Vehicle> vehicles;
    private Map<String, Vehicle> vehicleMap;
    private LinkedHashMap<String, Vehicle> vehiclesByInsertionOrder;
    private LinkedList<Vehicle> recentlyViewedVehicles;
    private static final int MAX_RECENTLY_VIEWED = 10;

    public VehicleService() {
        this.vehicles = new ArrayList<>();
        this.vehicleMap = new HashMap<>();
        this.vehiclesByInsertionOrder = new LinkedHashMap<>();
        this.recentlyViewedVehicles = new LinkedList<>();
    }

    public VehicleService(int initialCapacity) {
        this.vehicles = new ArrayList<>(initialCapacity);
        this.vehicleMap = new HashMap<>(initialCapacity);
        this.vehiclesByInsertionOrder = new LinkedHashMap<>(initialCapacity);
        this.recentlyViewedVehicles = new LinkedList<>();
    }

    // 1. Add vehicle with duplicate ID prevention
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            System.out.println("[Error] Cannot add a null vehicle.");
            return false;
        }

        String key = vehicle.getVehicleId() != null ? vehicle.getVehicleId().trim().toUpperCase() : "";
        if (key.isEmpty()) {
            System.out.println("[Error] Vehicle ID cannot be empty.");
            return false;
        }

        // Check for duplicate vehicle ID
        if (vehicleMap.containsKey(key)) {
            System.out.println("[Error] Duplicate vehicle ID: '" + vehicle.getVehicleId() + "' already exists in fleet. Add rejected.");
            return false;
        }

        // Synchronize List, HashMap, and LinkedHashMap
        vehicles.add(vehicle);
        vehicleMap.put(key, vehicle);
        vehiclesByInsertionOrder.put(vehicle.getVehicleId(), vehicle);

        System.out.println("[Service] Added vehicle: " + vehicle.getBrand() + " " + vehicle.getModel() +
                           " [" + vehicle.getVehicleId() + "], Price: $" + vehicle.getPricePerDay() + "/day, Rating: " + vehicle.getRating());
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

    // 3. Search vehicle by ID in O(1) time using HashMap
    public Vehicle searchVehicleById(String vehicleId) {
        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            return null;
        }
        return vehicleMap.get(vehicleId.trim().toUpperCase());
    }

    /**
     * Searches vehicles matching a keyword in brand, model, or category.
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
                results.append(String.format("Match #%d: [%s] %s %s (%s) - $%.2f/day - Rating: %.1f - %s\n",
                    matchCount, v.getVehicleId(), v.getBrand(), v.getModel(), v.getCategory(),
                    v.getPricePerDay(), v.getRating(), (v.isAvailable() ? "Available" : "Rented")));
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
    // Map Implementations: LinkedHashMap and TreeMap
    // ==========================================================

    /**
     * Returns vehicle records in exact insertion order using LinkedHashMap.
     */
    public LinkedHashMap<String, Vehicle> getVehiclesByInsertionOrder() {
        return new LinkedHashMap<>(this.vehiclesByInsertionOrder);
    }

    public void displayVehiclesByInsertionOrder() {
        System.out.println("\n===== Vehicles Fleet (LinkedHashMap - Insertion Order) =====");
        if (vehiclesByInsertionOrder.isEmpty()) {
            System.out.println("No vehicles recorded.");
            System.out.println("============================================================\n");
            return;
        }
        int rank = 1;
        for (Map.Entry<String, Vehicle> entry : vehiclesByInsertionOrder.entrySet()) {
            Vehicle v = entry.getValue();
            System.out.printf("%d. [%s] %s %s (%s) - $%.2f/day - Rating: %.1f\n",
                rank++, entry.getKey(), v.getBrand(), v.getModel(), v.getCategory(), v.getPricePerDay(), v.getRating());
        }
        System.out.println("============================================================\n");
    }

    /**
     * Returns vehicles sorted by Vehicle ID using TreeMap.
     */
    public TreeMap<String, Vehicle> getVehiclesSortedById() {
        TreeMap<String, Vehicle> sortedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedMap.putAll(this.vehicleMap);
        return sortedMap;
    }

    public void displayVehiclesSortedById() {
        System.out.println("\n===== Vehicles Fleet (TreeMap - Sorted Alphabetically by ID) =====");
        TreeMap<String, Vehicle> sortedMap = getVehiclesSortedById();
        if (sortedMap.isEmpty()) {
            System.out.println("No vehicles in fleet.");
            System.out.println("==================================================================\n");
            return;
        }
        for (Map.Entry<String, Vehicle> entry : sortedMap.entrySet()) {
            Vehicle v = entry.getValue();
            System.out.printf("Key: %-6s -> [%s] %s %s ($%.2f/day, Rating: %.1f, %s)\n",
                entry.getKey(), v.getVehicleId(), v.getBrand(), v.getModel(), v.getPricePerDay(), v.getRating(),
                v.isAvailable() ? "Available" : "Rented");
        }
        System.out.println("==================================================================\n");
    }

    // ==========================================================
    // Iterator & ListIterator Implementations
    // ==========================================================

    /**
     * Uses standard Iterator to safely remove unavailable or discontinued vehicles.
     * Prevents ConcurrentModificationException and synchronizes Map and Queue structures.
     */
    public int removeUnavailableVehiclesWithIterator() {
        System.out.println("\n[Iterator] Initiating safe removal of unavailable vehicles using Iterator.remove()...");
        int removedCount = 0;
        Iterator<Vehicle> it = vehicles.iterator();
        while (it.hasNext()) {
            Vehicle v = it.next();
            if (!v.isAvailable()) {
                it.remove(); // Safe structural removal via Iterator
                vehicleMap.remove(v.getVehicleId().toUpperCase());
                vehiclesByInsertionOrder.remove(v.getVehicleId());
                recentlyViewedVehicles.removeIf(r -> r.getVehicleId().equalsIgnoreCase(v.getVehicleId()));
                removedCount++;
                System.out.println("  -> Safely removed: [" + v.getVehicleId() + "] " + v.getName() + " (Status: Unavailable/Rented)");
            }
        }
        System.out.println("[Iterator] Safe removal complete. Total vehicles removed: " + removedCount + ". Remaining fleet: " + vehicles.size() + "\n");
        return removedCount;
    }

    /**
     * Uses ListIterator to demonstrate bidirectional traversal (Forward and Backward).
     */
    public void traverseVehiclesBidirectional() {
        System.out.println("\n===== ListIterator Bidirectional Traversal Demonstration =====");
        if (vehicles.isEmpty()) {
            System.out.println("Vehicle list is empty.");
            System.out.println("==============================================================\n");
            return;
        }

        System.out.println("--- Forward Traversal (hasNext() / next()) ---");
        ListIterator<Vehicle> lit = vehicles.listIterator();
        while (lit.hasNext()) {
            int index = lit.nextIndex();
            Vehicle v = lit.next();
            System.out.printf("Index %2d -> [%s] %-20s | $%-6.2f | Rating: %.1f\n",
                index, v.getVehicleId(), v.getName(), v.getPricePerDay(), v.getRating());
        }

        System.out.println("\n--- Backward Traversal (hasPrevious() / previous()) ---");
        while (lit.hasPrevious()) {
            int index = lit.previousIndex();
            Vehicle v = lit.previous();
            System.out.printf("Index %2d <- [%s] %-20s | $%-6.2f | Rating: %.1f\n",
                index, v.getVehicleId(), v.getName(), v.getPricePerDay(), v.getRating());
        }
        System.out.println("==============================================================\n");
    }

    // ==========================================================
    // Comparable & Comparator Implementations
    // ==========================================================

    /**
     * Sorts vehicles naturally using Comparable<Vehicle> (Vehicle ID alphabetical).
     */
    public List<Vehicle> getVehiclesSortedNaturally() {
        List<Vehicle> copy = new ArrayList<>(this.vehicles);
        Collections.sort(copy); // Relies on Vehicle.compareTo
        return copy;
    }

    public void displayVehiclesSortedNaturally() {
        displaySortedFleetTable("Vehicles Sorted by Natural Ordering (Comparable: Vehicle ID)", getVehiclesSortedNaturally());
    }

    /**
     * Sorts vehicles using custom Comparator.
     */
    public List<Vehicle> getVehiclesSortedBy(Comparator<Vehicle> comparator) {
        List<Vehicle> copy = new ArrayList<>(this.vehicles);
        copy.sort(comparator);
        return copy;
    }

    public void displayVehiclesSortedByPriceAsc() {
        displaySortedFleetTable("Vehicles Sorted by Price: Lowest to Highest (Comparator)",
            getVehiclesSortedBy(VehicleComparators.BY_PRICE_ASC));
    }

    public void displayVehiclesSortedByPriceDesc() {
        displaySortedFleetTable("Vehicles Sorted by Price: Highest to Lowest (Comparator)",
            getVehiclesSortedBy(VehicleComparators.BY_PRICE_DESC));
    }

    public void displayVehiclesSortedByName() {
        displaySortedFleetTable("Vehicles Sorted by Name: Alphabetical Order (Comparator)",
            getVehiclesSortedBy(VehicleComparators.BY_NAME_ASC));
    }

    public void displayVehiclesSortedByRatingDesc() {
        displaySortedFleetTable("Vehicles Sorted by Customer Rating: Highest to Lowest (Comparator)",
            getVehiclesSortedBy(VehicleComparators.BY_RATING_DESC));
    }

    private void displaySortedFleetTable(String title, List<Vehicle> sortedList) {
        System.out.println("\n==================== " + title + " ====================");
        System.out.printf("%-6s | %-24s | %-10s | %-10s | %-6s | %-12s\n",
            "ID", "Brand & Model", "Category", "Price/Day", "Rating", "Status");
        System.out.println("----------------------------------------------------------------------------------");
        for (Vehicle v : sortedList) {
            System.out.printf("%-6s | %-24s | %-10s | $%-9.2f | %-6.1f | %-12s\n",
                v.getVehicleId(), v.getName(), v.getCategory(), v.getPricePerDay(), v.getRating(),
                v.isAvailable() ? "Available" : "Unavailable");
        }
        System.out.println("==================================================================================\n");
    }

    // ==========================================================
    // LinkedList Implementation: Recently Viewed Vehicles
    // ==========================================================

    public void recordRecentlyViewed(Vehicle vehicle) {
        if (vehicle == null) {
            return;
        }
        recentlyViewedVehicles.removeIf(v -> v.getVehicleId().equalsIgnoreCase(vehicle.getVehicleId()));
        recentlyViewedVehicles.addFirst(vehicle);
        if (recentlyViewedVehicles.size() > MAX_RECENTLY_VIEWED) {
            recentlyViewedVehicles.removeLast();
        }
    }

    public boolean removeRecentlyViewed(String vehicleId) {
        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            return false;
        }
        return recentlyViewedVehicles.removeIf(v -> v.getVehicleId().equalsIgnoreCase(vehicleId.trim()));
    }

    public void clearRecentlyViewed() {
        recentlyViewedVehicles.clear();
    }

    public List<Vehicle> getRecentlyViewedVehicles() {
        return Collections.unmodifiableList(this.recentlyViewedVehicles);
    }

    public void displayRecentlyViewedVehicles() {
        System.out.println("\n========== Recently Viewed Vehicles (LinkedList - MRU Order) ==========");
        if (recentlyViewedVehicles.isEmpty()) {
            System.out.println("No recently viewed vehicles recorded yet.");
            System.out.println("========================================================================\n");
            return;
        }
        int rank = 1;
        for (Vehicle v : recentlyViewedVehicles) {
            System.out.printf("%d. [%s] %s %s (%s) - $%.2f/day - Rating: %.1f - %s\n",
                rank++, v.getVehicleId(), v.getBrand(), v.getModel(), v.getCategory(),
                v.getPricePerDay(), v.getRating(), (v.isAvailable() ? "Available" : "Rented"));
        }
        System.out.println("========================================================================\n");
    }

    // ==========================================================
    // Set Implementations: HashSet, LinkedHashSet, TreeSet
    // ==========================================================

    public Set<String> getCategoryHashSet() {
        Set<String> categories = new HashSet<>();
        for (Vehicle v : vehicles) {
            if (v.getCategory() != null && !v.getCategory().trim().isEmpty()) {
                categories.add(v.getCategory().trim());
            }
        }
        return categories;
    }

    public Set<String> getCategoryLinkedHashSet() {
        Set<String> categories = new LinkedHashSet<>();
        for (Vehicle v : vehicles) {
            if (v.getCategory() != null && !v.getCategory().trim().isEmpty()) {
                categories.add(v.getCategory().trim());
            }
        }
        return categories;
    }

    public Set<String> getCategoryTreeSet() {
        Set<String> categories = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Vehicle v : vehicles) {
            if (v.getCategory() != null && !v.getCategory().trim().isEmpty()) {
                categories.add(v.getCategory().trim());
            }
        }
        return categories;
    }

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

    public Map<String, Vehicle> getVehicleMap() {
        return Collections.unmodifiableMap(this.vehicleMap);
    }
}
