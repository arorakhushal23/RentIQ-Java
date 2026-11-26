package com.rentiq.service;

import com.rentiq.database.DatabaseConnection;
import com.rentiq.model.Bike;
import com.rentiq.model.Car;
import com.rentiq.model.Vehicle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VehicleService {

    private final List<Vehicle> vehicles = new ArrayList<>();
    private final Map<String, Vehicle> vehicleMap = new HashMap<>();

    private final LinkedList<Vehicle> recentlyViewedVehicles = new LinkedList<>();
    private static final int MAX_RECENTLY_VIEWED = 10;

   
    public List<Vehicle> getAllVehicles() {
        return Collections.unmodifiableList(this.vehicles);
    }

    public Vehicle searchVehicleById(String vehicleId) {
        if (vehicleId == null || vehicleId.trim().isEmpty()) {
            return null;
        }

        return vehicleMap.get(vehicleId.trim().toUpperCase());
    }

    public List<Vehicle> searchVehicles(String keyword) {
        List<Vehicle> matches = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return matches;
        }

        String clean = keyword.trim().toLowerCase();

        for (Vehicle v : vehicles) {
            boolean brandMatch = v.getBrand() != null
                    && v.getBrand().toLowerCase().contains(clean);

            boolean modelMatch = v.getModel() != null
                    && v.getModel().toLowerCase().contains(clean);

            boolean catMatch = v.getCategory() != null
                    && v.getCategory().toLowerCase().contains(clean);

            if (brandMatch || modelMatch || catMatch) {
                matches.add(v);
            }
        }

        return matches;
    }

    public void compareVehicles(String id1, String id2) {
        Vehicle v1 = searchVehicleById(id1);
        Vehicle v2 = searchVehicleById(id2);

        if (v1 == null || v2 == null) {
            System.out.println("[Comparison Error] One or both vehicle IDs do not exist in the fleet.");
            return;
        }

        recordRecentlyViewed(v1);
        recordRecentlyViewed(v2);

        String v1Status = v1.isAvailable() ? "AVAILABLE" : "RENTED / UNAVAILABLE";
        String v2Status = v2.isAvailable() ? "AVAILABLE" : "RENTED / UNAVAILABLE";

        String v1Details = getVehicleSpecificationSummary(v1);
        String v2Details = getVehicleSpecificationSummary(v2);

        System.out.println("\n==========================================================================================");
        System.out.println("                                VEHICLE COMPARISON ANALYSIS                               ");
        System.out.println("==========================================================================================");

        System.out.printf("%-22s | %-31s | %-31s\n",
                "Feature / Attribute",
                "Vehicle 1 [" + v1.getVehicleId() + "]",
                "Vehicle 2 [" + v2.getVehicleId() + "]");

        System.out.println("------------------------------------------------------------------------------------------");

        System.out.printf("%-22s | %-31s | %-31s\n", "Name", v1.getName(), v2.getName());
        System.out.printf("%-22s | %-31s | %-31s\n", "Category", v1.getCategory(), v2.getCategory());

        System.out.printf("%-22s | %-31s | %-31s\n",
                "Registration Plate",
                v1.getRegistrationNumber(),
                v2.getRegistrationNumber());

        System.out.printf("%-22s | $%-30.2f | $%-30.2f\n",
                "Daily Rate",
                v1.getPricePerDay(),
                v2.getPricePerDay());

        System.out.printf("%-22s | %-31s | %-31s\n",
                "Customer Rating",
                String.format("%.1f / 5.0", v1.getRating()),
                String.format("%.1f / 5.0", v2.getRating()));

        System.out.printf("%-22s | %-31s | %-31s\n",
                "Rental Availability",
                v1Status,
                v2Status);

        System.out.printf("%-22s | %-31s | %-31s\n",
                "Class Specifications",
                v1Details,
                v2Details);

        System.out.println("------------------------------------------------------------------------------------------");

        if (v1.getPricePerDay() < v2.getPricePerDay()) {
            double diff = v2.getPricePerDay() - v1.getPricePerDay();

            System.out.printf("* Price Advantage : [%s] %s is $%.2f/day cheaper than [%s] %s.\n",
                    v1.getVehicleId(),
                    v1.getName(),
                    diff,
                    v2.getVehicleId(),
                    v2.getName());

        } else if (v2.getPricePerDay() < v1.getPricePerDay()) {
            double diff = v1.getPricePerDay() - v2.getPricePerDay();

            System.out.printf("* Price Advantage : [%s] %s is $%.2f/day cheaper than [%s] %s.\n",
                    v2.getVehicleId(),
                    v2.getName(),
                    diff,
                    v1.getVehicleId(),
                    v1.getName());

        } else {
            System.out.println("* Price Advantage : Both vehicles have the same daily rental rate ($"
                    + v1.getPricePerDay() + ").");
        }

        System.out.println("==========================================================================================\n");
    }

    private String getVehicleSpecificationSummary(Vehicle v) {
        if (v instanceof Car) {
            Car car = (Car) v;

            return "Seats: " + car.getSeatingCapacity()
                    + ", Fuel: " + car.getFuelType();

        } else if (v instanceof Bike) {
            Bike bike = (Bike) v;

            String helmetText = bike.isHelmetIncluded() ? "Yes" : "No";

            return bike.getEngineCapacityCc() + "cc, Helmet: " + helmetText;
        }

        return "Standard Vehicle";
    }

    public void recordRecentlyViewed(Vehicle vehicle) {
        if (vehicle == null) {
            return;
        }

        for (int i = 0; i < recentlyViewedVehicles.size(); i++) {
            if (recentlyViewedVehicles.get(i).getVehicleId()
                    .equalsIgnoreCase(vehicle.getVehicleId())) {

                recentlyViewedVehicles.remove(i);
                break;
            }
        }

        recentlyViewedVehicles.addFirst(vehicle);

        if (recentlyViewedVehicles.size() > MAX_RECENTLY_VIEWED) {
            recentlyViewedVehicles.removeLast();
        }
    }

    public List<Vehicle> getRecentlyViewedVehicles() {
        return Collections.unmodifiableList(this.recentlyViewedVehicles);
    }

    public void displayVehiclesSortedByPriceAsc() {
        List<Vehicle> copy = new ArrayList<>(this.vehicles);

        copy.sort(new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle v1, Vehicle v2) {
                return Double.compare(v1.getPricePerDay(), v2.getPricePerDay());
            }
        });

        displaySortedTable("Vehicles Sorted by Price: Lowest to Highest (Comparator)", copy);
    }

    public void displayVehiclesSortedByPriceDesc() {
        List<Vehicle> copy = new ArrayList<>(this.vehicles);

        copy.sort(new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle v1, Vehicle v2) {
                return Double.compare(v2.getPricePerDay(), v1.getPricePerDay());
            }
        });

        displaySortedTable("Vehicles Sorted by Price: Highest to Lowest (Comparator)", copy);
    }

    public void displayVehiclesSortedByName() {
        List<Vehicle> copy = new ArrayList<>(this.vehicles);

        copy.sort(new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle v1, Vehicle v2) {
                return v1.getName().compareToIgnoreCase(v2.getName());
            }
        });

        displaySortedTable("Vehicles Sorted by Name: Alphabetical Order (Comparator)", copy);
    }

    public void displayVehiclesSortedByRatingDesc() {
        List<Vehicle> copy = new ArrayList<>(this.vehicles);

        copy.sort(new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle v1, Vehicle v2) {
                return Double.compare(v2.getRating(), v1.getRating());
            }
        });

        displaySortedTable("Vehicles Sorted by Customer Rating: Highest First (Comparator)", copy);
    }

    private void displaySortedTable(String title, List<Vehicle> list) {
        System.out.println("\n==================== " + title + " ====================");

        System.out.printf("%-6s | %-24s | %-10s | %-10s | %-6s | %-12s\n",
                "ID", "Brand & Model", "Category", "Price/Day", "Rating", "Status");

        System.out.println("----------------------------------------------------------------------------------");

        for (Vehicle v : list) {
            String status = v.isAvailable() ? "Available" : "Unavailable";

            System.out.printf("%-6s | %-24s | %-10s | $%-9.2f | %-6.1f | %-12s\n",
                    v.getVehicleId(),
                    v.getName(),
                    v.getCategory(),
                    v.getPricePerDay(),
                    v.getRating(),
                    status);
        }

        System.out.println("==================================================================================\n");
    }

    

    public List<Vehicle> getAllVehiclesFromDatabase() throws SQLException {
        List<Vehicle> list = new ArrayList<>();

        String sql = "SELECT * FROM vehicles ORDER BY vehicle_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToVehicle(rs));
            }
        }

        return list;
    }

    public void loadVehiclesFromDatabase() throws SQLException {
        List<Vehicle> databaseVehicles = getAllVehiclesFromDatabase();

        vehicles.clear();
        vehicleMap.clear();

        for (Vehicle vehicle : databaseVehicles) {
            vehicles.add(vehicle);

            vehicleMap.put(
                    vehicle.getVehicleId().trim().toUpperCase(),
                    vehicle
            );
        }

        System.out.println("Vehicles loaded from database: " + vehicles.size());
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        String id = rs.getString("vehicle_id");
        String brand = rs.getString("brand");
        String model = rs.getString("model");
        String reg = rs.getString("registration_number");

        double price = rs.getDouble("price_per_day");
        double rating = rs.getDouble("rating");

        boolean avail = "AVAILABLE".equalsIgnoreCase(
                rs.getString("availability_status")
        );

        String type = rs.getString("vehicle_type");

        if ("Bike".equalsIgnoreCase(type)) {
            int cc = rs.getInt("engine_capacity_cc");
            boolean helmet = rs.getBoolean("helmet_included");

            return new Bike(
                    id, brand, model, reg, price, avail, cc, helmet, rating
            );

        } else {
            int seats = rs.getInt("seating_capacity");
            String fuel = rs.getString("fuel_type");

            return new Car(
                    id, brand, model, reg, price, avail, seats, fuel, rating
            );
        }
    }
}