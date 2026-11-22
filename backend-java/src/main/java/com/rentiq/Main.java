package com.rentiq;

import com.rentiq.model.Admin;
import com.rentiq.model.Bike;
import com.rentiq.model.Car;
import com.rentiq.model.Customer;
import com.rentiq.model.User;
import com.rentiq.model.Vehicle;
import com.rentiq.service.VehicleService;

public class Main {
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("   RentIQ - Vehicle Rental System (Inheritance & Polymorphism) ");
        System.out.println("============================================================\n");

        // ------------------------------------------------------------
        // 1. User Inheritance and Runtime Polymorphism
        // ------------------------------------------------------------
        System.out.println(">>> 1. Demonstrating User Roles & Runtime Polymorphism:");

        // Polymorphic references: Parent type (User) referring to Child objects (Customer, Admin)
        User customerUser = new Customer("C001", "Alice Johnson", "alice@example.com", "pass123", "+1-555-0101", "DL-98765-XYZ", "Gold VIP");
        User adminUser = new Admin("A001", "David Miller", "david.admin@rentiq.com", "root456", "+1-555-0999", "Fleet Operations", "Super Admin");

        // Demonstrating runtime polymorphism via array of parent type
        User[] users = new User[] { customerUser, adminUser };
        for (int i = 0; i < users.length; i++) {
            // JVM resolves the overridden displayUserInfo() method dynamically at runtime
            users[i].displayUserInfo();
        }

        // Child-specific method demonstration
        if (adminUser instanceof Admin) {
            ((Admin) adminUser).displayAdminPrivileges();
        }

        System.out.println("\n------------------------------------------------------------\n");

        // ------------------------------------------------------------
        // 2. Vehicle Inheritance and Runtime Polymorphism
        // ------------------------------------------------------------
        System.out.println(">>> 2. Demonstrating Vehicle Inheritance & Polymorphism:");

        // Parent references holding Car and Bike objects
        Vehicle sedanCar = new Car("V101", "Tesla", "Model 3", "TS-01-EV", 85.0, true, 5, "Electric");
        Vehicle suvCar = new Car("V102", "Toyota", "RAV4", "TY-02-SUV", 65.0, true, 5, "Hybrid");
        Vehicle sportsBike = new Bike("B201", "Yamaha", "YZF-R3", "YM-01-BK", 45.0, true, 321, true);
        Vehicle cruiserBike = new Bike("B202", "Harley-Davidson", "Iron 883", "HD-02-BK", 75.0, false, 883, true);

        // Demonstrating runtime polymorphism with Vehicle array
        Vehicle[] sampleVehicles = new Vehicle[] { sedanCar, sportsBike };
        System.out.println("Displaying vehicles via polymorphic parent array:");
        for (int i = 0; i < sampleVehicles.length; i++) {
            sampleVehicles[i].displayVehicleDetails();
        }

        System.out.println("\n------------------------------------------------------------\n");

        // ------------------------------------------------------------
        // 3. Vehicle Management Operations via VehicleService (Array-based)
        // ------------------------------------------------------------
        System.out.println(">>> 3. Demonstrating VehicleService Operations:");
        VehicleService vehicleService = new VehicleService();

        // Operation a: Add vehicles to the service
        System.out.println("\n--- Adding Vehicles to Fleet ---");
        vehicleService.addVehicle(sedanCar);
        vehicleService.addVehicle(suvCar);
        vehicleService.addVehicle(sportsBike);
        vehicleService.addVehicle(cruiserBike);

        // Operation b: Display all registered vehicles
        vehicleService.displayAllVehicles();

        // Operation c: Search vehicle by ID
        System.out.println("--- Searching Vehicle by ID ---");
        String searchId = "V101";
        System.out.println("Searching for vehicle ID: " + searchId);
        Vehicle foundVehicle = vehicleService.searchVehicleById(searchId);
        if (foundVehicle != null) {
            System.out.println("Found vehicle: " + foundVehicle.getBrand() + " " + foundVehicle.getModel());
            foundVehicle.displayVehicleDetails();
        } else {
            System.out.println("Vehicle with ID " + searchId + " not found.");
        }

        // Operation d: Update vehicle availability (Rental transaction)
        System.out.println("\n--- Updating Vehicle Availability (Renting & Returning) ---");
        System.out.println("Renting out V101 (Tesla Model 3)...");
        vehicleService.updateVehicleAvailability("V101", false);

        System.out.println("\nReturning B202 (Harley-Davidson Iron 883)...");
        vehicleService.updateVehicleAvailability("B202", true);

        // Operation e: Display vehicle details for updated vehicles
        System.out.println("\n--- Displaying Updated Vehicle Details ---");
        vehicleService.displayVehicleDetails("V101");
        vehicleService.displayVehicleDetails("B202");

        System.out.println("============================================================");
        System.out.println("   RentIQ Phase 1 Commit 2 Demonstration Completed Successfully ");
        System.out.println("============================================================");
    }
}
