package com.rentiq;

import com.rentiq.exception.InvalidBookingException;
import com.rentiq.exception.InvalidUserException;
import com.rentiq.exception.VehicleNotAvailableException;
import com.rentiq.model.Bike;
import com.rentiq.model.Car;
import com.rentiq.model.Customer;
import com.rentiq.model.Vehicle;
import com.rentiq.service.BookingService;
import com.rentiq.service.UserService;
import com.rentiq.service.VehicleService;
import com.rentiq.ui.console.ConsoleApp;
import java.util.Scanner;

/**
 * Main application class for RentIQ Vehicle Rental Management System.
 * Integrates all domain models, services, custom exceptions, String handling demonstrations,
 * Wrapper class usage, and Console UI.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("   RentIQ - Vehicle Rental System (Commit 4 Finalization)   ");
        System.out.println("============================================================\n");

        // 1. Initialize Core Services
        UserService userService = new UserService();
        VehicleService vehicleService = new VehicleService();
        BookingService bookingService = new BookingService();

        // 2. Pre-seed fleet vehicles
        Vehicle v1 = new Car("V101", "Tesla", "Model 3", "TS-01-EV", 85.0, true, 5, "Electric");
        Vehicle v2 = new Car("V102", "Toyota", "RAV4", "TY-02-SUV", 65.0, true, 5, "Hybrid");
        Vehicle v3 = new Bike("B201", "Yamaha", "YZF-R3", "YM-01-BK", 45.0, true, 321, true);
        Vehicle v4 = new Bike("B202", "Harley-Davidson", "Iron 883", "HD-02-BK", 75.0, true, 883, true);

        vehicleService.addVehicle(v1);
        vehicleService.addVehicle(v2);
        vehicleService.addVehicle(v3);
        vehicleService.addVehicle(v4);

        // Pre-seed sample users
        try {
            userService.registerCustomer("Alice Johnson", "alice@example.com", "+1-555-0101", "pass123", "DL-98765-XYZ", "Gold VIP");
            userService.registerCustomer("Bob Smith", "bob@example.com", "+1-555-0102", "pass456", "DL-12345-ABC", "Standard");
            userService.registerAdmin("David Miller", "admin@rentiq.com", "+1-555-0999", "adminPass", "Fleet Management", "Operations Lead");
        } catch (InvalidUserException e) {
            System.out.println("[Init Error] " + e.getMessage());
        }

        // ------------------------------------------------------------
        // SECTION A: Core Java String Handling & Wrapper Demonstrations
        // ------------------------------------------------------------
        demonstrateStringHandling();
        demonstrateWrapperClasses();

        // ------------------------------------------------------------
        // SECTION B: Exception Handling Demonstration (Try-Catch-Finally)
        // ------------------------------------------------------------
        demonstrateExceptionHandling(userService, vehicleService, bookingService);

        // ------------------------------------------------------------
        // SECTION C: Console Interface Execution
        // ------------------------------------------------------------
        // When running in an automated or piped environment, simulate complete console menu operations (1 through 8)
        System.out.println("\n>>> Launching Console Application Demonstration <<<");
        String simulatedConsoleInput =
            "1\n" +                                   // 1. Register User
            "Charlie Davis\n" +                       // Name
            "charlie@rentiq-demo.com\n" +              // Email
            "+1-555-0199\n" +                         // Phone
            "charliePass\n" +                         // Password
            "DL-55443-CD\n" +                         // License
            "Platinum VIP\n" +                        // Membership
            "2\n" +                                   // 2. View Users
            "3\n" +                                   // 3. View Vehicles
            "4\n" +                                   // 4. Search Vehicles
            "Tesla\n" +                               // Keyword
            "5\n" +                                   // 5. Book Vehicle
            "U1001\n" +                               // Alice User ID
            "V101\n" +                                // Tesla Model 3
            "5\n" +                                   // 5 days
            "6\n" +                                   // 6. View Bookings
            "7\n" +                                   // 7. Cancel Booking
            "BK-1001\n" +                             // Booking ID
            "6\n" +                                   // View Bookings again
            "99\n" +                                  // Invalid menu selection test
            "abc\n" +                                 // Invalid numeric input test
            "8\n";                                    // 8. Exit

        Scanner consoleScanner = new Scanner(simulatedConsoleInput);
        ConsoleApp consoleApp = new ConsoleApp(userService, vehicleService, bookingService, consoleScanner);
        consoleApp.start();

        System.out.println("\n============================================================");
        System.out.println("   RentIQ Phase 1 Commit 4 Completed Successfully!          ");
        System.out.println("============================================================");
    }

    /**
     * Demonstrates String immutability, comparisons (equals, equalsIgnoreCase, ==),
     * StringBuilder, and String formatting.
     */
    private static void demonstrateStringHandling() {
        System.out.println("\n>>> 1. String Handling Demonstrations:");

        // 1. String Immutability
        String baseStr = "RentIQ";
        String modifiedStr = baseStr.concat(" - Java Edition");
        System.out.println("Original String (immutable) : " + baseStr);
        System.out.println("Result after concatenation  : " + modifiedStr);

        // 2. String comparison: equals() vs equalsIgnoreCase() vs == (reference comparison)
        String s1 = "Car";
        String s2 = new String("Car");
        String s3 = "car";

        System.out.println("s1 == s2 (reference check)  : " + (s1 == s2) + " [Different memory references]");
        System.out.println("s1.equals(s2) (content)     : " + s1.equals(s2) + " [Identical content]");
        System.out.println("s1.equalsIgnoreCase(s3)     : " + s1.equalsIgnoreCase(s3) + " [Case-insensitive match]");

        // 3. StringBuilder demonstration
        StringBuilder sb = new StringBuilder("Vehicle Fleet: ");
        sb.append("Sedans").append(", ").append("SUVs").append(", ").append("Motorcycles");
        System.out.println("StringBuilder output        : " + sb.toString());

        // 4. String formatting
        String formatted = String.format("Summary: User count = %d, System status = %s, Currency = %s", 3, "OPERATIONAL", "USD");
        System.out.println("String.format output        : " + formatted);
    }

    /**
     * Demonstrates Wrapper classes (Integer, Double, Boolean), string parsing,
     * autoboxing, and unboxing.
     */
    private static void demonstrateWrapperClasses() {
        System.out.println("\n>>> 2. Wrapper Classes Demonstrations (Integer, Double, Boolean):");

        // 1. Parsing numeric strings
        String daysStr = "7";
        String rateStr = "89.50";
        String flagStr = "true";

        int parsedDays = Integer.parseInt(daysStr);
        double parsedRate = Double.parseDouble(rateStr);
        boolean parsedFlag = Boolean.parseBoolean(flagStr);

        System.out.println("Parsed int from '" + daysStr + "'      : " + parsedDays);
        System.out.println("Parsed double from '" + rateStr + "' : " + parsedRate);
        System.out.println("Parsed boolean from '" + flagStr + "' : " + parsedFlag);

        // 2. Autoboxing: Primitive to Wrapper Object
        Integer boxedDays = parsedDays;     // Autoboxing
        Double boxedRate = parsedRate;      // Autoboxing
        Boolean boxedFlag = parsedFlag;     // Autoboxing

        System.out.println("Boxed Integer object        : " + boxedDays.getClass().getName() + " -> " + boxedDays);
        System.out.println("Boxed Double object         : " + boxedRate.getClass().getName() + " -> " + boxedRate);
        System.out.println("Boxed Boolean object        : " + boxedFlag.getClass().getName() + " -> " + boxedFlag);

        // 3. Unboxing: Wrapper Object to Primitive
        int unboxedDays = boxedDays;        // Unboxing
        double unboxedRate = boxedRate;     // Unboxing
        double calculatedTotal = unboxedDays * unboxedRate;
        System.out.println("Calculated Total via unbox  : $" + String.format("%.2f", calculatedTotal));
    }

    /**
     * Demonstrates custom exception handling with try-catch-finally blocks.
     */
    private static void demonstrateExceptionHandling(UserService userService, VehicleService vehicleService, BookingService bookingService) {
        System.out.println("\n>>> 3. Exception Handling Demonstrations (try-catch-finally):");

        // Test A: InvalidUserException (malformed email)
        System.out.println("\n--- Test A: Handling InvalidUserException (invalid email) ---");
        try {
            userService.registerCustomer("Invalid User", "not-an-email", "+1-555-0000", "pass", "DL-999", "Standard");
        } catch (InvalidUserException e) {
            System.out.println("[Caught InvalidUserException] " + e.getMessage());
        } finally {
            System.out.println("[Finally Block A] User validation attempted.");
        }

        // Test B: InvalidBookingException (invalid duration 0 days)
        System.out.println("\n--- Test B: Handling InvalidBookingException (0 days duration) ---");
        Customer customer = (Customer) userService.findUserById("U1001");
        Vehicle vehicle = vehicleService.searchVehicleById("V101");
        try {
            bookingService.createBooking(customer, vehicle, 0);
        } catch (InvalidBookingException e) {
            System.out.println("[Caught InvalidBookingException] " + e.getMessage());
        } catch (VehicleNotAvailableException e) {
            System.out.println("[Caught VehicleNotAvailableException] " + e.getMessage());
        } finally {
            System.out.println("[Finally Block B] Duration validation check completed.");
        }

        // Test C: VehicleNotAvailableException (booking an already rented vehicle)
        System.out.println("\n--- Test C: Handling VehicleNotAvailableException (double booking) ---");
        try {
            // First booking: succeeds
            bookingService.createBooking(customer, vehicle, 3);
            // Second booking on same vehicle: fails with VehicleNotAvailableException
            Customer customerBob = (Customer) userService.findUserById("U1002");
            bookingService.createBooking(customerBob, vehicle, 2);
        } catch (VehicleNotAvailableException e) {
            System.out.println("[Caught VehicleNotAvailableException] " + e.getMessage());
        } catch (InvalidBookingException e) {
            System.out.println("[Caught InvalidBookingException] " + e.getMessage());
        } finally {
            System.out.println("[Finally Block C] Availability check completed.");
        }
    }
}
