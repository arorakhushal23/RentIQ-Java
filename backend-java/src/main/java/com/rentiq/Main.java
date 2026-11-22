package com.rentiq;

import com.rentiq.concurrency.AtomicityVisibilityDemo;
import com.rentiq.concurrency.BookingNotificationTask;
import com.rentiq.concurrency.FleetMonitorThread;
import com.rentiq.concurrency.RaceConditionDemo;
import com.rentiq.concurrency.StartVsRunDemo;
import com.rentiq.concurrency.ThreadLifecycleDemo;
import com.rentiq.concurrency.ThreadPriorityDemo;
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
 * - Collections Framework (ArrayList, LinkedList, HashSet, LinkedHashSet, TreeSet)
 * - Map Collections (HashMap, LinkedHashMap, TreeMap)
 * - Queue Collections (Queue FIFO, Deque ArrayDeque, PriorityQueue)
 * - Iterators (Iterator safe removal, ListIterator bidirectional traversal)
 * - Sorting (Comparable natural order, Comparator multi-criteria)
 * - Multithreading Fundamentals (Thread, Runnable, start vs run, sleep, join, interrupt,
 *   lifecycle states, daemon threads, thread priorities, race conditions, synchronized bookings,
 *   atomicity, and memory visibility).
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("   RentIQ - Vehicle Rental System (Phase 2, Milestone 3)    ");
        System.out.println("   Multithreading Fundamentals (Java 26 / JDK 26.0.1)       ");
        System.out.println("============================================================\n");

        // 1. Initialize Core Services
        UserService userService = new UserService();
        VehicleService vehicleService = new VehicleService();
        BookingService bookingService = new BookingService();

        // 2. Pre-seed fleet vehicles with diverse prices and ratings
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

        // Pre-seed sample users (Regular, Gold, and VIP customers, plus Admin)
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
        // SECTION D: Maps, Queues, Iterators & Sorting (Milestone 2 Features)
        // ------------------------------------------------------------
        demonstrateMilestone2Features(userService, vehicleService, bookingService, alice, bob, elena, carlos);

        // ------------------------------------------------------------
        // SECTION E: Multithreading Fundamentals (Milestone 3 Live Demonstrations)
        // ------------------------------------------------------------
        demonstrateMultithreadingFeatures(vehicleService, bookingService, alice, bob);

        // ------------------------------------------------------------
        // SECTION F: Console Application Automated Demonstration
        // ------------------------------------------------------------
        System.out.println("\n>>> Launching Console Application Demonstration <<<");
        String simulatedConsoleInput =
            // 1. Main menu option 13: Enter Milestone 3 Submenu
            "13\n" +
            "1\n" +                       // Submenu 1: Thread creation via Thread
            "2\n" +                       // Submenu 2: Thread creation via Runnable
            "3\n" +                       // Submenu 3: start() vs run()
            "4\n" +                       // Submenu 4: sleep()
            "5\n" +                       // Submenu 5: join()
            "6\n" +                       // Submenu 6: interrupt()
            "7\n" +                       // Submenu 7: Thread lifecycle states
            "8\n" +                       // Submenu 8: Daemon threads
            "9\n" +                       // Submenu 9: Thread priorities
            "10\n" +                      // Submenu 10: Concurrent synchronized booking
            "11\n" +                      // Submenu 11: Race condition vs synchronized booking
            "12\n" +                      // Submenu 12: Atomicity & visibility
            "13\n" +                      // Submenu 13: Return to Main Menu
            // 2. Main menu operations
            "12\n" + "1\n" + "U1001\n" + "14\n" + // Milestone 2 submenu HashMap lookup then return
            "8\n" + "\n" +                // 8. View Recently Viewed Vehicles (LinkedList)
            "9\n" +                       // 9. View Unique Categories (HashSet/LinkedHashSet)
            "10\n" +                      // 10. View Sorted Categories (TreeSet)
            "99\n" +                      // Invalid choice test
            "14\n";                       // 14. Exit

        Scanner consoleScanner = new Scanner(simulatedConsoleInput);
        ConsoleApp consoleApp = new ConsoleApp(userService, vehicleService, bookingService, consoleScanner);
        consoleApp.start();

        System.out.println("\n============================================================");
        System.out.println("   RentIQ Phase 2 Milestone 3 Completed Successfully!       ");
        System.out.println("============================================================");
    }

    /**
     * Live comprehensive verification of Milestone 3 Multithreading Fundamentals:
     * 1. Thread creation: Extending Thread (FleetMonitorThread)
     * 2. Thread creation: Implementing Runnable (BookingNotificationTask)
     * 3. start() vs run()
     * 4. sleep(), join(), and interrupt()
     * 5. Thread Lifecycle States (NEW, RUNNABLE, TIMED_WAITING, WAITING, BLOCKED, TERMINATED)
     * 6. Daemon thread status and behavior
     * 7. Thread priorities (MIN_PRIORITY, NORM_PRIORITY, MAX_PRIORITY)
     * 8. Race condition simulation vs Synchronized booking solution
     * 9. Atomicity (Unsafe vs Sync vs AtomicInteger) & Visibility (volatile flag)
     */
    private static void demonstrateMultithreadingFeatures(VehicleService vehicleService, BookingService bookingService,
                                                         Customer alice, Customer bob) {
        System.out.println("\n============================================================");
        System.out.println("   Milestone 3: Multithreading Live Demonstrations          ");
        System.out.println("============================================================\n");

        // 1. Thread creation using Thread subclass
        System.out.println(">>> 1. Thread Creation via Thread Subclass (FleetMonitorThread):");
        FleetMonitorThread monitor = new FleetMonitorThread(vehicleService, 150);
        monitor.setName("FleetAudit-Thread");
        monitor.start();
        try {
            Thread.sleep(350); // Let monitor run two cycles
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        monitor.stopMonitoring();
        try {
            monitor.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 2. Thread creation using Runnable
        System.out.println("\n>>> 2. Thread Creation via Runnable (BookingNotificationTask):");
        Vehicle notifVeh = vehicleService.searchVehicleById("V101");
        Booking sampleBooking = new Booking("BK-ASYNC-10", alice, notifVeh, 4);
        BookingNotificationTask notifTask = new BookingNotificationTask(sampleBooking, "EMAIL");
        Thread notifThread = new Thread(notifTask, "EmailDispatcher-Thread");
        notifThread.start();
        try {
            notifThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 3. start() vs run()
        StartVsRunDemo.runDemo();

        // 4. Thread Lifecycle States
        ThreadLifecycleDemo.runDemo();

        // 5. Thread Priorities
        ThreadPriorityDemo.runDemo();

        // 6. Daemon Thread Demonstration
        System.out.println(">>> 6. Daemon Thread Demonstration:");
        Thread daemonWorker = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "Telemetry-Daemon-Thread");
        daemonWorker.setDaemon(true);
        daemonWorker.start();
        System.out.println("  Worker Thread: " + daemonWorker.getName() + " | isDaemon: " + daemonWorker.isDaemon());
        System.out.println("  Daemon threads do not prevent the JVM from shutting down when user threads complete.");
        daemonWorker.interrupt();
        try {
            daemonWorker.join(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 7. Atomicity and Visibility
        AtomicityVisibilityDemo.runDemo();

        // 8. Race Condition vs Synchronized Booking
        System.out.println(">>> 8. Race Condition Simulation vs Thread-Safe Synchronized Booking:");
        Vehicle targetVehicle = vehicleService.searchVehicleById("V102");
        RaceConditionDemo.demoUnsafeBooking(alice, bob, targetVehicle);
        RaceConditionDemo.demoSynchronizedBooking(bookingService, alice, bob, targetVehicle);

        System.out.println("============================================================\n");
    }

    private static void demonstrateMilestone2Features(UserService userService, VehicleService vehicleService,
                                                     BookingService bookingService,
                                                     Customer alice, Customer bob, Customer elena, Customer carlos) {
        System.out.println("\n============================================================");
        System.out.println("   Milestone 2: Programmatic Feature Verification          ");
        System.out.println("============================================================\n");

        // 1. HashMap Lookups
        User foundUser = userService.findUserById("U1001");
        Vehicle foundVehicle = vehicleService.searchVehicleById("V101");
        System.out.println("HashMap User Lookup: " + (foundUser != null ? foundUser.getName() : "NOT FOUND"));
        System.out.println("HashMap Vehicle Lookup: " + (foundVehicle != null ? foundVehicle.getName() : "NOT FOUND"));

        // 2. FIFO Queue
        Vehicle reqVehicle1 = vehicleService.searchVehicleById("V103");
        bookingService.submitBookingRequest(bob, reqVehicle1, 2);
        try {
            bookingService.processNextBookingRequest();
        } catch (Exception e) {
            System.out.println("[Error] " + e.getMessage());
        }

        // 3. Sorting with Comparable and Comparator
        vehicleService.displayVehiclesSortedNaturally();
        vehicleService.displayVehiclesSortedByPriceAsc();
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

        try {
            userService.registerCustomer("Invalid User", "not-an-email", "+1-555-0000", "pass", "DL-999", "Standard");
        } catch (InvalidUserException e) {
            System.out.println("[Caught InvalidUserException] " + e.getMessage());
        } finally {
            System.out.println("[Finally Block A] User validation attempted.");
        }

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
