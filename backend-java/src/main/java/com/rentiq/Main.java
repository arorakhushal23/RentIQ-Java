package com.rentiq;

import com.rentiq.model.User;
import com.rentiq.model.Vehicle;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Welcome to RentIQ - Vehicle Rental System      ");
        System.out.println("==================================================\n");

        // 1. Demonstrate User objects
        System.out.println(">>> Creating and Displaying Users:");
        User user1 = new User("U001", "Alice Johnson", "alice@example.com", "pass123", "+1-555-0101");
        User user2 = new User("U002", "Bob Smith", "bob@example.com", "secure456", "+1-555-0102");

        user1.displayUserInfo();
        user2.displayUserInfo();

        System.out.println();

        // 2. Demonstrate Vehicle objects
        System.out.println(">>> Creating and Displaying Vehicles:");
        Vehicle vehicle1 = new Vehicle("V001", "Tesla", "Model 3", "Electric Sedan", "TS-01-EV", 85.0, true);
        Vehicle vehicle2 = new Vehicle("V002", "Toyota", "RAV4", "SUV", "TY-02-SUV", 60.0, true);
        Vehicle vehicle3 = new Vehicle("V003", "Ford", "Mustang", "Sports Car", "FD-03-SPT", 120.0, false);

        vehicle1.displayVehicleDetails();
        vehicle2.displayVehicleDetails();
        vehicle3.displayVehicleDetails();

        System.out.println();

        // 3. Demonstrate updating vehicle availability
        System.out.println(">>> Updating Vehicle Availability:");
        System.out.println("Renting Vehicle V001 (Tesla Model 3)...");
        vehicle1.updateAvailability(false);
        System.out.println("Updated status for V001:");
        vehicle1.displayVehicleDetails();

        System.out.println("Returning Vehicle V003 (Ford Mustang)...");
        vehicle3.updateAvailability(true);
        System.out.println("Updated status for V003:");
        vehicle3.displayVehicleDetails();

        System.out.println("==================================================");
        System.out.println("   RentIQ Phase 1 Demonstration Completed         ");
        System.out.println("==================================================");
    }
}
