package com.rentiq;

import com.rentiq.exception.InvalidBookingException;
import com.rentiq.exception.InvalidUserException;
import com.rentiq.exception.VehicleNotAvailableException;
import com.rentiq.model.Bike;
import com.rentiq.model.Booking;
import com.rentiq.model.BookingRequest;
import com.rentiq.model.Car;
import com.rentiq.model.Customer;
import com.rentiq.model.User;
import com.rentiq.model.Vehicle;
import com.rentiq.service.BookingService;
import com.rentiq.service.UserService;
import com.rentiq.service.VehicleService;
import com.rentiq.ui.console.ConsoleApp;
import com.rentiq.util.CollectionDemo;
import java.util.Scanner;

/**
 * Main application class for RentIQ Vehicle Rental Management System.
 * Fully integrates:
 * - Domain Models (User, Customer, Admin, Vehicle, Car, Bike, Booking, BookingRequest)
 * - Java Collections Framework (ArrayList, LinkedList, HashSet, LinkedHashSet, TreeSet)
 * - Map Collections (HashMap, LinkedHashMap, TreeMap)
 * - Queue Collections (Queue FIFO, Deque ArrayDeque, PriorityQueue)
 * - Iterators (Iterator safe removal, ListIterator bidirectional traversal)
 * - Sorting (Comparable natural order, Comparator multi-criteria)
 * - Interactive and Automated Console Application
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("   RentIQ - Vehicle Rental System (Phase 2, Milestone 2)    ");
        System.out.println("   Maps, Queues, Iterators & Multi-Criteria Sorting         ");
        System.out.println("============================================================\n");

        // 1. Initialize Core Services
        UserService userService = new UserService();
        VehicleService vehicleService = new VehicleService();
        BookingService bookingService = new BookingService();

        // 2. Pre-seed fleet vehicles with diverse prices and ratings
        // Vehicles implement Comparable<Vehicle> (natural ordering by ID)
        Vehicle v1  = new Car("V101", "Tesla", "Model 3", "TS-01-EV", 85.0, true, 5, "Electric", 4.9);
        Vehicle v2  = new Car("V102", "Toyota", "RAV4", "TY-02-SUV", 65.0, true, 5, "Hybrid", 4.6);
        Vehicle v3  = new Bike("B201", "Yamaha", "YZF-R3", "YM-01-BK", 45.0, true, 321, true, 4.7);
        Vehicle v4  = new Bike("B202", "Harley-Davidson", "Iron 883", "HD-02-BK", 75.0, true, 883, true, 4.8);
        Vehicle v5  = new Car("V103", "Honda", "Civic", "HN-03-SD", 55.0, true, 5, "Gasoline", 4.4);
        Vehicle v6  = new Car("V104", "BMW", "3 Series", "BM-04-SD", 95.0, true, 5, "Gasoline", 4.8);
        Vehicle v7  = new Bike("B203", "Kawasaki", "Ninja 400", "KW-03-BK", 50.0, true, 399, true, 4.5);
        Vehicle v8  = new Car("V105", "Ford", "Mustang Mach-E", "FD-05-EV", 90.0, true, 5, "Electric", 4.7);
        Vehicle v9  = new Car("V106", "Audi", "Q5", "AD-06-SUV", 80.0, true, 5, "Gasoline", 4.6);
        Vehicle v10 = new Bike("B204", "Ducati", "Scrambler", "DC-04-BK", 85.0, true, 803, true, 4.9);
        Vehicle v11 = new Car("V107", "Mercedes-Benz", "C300", "MB-07-SD", 100.0, true, 5, "Gasoline", 4.9);
        Vehicle v12 = new Bike("B205", "Triumph", "Bonneville T100", "TR-05-BK", 70.0, true, 900, true, 4.7);

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

        // Verify Duplicate ID Prevention in HashMap
        System.out.println("\n>>> Testing Duplicate ID Rejection:");
        boolean dupAdded = vehicleService.addVehicle(new Car("V101", "Duplicate", "Tesla", "DUP-01", 99.0, true, 5, "Electric", 1.0));
        System.out.println("Duplicate Vehicle ID 'V101' rejected successfully: " + (!dupAdded));

        // Pre-seed sample users (Regular and VIP customers, plus Admin)
        Customer alice = null;
        Customer bob = null;
        Customer elena = null;
        Customer carlos = null;
        try {
            alice = userService.registerCustomer("Alice Johnson", "alice@example.com", "+1-555-0101", "pass123", "DL-98765-XYZ", "Platinum VIP");
            bob = userService.registerCustomer("Bob Smith", "bob@example.com", "+1-555-0102", "pass456", "DL-12345-ABC", "Standard");
            elena = userService.registerCustomer("Elena Rostova", "elena@example.com", "+1-555-0103", "pass789", "DL-77889-ER", "Gold VIP");
            carlos = userService.registerCustomer("Carlos Mendez", "carlos@example.com", "+1-555-0104", "pass321", "DL-33445-CM", "Standard");
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
        // SECTION C: Full Collections Framework Showcase (Lectures 16–28)
        // ------------------------------------------------------------
        CollectionDemo.runAllDemos();

        // ------------------------------------------------------------
        // SECTION D: Live Demonstration of Milestone 2 Core Features
        // ------------------------------------------------------------
        demonstrateMilestone2Features(userService, vehicleService, bookingService, alice, bob, elena, carlos);

        // ------------------------------------------------------------
        // SECTION E: Console Application Automated Demonstration
        // ------------------------------------------------------------
        System.out.println("\n>>> Launching Console Application Demonstration <<<");
        String simulatedConsoleInput =
            // 1. Main menu option 12: Enter Milestone 2 Submenu
            "12\n" +
            "1\n" + "U1001\n" +                        // Submenu 1: Search User by ID (HashMap)
            "2\n" + "V101\n" +                         // Submenu 2: Search Vehicle by ID (HashMap)
            "3\n" + "BK-1001\n" +                      // Submenu 3: Search Booking by ID (HashMap)
            "4\n" +                                    // Submenu 4: Display LinkedHashMap records
            "5\n" +                                    // Submenu 5: Display TreeMap records
            "6\n" + "U1001\n" + "V102\n" + "3\n" +      // Submenu 6: Submit Pending Request (FIFO)
            "7\n" +                                    // Submenu 7: Display Pending Requests (FIFO)
            "8\n" +                                    // Submenu 8: Process Next FIFO Request
            "9\n" +                                    // Submenu 9: Demonstrate Deque operations
            "10\n" +                                   // Submenu 10: Demonstrate PriorityQueue processing
            "11\n" +                                   // Submenu 11: Demonstrate Iterator and ListIterator
            "12\n" +                                   // Submenu 12: Sort Vehicles using Comparable
            "13\n" + "1\n" +                           // Submenu 13: Sort by Price Ascending
            "14\n" +                                   // Submenu 14: Return to Main Menu
            // 2. Main menu operations
            "8\n" + "\n" +                             // 8. View Recently Viewed Vehicles (LinkedList)
            "9\n" +                                    // 9. View Unique Categories (HashSet/LinkedHashSet)
            "10\n" +                                   // 10. View Sorted Categories (TreeSet)
            "99\n" +                                   // Invalid input test
            "13\n";                                    // 13. Exit

        Scanner consoleScanner = new Scanner(simulatedConsoleInput);
        ConsoleApp consoleApp = new ConsoleApp(userService, vehicleService, bookingService, consoleScanner);
        consoleApp.start();

        System.out.println("\n============================================================");
        System.out.println("   RentIQ Phase 2 Milestone 2 Completed Successfully!       ");
        System.out.println("============================================================");
    }

    /**
     * Deep programmatic verification and demonstration of Milestone 2 features:
     * - HashMap O(1) lookup
     * - LinkedHashMap insertion order preservation
     * - TreeMap sorted ID ordering
     * - Queue FIFO request scheduling and empty queue handling
     * - Deque front/rear operations
     * - PriorityQueue VIP tier scheduling and FIFO sequence tie-breaking
     * - Iterator safe removal and ListIterator bidirectional traversal
     * - Comparable natural sorting
     * - Comparator multi-criteria sorting
     */
    private static void demonstrateMilestone2Features(UserService userService, VehicleService vehicleService,
                                                     BookingService bookingService,
                                                     Customer alice, Customer bob, Customer elena, Customer carlos) {
        System.out.println("\n============================================================");
        System.out.println("   Milestone 2: Programmatic Feature Verification          ");
        System.out.println("============================================================\n");

        // 1. HashMap Lookups
        System.out.println(">>> 1. HashMap O(1) Entity Lookup Verification:");
        User foundUser = userService.findUserById("U1001");
        Vehicle foundVehicle = vehicleService.searchVehicleById("V101");
        System.out.println("HashMap User Lookup: " + (foundUser != null ? foundUser.getName() : "NOT FOUND"));
        System.out.println("HashMap Vehicle Lookup: " + (foundVehicle != null ? foundVehicle.getName() : "NOT FOUND"));

        // 2. LinkedHashMap Insertion-Order Verification
        System.out.println("\n>>> 2. LinkedHashMap Chronological Insertion Order Verification:");
        userService.displayRecentRegistrations();
        vehicleService.displayVehiclesByInsertionOrder();

        // 3. TreeMap Key-Sorted Verification
        System.out.println(">>> 3. TreeMap Alphabetically Sorted Vehicle ID Keys:");
        vehicleService.displayVehiclesSortedById();

        // 4. Queue (FIFO) Verification
        System.out.println(">>> 4. FIFO Queue Pending Booking Requests Verification:");
        Vehicle reqVehicle1 = vehicleService.searchVehicleById("V103");
        Vehicle reqVehicle2 = vehicleService.searchVehicleById("V104");

        // Submit in FIFO order: Bob, then Carlos
        bookingService.submitBookingRequest(bob, reqVehicle1, 2);
        bookingService.submitBookingRequest(carlos, reqVehicle2, 4);

        System.out.println("\nQueue state before processing:");
        bookingService.displayPendingRequests();

        // Process next FIFO request (should process Bob first)
        try {
            Booking processed1 = bookingService.processNextBookingRequest();
            System.out.println("Processed FIFO #1 (Customer: " + processed1.getCustomer().getName() + ", Vehicle: " + processed1.getVehicle().getVehicleId() + ")");
        } catch (Exception e) {
            System.out.println("[Error] " + e.getMessage());
        }

        // 5. Empty Queue Safe Handling
        System.out.println("\n>>> 5. Empty Queue Safe Handling Verification:");
        try {
            // Drain remaining and then test empty poll
            bookingService.processNextBookingRequest(); // Drains Carlos
            Booking emptyPoll = bookingService.processNextBookingRequest(); // Queue is now empty
            System.out.println("Safe poll on empty queue returned null: " + (emptyPoll == null));
        } catch (Exception e) {
            System.out.println("[Error] " + e.getMessage());
        }

        // 6. Deque (ArrayDeque) Operations
        System.out.println("\n>>> 6. Deque (ArrayDeque) Front and Rear Operations Verification:");
        BookingRequest normalReq = new BookingRequest(BookingRequest.generateNextRequestId(), carlos, reqVehicle1, 1);
        BookingRequest expressReq = new BookingRequest(BookingRequest.generateNextRequestId(), alice, reqVehicle2, 3);
        bookingService.addRequestToRear(normalReq);
        bookingService.addRequestToFront(expressReq);
        bookingService.displayDequeRequests();
        BookingRequest polledFront = bookingService.removeRequestFromFront();
        BookingRequest polledRear = bookingService.removeRequestFromRear();
        System.out.println("Polled from front was express request: " + (polledFront == expressReq));
        System.out.println("Polled from rear was standard request: " + (polledRear == normalReq));

        // 7. PriorityQueue (Priority Scoring & Sequence Tie-breaking)
        System.out.println("\n>>> 7. PriorityQueue Membership Tier Verification:");
        Vehicle pVeh1 = vehicleService.searchVehicleById("V105");
        Vehicle pVeh2 = vehicleService.searchVehicleById("V106");
        Vehicle pVeh3 = vehicleService.searchVehicleById("V107");

        // Submit Standard customer first, Gold second, VIP third
        BookingRequest rStandard = new BookingRequest(BookingRequest.generateNextRequestId(), carlos, pVeh1, 2); // Score: 1
        BookingRequest rGold = new BookingRequest(BookingRequest.generateNextRequestId(), elena, pVeh2, 3);      // Score: 2
        BookingRequest rVip = new BookingRequest(BookingRequest.generateNextRequestId(), alice, pVeh3, 5);       // Score: 3

        bookingService.submitPriorityBookingRequest(rStandard);
        bookingService.submitPriorityBookingRequest(rGold);
        bookingService.submitPriorityBookingRequest(rVip);

        System.out.println("\nPriorityQueue drain order (VIP first, then Gold, then Standard):");
        try {
            Booking p1 = bookingService.processNextPriorityRequest(); // Alice (VIP)
            Booking p2 = bookingService.processNextPriorityRequest(); // Elena (Gold)
            Booking p3 = bookingService.processNextPriorityRequest(); // Carlos (Standard)
            System.out.println("First processed: " + p1.getCustomer().getName() + " (" + p1.getCustomer().getMembershipLevel() + ")");
            System.out.println("Second processed: " + p2.getCustomer().getName() + " (" + p2.getCustomer().getMembershipLevel() + ")");
            System.out.println("Third processed: " + p3.getCustomer().getName() + " (" + p3.getCustomer().getMembershipLevel() + ")");
        } catch (Exception e) {
            System.out.println("[PriorityQueue Error] " + e.getMessage());
        }

        // 8. ListIterator Bidirectional Traversal
        System.out.println("\n>>> 8. ListIterator Bidirectional Traversal Verification:");
        vehicleService.traverseVehiclesBidirectional();

        // 9. Iterator Safe Structural Removal
        System.out.println(">>> 9. Iterator Safe Removal of Unavailable Vehicles:");
        // Mark one vehicle unavailable and safely remove it via Iterator
        vehicleService.updateVehicleAvailability("B205", false);
        int removed = vehicleService.removeUnavailableVehiclesWithIterator();
        System.out.println("Safely removed unavailable vehicles count: " + removed);

        // 10. Comparable Natural Sorting
        System.out.println(">>> 10. Comparable Natural Sorting by Vehicle ID:");
        vehicleService.displayVehiclesSortedNaturally();

        // 11. Comparator Sorting Multi-Criteria
        System.out.println(">>> 11. Comparator Multi-Criteria Sorting:");
        vehicleService.displayVehiclesSortedByPriceAsc();
        vehicleService.displayVehiclesSortedByPriceDesc();
        vehicleService.displayVehiclesSortedByName();
        vehicleService.displayVehiclesSortedByRatingDesc();

        System.out.println("============================================================\n");
    }

    private static void demonstrateStringHandling() {
        System.out.println("\n>>> 1. String Handling Demonstrations:");
        String baseStr = "RentIQ";
        String modifiedStr = baseStr.concat(" - Java Edition");
        System.out.println("Original String (immutable) : " + baseStr);
        System.out.println("Result after concatenation  : " + modifiedStr);

        String s1 = "Car";
        String s2 = new String("Car");
        String s3 = "car";
        System.out.println("s1 == s2 (reference check)  : " + (s1 == s2) + " [Different memory references]");
        System.out.println("s1.equals(s2) (content)     : " + s1.equals(s2) + " [Identical content]");
        System.out.println("s1.equalsIgnoreCase(s3)     : " + s1.equalsIgnoreCase(s3) + " [Case-insensitive match]");

        StringBuilder sb = new StringBuilder("Vehicle Fleet: ");
        sb.append("Sedans").append(", ").append("SUVs").append(", ").append("Motorcycles");
        System.out.println("StringBuilder output        : " + sb.toString());

        String formatted = String.format("Summary: User count = %d, System status = %s, Currency = %s", 5, "OPERATIONAL", "USD");
        System.out.println("String.format output        : " + formatted);
    }

    private static void demonstrateWrapperClasses() {
        System.out.println("\n>>> 2. Wrapper Classes Demonstrations (Integer, Double, Boolean):");
        String daysStr = "7";
        String rateStr = "89.50";
        String flagStr = "true";

        int parsedDays = Integer.parseInt(daysStr);
        double parsedRate = Double.parseDouble(rateStr);
        boolean parsedFlag = Boolean.parseBoolean(flagStr);

        System.out.println("Parsed int from '" + daysStr + "'      : " + parsedDays);
        System.out.println("Parsed double from '" + rateStr + "' : " + parsedRate);
        System.out.println("Parsed boolean from '" + flagStr + "' : " + parsedFlag);

        Integer boxedDays = parsedDays;
        Double boxedRate = parsedRate;
        Boolean boxedFlag = parsedFlag;

        System.out.println("Boxed Integer object        : " + boxedDays.getClass().getName() + " -> " + boxedDays);
        System.out.println("Boxed Double object         : " + boxedRate.getClass().getName() + " -> " + boxedRate);
        System.out.println("Boxed Boolean object        : " + boxedFlag.getClass().getName() + " -> " + boxedFlag);

        int unboxedDays = boxedDays;
        double unboxedRate = boxedRate;
        double calculatedTotal = unboxedDays * unboxedRate;
        System.out.println("Calculated Total via unbox  : $" + String.format("%.2f", calculatedTotal));
    }

    private static void demonstrateExceptionHandling(UserService userService, VehicleService vehicleService, BookingService bookingService) {
        System.out.println("\n>>> 3. Exception Handling Demonstrations (try-catch-finally):");

        System.out.println("\n--- Test A: Handling InvalidUserException (invalid email) ---");
        try {
            userService.registerCustomer("Invalid User", "not-an-email", "+1-555-0000", "pass", "DL-999", "Standard");
        } catch (InvalidUserException e) {
            System.out.println("[Caught InvalidUserException] " + e.getMessage());
        } finally {
            System.out.println("[Finally Block A] User validation attempted.");
        }

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

        System.out.println("\n--- Test C: Handling VehicleNotAvailableException (double booking) ---");
        try {
            bookingService.createBooking(customer, vehicle, 3);
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
