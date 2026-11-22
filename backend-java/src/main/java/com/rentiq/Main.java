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
import com.rentiq.util.CollectionDemo;
import java.util.Scanner;

/**
 * Main application class for RentIQ Vehicle Rental Management System.
 * Integrates domain models, services backed by Java Collections Framework,
 * custom exceptions, String handling demonstrations, Wrapper class usage,
 * Collection demonstrations, and Console UI.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("   RentIQ - Vehicle Rental System (Phase 2, Milestone 1)    ");
        System.out.println("============================================================\n");

        // 1. Initialize Core Services (powered by Java Collections)
        UserService userService = new UserService();
        VehicleService vehicleService = new VehicleService();
        BookingService bookingService = new BookingService();

        // 2. Pre-seed fleet vehicles
        // Note: Adding 12 vehicles exceeds previous array capacity (10), verifying dynamic ArrayList growth
        Vehicle v1 = new Car("V101", "Tesla", "Model 3", "TS-01-EV", 85.0, true, 5, "Electric");
        Vehicle v2 = new Car("V102", "Toyota", "RAV4", "TY-02-SUV", 65.0, true, 5, "Hybrid");
        Vehicle v3 = new Bike("B201", "Yamaha", "YZF-R3", "YM-01-BK", 45.0, true, 321, true);
        Vehicle v4 = new Bike("B202", "Harley-Davidson", "Iron 883", "HD-02-BK", 75.0, true, 883, true);
        Vehicle v5 = new Car("V103", "Honda", "Civic", "HN-03-SD", 55.0, true, 5, "Gasoline");
        Vehicle v6 = new Car("V104", "BMW", "3 Series", "BM-04-SD", 95.0, true, 5, "Gasoline");
        Vehicle v7 = new Bike("B203", "Kawasaki", "Ninja 400", "KW-03-BK", 50.0, true, 399, true);
        Vehicle v8 = new Car("V105", "Ford", "Mustang Mach-E", "FD-05-EV", 90.0, true, 5, "Electric");
        Vehicle v9 = new Car("V106", "Audi", "Q5", "AD-06-SUV", 80.0, true, 5, "Gasoline");
        Vehicle v10 = new Bike("B204", "Ducati", "Scrambler", "DC-04-BK", 85.0, true, 803, true);
        // Exceeding previous fixed-size capacity of 10:
        Vehicle v11 = new Car("V107", "Mercedes-Benz", "C300", "MB-07-SD", 100.0, true, 5, "Gasoline");
        Vehicle v12 = new Bike("B205", "Triumph", "Bonneville T100", "TR-05-BK", 70.0, true, 900, true);

        vehicleService.addVehicle(v1);
        vehicleService.addVehicle(v2);
        vehicleService.addVehicle(v3);
        vehicleService.addVehicle(v4);
        vehicleService.addVehicle(v5);
        vehicleService.addVehicle(v6);
        vehicleService.addVehicle(v7);
        vehicleService.addVehicle(v8);
        vehicleService.addVehicle(v9);
        vehicleService.addVehicle(v10);
        vehicleService.addVehicle(v11);
        vehicleService.addVehicle(v12);

        System.out.println("[Fleet Setup] Successfully seeded " + vehicleService.getVehicleCount() + " vehicles into dynamic ArrayList fleet.");

        // Pre-seed sample users
        try {
            userService.registerCustomer("Alice Johnson", "alice@example.com", "+1-555-0101", "pass123", "DL-98765-XYZ", "Gold VIP");
            userService.registerCustomer("Bob Smith", "bob@example.com", "+1-555-0102", "pass456", "DL-12345-ABC", "Standard");
            userService.registerAdmin("David Miller", "admin@rentiq.com", "+1-555-0999", "adminPass", "Fleet Management", "Operations Lead");
        } catch (InvalidUserException e) {
            System.out.println("[Init Error] " + e.getMessage());
        }

        // ------------------------------------------------------------
        // SECTION A: Core Java String Handling & Wrapper Demonstrations (Phase 1 Preserved)
        // ------------------------------------------------------------
        demonstrateStringHandling();
        demonstrateWrapperClasses();

        // ------------------------------------------------------------
        // SECTION B: Exception Handling Demonstration (Phase 1 Preserved)
        // ------------------------------------------------------------
        demonstrateExceptionHandling(userService, vehicleService, bookingService);

        // ------------------------------------------------------------
        // SECTION C: Java Collections Framework Showcase (Phase 2 Milestone 1)
        // ------------------------------------------------------------
        CollectionDemo.runAllDemos();

        // Demonstrate Set & LinkedList behaviors with current fleet
        System.out.println(">>> Dynamic Vehicle Fleet Category Analysis (Set Implementations):");
        vehicleService.displayCategoryOverview();

        // Simulate viewing vehicles to populate LinkedList (Recently Viewed)
        vehicleService.displayVehicleDetails("V101");
        vehicleService.displayVehicleDetails("B201");
        vehicleService.displayVehicleDetails("V102");
        vehicleService.displayRecentlyViewedVehicles();

        // ------------------------------------------------------------
        // SECTION D: Console Interface Execution (Automated Simulation)
        // ------------------------------------------------------------
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
            "V101\n" +                                // View details & add to recently viewed
            "5\n" +                                   // 5. Book Vehicle
            "U1001\n" +                               // Alice User ID
            "V101\n" +                                // Tesla Model 3
            "5\n" +                                   // 5 days
            "6\n" +                                   // 6. View Bookings
            "7\n" +                                   // 7. Cancel Booking
            "BK-1001\n" +                             // Booking ID
            "8\n" +                                   // 8. View Recently Viewed Vehicles (LinkedList)
            "\n" +                                    // Skip removing by ID
            "9\n" +                                   // 9. View Unique Categories (HashSet & LinkedHashSet)
            "10\n" +                                  // 10. View Sorted Categories (TreeSet)
            "11\n" +                                  // 11. Run Collections Demo
            "99\n" +                                  // Invalid menu selection test
            "abc\n" +                                 // Invalid numeric input test
            "12\n";                                   // 12. Exit

        Scanner consoleScanner = new Scanner(simulatedConsoleInput);
        ConsoleApp consoleApp = new ConsoleApp(userService, vehicleService, bookingService, consoleScanner);
        consoleApp.start();

        System.out.println("\n============================================================");
        System.out.println("   RentIQ Phase 2 Milestone 1 Completed Successfully!       ");
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
        Integer boxedDays = parsedDays;
        Double boxedRate = parsedRate;
        Boolean boxedFlag = parsedFlag;

        System.out.println("Boxed Integer object        : " + boxedDays.getClass().getName() + " -> " + boxedDays);
        System.out.println("Boxed Double object         : " + boxedRate.getClass().getName() + " -> " + boxedRate);
        System.out.println("Boxed Boolean object        : " + boxedFlag.getClass().getName() + " -> " + boxedFlag);

        // 3. Unboxing: Wrapper Object to Primitive
        int unboxedDays = boxedDays;
        double unboxedRate = boxedRate;
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
