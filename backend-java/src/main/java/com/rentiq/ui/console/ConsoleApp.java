package com.rentiq.ui.console;

import com.rentiq.config.DatabaseConfig;
import com.rentiq.database.DatabaseConnection;
import com.rentiq.database.DatabaseConnectionTest;
import com.rentiq.concurrency.AtomicityVisibilityDemo;
import com.rentiq.concurrency.BookingConsumer;
import com.rentiq.concurrency.BookingNotificationTask;
import com.rentiq.concurrency.BookingProducer;
import com.rentiq.concurrency.BookingRequestBuffer;
import com.rentiq.concurrency.FleetMonitorThread;
import com.rentiq.concurrency.GuardedBlockDemo;
import com.rentiq.concurrency.NotifyAllDemo;
import com.rentiq.concurrency.ProducerConsumerDemo;
import com.rentiq.concurrency.RaceConditionDemo;
import com.rentiq.concurrency.StartVsRunDemo;
import com.rentiq.concurrency.ThreadLifecycleDemo;
import com.rentiq.concurrency.ThreadPriorityDemo;
import com.rentiq.concurrency.WaitNotifyDemo;
import com.rentiq.exception.InvalidBookingException;
import com.rentiq.exception.InvalidUserException;
import com.rentiq.exception.VehicleNotAvailableException;
import com.rentiq.model.Booking;
import com.rentiq.model.BookingRequest;
import com.rentiq.model.Customer;
import com.rentiq.model.User;
import com.rentiq.model.Vehicle;
import com.rentiq.service.BookingService;
import com.rentiq.service.UserService;
import com.rentiq.service.VehicleService;
import com.rentiq.util.CollectionDemo;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Console interface for the RentIQ Vehicle Rental Management System.
 * Fully integrates Phase 1, Phase 2 Milestone 1, Milestone 2, Milestone 3, and
 * Milestone 4:
 * - Main menu with core rental operations and collections.
 * - Milestone 2 Submenu (Maps, Queues, Iterators, Sorting).
 * - Milestone 3 Submenu (Multithreading Fundamentals: Thread, Runnable, start
 * vs run, sleep,
 * join, interrupt, lifecycle states, daemon threads, priorities, race
 * conditions, atomicity, visibility).
 * - Milestone 4 Submenu (Thread Coordination: wait, notify, notifyAll, guarded
 * blocks,
 * Producer-Consumer bounded buffer, and graceful shutdown).
 */
public class ConsoleApp {
    private final UserService userService;
    private final VehicleService vehicleService;
    private final BookingService bookingService;
    private final Scanner scanner;

    public ConsoleApp(UserService userService, VehicleService vehicleService, BookingService bookingService,
            Scanner scanner) {
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.bookingService = bookingService;
        this.scanner = scanner;
    }

    /**
     * Launches the main console menu loop.
     */
    public void start() {
        boolean running = true;
        System.out.println("\n=======================================================");
        System.out.println("   Welcome to RentIQ - Vehicle Rental Console System   ");
        System.out.println("=======================================================");

        while (running) {
            displayMainMenu();
            System.out.print("Enter your choice (1-16): ");

            if (!scanner.hasNextLine()) {
                System.out.println("\n[Console] No input stream detected. Exiting console session.");
                break;
            }

            String input = scanner.nextLine().trim();
            int choice = -1;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(
                        "[Error] Invalid numeric input: '" + input + "'. Please enter a number between 1 and 16.\n");
                continue;
            }

            switch (choice) {
                case 1:
                    handleRegisterUser();
                    break;
                case 2:
                    handleViewUsers();
                    break;
                case 3:
                    handleViewVehicles();
                    break;
                case 4:
                    handleSearchVehicles();
                    break;
                case 5:
                    handleBookVehicle();
                    break;
                case 6:
                    handleViewBookings();
                    break;
                case 7:
                    handleCancelBooking();
                    break;
                case 8:
                    handleRecentlyViewedVehicles();
                    break;
                case 9:
                    handleUniqueCategories();
                    break;
                case 10:
                    handleSortedCategories();
                    break;
                case 11:
                    handleCollectionsDemo();
                    break;
                case 12:
                    handleMilestone2Submenu();
                    break;
                case 13:
                    handleMultithreadingSubmenu();
                    break;
                case 14:
                    handleThreadCoordinationSubmenu();
                    break;
                case 15:
                    handleDatabaseConnectivitySubmenu();
                    break;
                case 16:
                    System.out.println("\n[Console] Thank you for using RentIQ. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println(
                            "[Error] Invalid selection (" + choice + "). Please choose a valid option from 1 to 16.\n");
                    break;
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n----------------- RentIQ Main Menu -----------------");
        System.out.println("1.  Register User");
        System.out.println("2.  View Users");
        System.out.println("3.  View Vehicles");
        System.out.println("4.  Search Vehicles");
        System.out.println("5.  Book Vehicle");
        System.out.println("6.  View Bookings");
        System.out.println("7.  Cancel Booking");
        System.out.println("8.  View Recently Viewed Vehicles (LinkedList)");
        System.out.println("9.  View Unique Vehicle Categories (HashSet & LinkedHashSet)");
        System.out.println("10. View Sorted Vehicle Categories (TreeSet)");
        System.out.println("11. Run Complete Collections Framework Demonstration");
        System.out.println("12. Maps, Queues, Iterators & Sorting Submenu (Milestone 2)");
        System.out.println("13. Multithreading Demonstrations Submenu (Milestone 3)");
        System.out.println("14. Thread Coordination Demonstrations Submenu (Milestone 4)");
        System.out.println("15. Database Connectivity Submenu (Phase 3 - Milestones 1 & 2)");
        System.out.println("16. Exit");
        System.out.println("----------------------------------------------------");
    }

    // ==========================================================
    // Milestone 3: Multithreading Submenu
    // ==========================================================

    private void handleMultithreadingSubmenu() {
        boolean inSubmenu = true;
        while (inSubmenu) {
            System.out.println("\n======= Multithreading Demonstrations (Phase 2 - Milestone 3) =======");
            System.out.println("1.  Thread creation using Thread (FleetMonitorThread)");
            System.out.println("2.  Thread creation using Runnable (BookingNotificationTask)");
            System.out.println("3.  start() vs run() Execution Demonstration");
            System.out.println("4.  sleep() Demonstration (Simulated Delays & Interruption)");
            System.out.println("5.  join() Demonstration (Waiting for Concurrent Tasks)");
            System.out.println("6.  interrupt() Demonstration (Graceful Thread Termination)");
            System.out.println("7.  Thread Lifecycle States (NEW, RUNNABLE, TIMED_WAITING, etc.)");
            System.out.println("8.  Daemon Thread Demonstration (isDaemon & Lifecycle)");
            System.out.println("9.  Thread Priority Demonstration (MIN, NORM, MAX)");
            System.out.println("10. Concurrent Vehicle Booking Simulation (Synchronized)");
            System.out.println("11. Race Condition vs Synchronized Booking (Side-by-Side Simulation)");
            System.out.println("12. Atomicity & Visibility Demonstration (Unsafe vs Sync vs Atomic + Volatile)");
            System.out.println("13. Return to Main Menu");
            System.out.println("=====================================================================");
            System.out.print("Enter your choice (1-13): ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();
            int subChoice = -1;
            try {
                subChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid numeric choice: '" + input + "'.\n");
                continue;
            }

            switch (subChoice) {
                case 1:
                    handleThreadCreationViaThread();
                    break;
                case 2:
                    handleThreadCreationViaRunnable();
                    break;
                case 3:
                    StartVsRunDemo.runDemo();
                    break;
                case 4:
                    handleSleepDemo();
                    break;
                case 5:
                    handleJoinDemo();
                    break;
                case 6:
                    handleInterruptDemo();
                    break;
                case 7:
                    ThreadLifecycleDemo.runDemo();
                    break;
                case 8:
                    handleDaemonDemo();
                    break;
                case 9:
                    ThreadPriorityDemo.runDemo();
                    break;
                case 10:
                    handleConcurrentBookingSimulation();
                    break;
                case 11:
                    handleRaceConditionVsSyncDemo();
                    break;
                case 12:
                    AtomicityVisibilityDemo.runDemo();
                    break;
                case 13:
                    inSubmenu = false;
                    break;
                default:
                    System.out.println(
                            "[Error] Invalid selection (" + subChoice + "). Enter a number between 1 and 13.\n");
                    break;
            }
        }
    }

    private void handleThreadCreationViaThread() {
        System.out.println("\n--- 1. Thread Creation: Extending Thread (FleetMonitorThread) ---");
        FleetMonitorThread monitor = new FleetMonitorThread(vehicleService, 200);
        System.out.println("Thread Name before setName(): " + monitor.getName());
        monitor.setName("Custom-FleetMonitor-Worker");
        System.out.println("Thread Name after setName() : " + monitor.getName());
        System.out.println("Thread State before start() : " + monitor.getState());

        monitor.start();
        try {
            Thread.sleep(450); // Let it run two audit cycles
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        monitor.stopMonitoring();
        try {
            monitor.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Thread State after join()   : " + monitor.getState());
        System.out.println("----------------------------------------------------------------\n");
    }

    private void handleThreadCreationViaRunnable() {
        System.out.println("\n--- 2. Thread Creation: Implementing Runnable (BookingNotificationTask) ---");
        Customer customer = (Customer) userService.findUserById("U1001");
        Vehicle vehicle = vehicleService.searchVehicleById("V101");
        if (customer != null && vehicle != null) {
            Booking sampleBooking = new Booking("BK-NOTIF-99", customer, vehicle, 3);
            BookingNotificationTask task = new BookingNotificationTask(sampleBooking, "EMAIL");

            Thread notificationThread = new Thread(task, "Notification-Worker-Thread");
            System.out.println("Created Thread with Runnable task: " + notificationThread.getName());
            notificationThread.start();
            try {
                notificationThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            System.out.println("[Error] Customer or vehicle sample data missing for notification task.");
        }
        System.out.println("---------------------------------------------------------------------------\n");
    }

    private void handleSleepDemo() {
        System.out.println("\n--- 4. sleep() Demonstration with InterruptedException Handling ---");
        System.out.println("Simulating a 300ms vehicle diagnostic check on calling thread...");
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            System.out.println("[Interrupt] Sleep was interrupted!");
            Thread.currentThread().interrupt();
        }
        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("Diagnostic check completed in %d ms.\n", elapsed);
        System.out.println("--------------------------------------------------------------------\n");
    }

    private void handleJoinDemo() {
        System.out.println("\n--- 5. join() Demonstration: Waiting for Concurrent Background Tasks ---");
        System.out.println("Main thread spawning 3 asynchronous billing computation tasks...");
        List<Thread> tasks = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            final int taskId = i;
            Thread t = new Thread(() -> {
                System.out.printf("[%s] Calculating billing batch #%d...\n", Thread.currentThread().getName(), taskId);
                try {
                    Thread.sleep(150 * taskId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.printf("[%s] Batch #%d finished.\n", Thread.currentThread().getName(), taskId);
            }, "BillingTask-" + taskId);
            tasks.add(t);
            t.start();
        }

        System.out.println("[Main Thread] Calling join() on all 3 billing task threads...");
        for (Thread t : tasks) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("[Main Thread] All billing tasks joined successfully. Displaying consolidated reports.");
        System.out.println("------------------------------------------------------------------------\n");
    }

    private void handleInterruptDemo() {
        System.out.println("\n--- 6. interrupt() Demonstration: Stopping Background Tasks Cleanly ---");
        FleetMonitorThread stoppableThread = new FleetMonitorThread(vehicleService, 500);
        stoppableThread.setName("Stoppable-Monitor-Thread");
        stoppableThread.start();
        try {
            Thread.sleep(200);
            System.out.println("[Main Thread] Sending interrupt signal to: " + stoppableThread.getName());
            stoppableThread.interrupt(); // Signal interrupt
            stoppableThread.join(); // Wait for clean termination
            System.out.println("[Main Thread] Background monitor stopped cleanly without deprecated methods.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("------------------------------------------------------------------------\n");
    }

    private void handleDaemonDemo() {
        System.out.println("\n--- 8. Daemon Thread Demonstration ---");
        Thread daemonThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "Background-Daemon-Worker");

        daemonThread.setDaemon(true); // Must be set before start()
        System.out.println("Configured Daemon Status: " + daemonThread.isDaemon());
        daemonThread.start();
        System.out.println(
                "Daemon thread started: " + daemonThread.getName() + " (isDaemon = " + daemonThread.isDaemon() + ")");
        System.out.println("[Concept]: Daemon threads provide background support (e.g. GC, telemetry).");
        System.out.println("           The JVM terminates immediately when all non-daemon user threads finish.");
        daemonThread.interrupt(); // Cleanup
        System.out.println("--------------------------------------\n");
    }

    private void handleConcurrentBookingSimulation() {
        System.out.println("\n--- 10. Concurrent Vehicle Booking Simulation (Thread-Safe Synchronized) ---");
        Customer customer1 = (Customer) userService.findUserById("U1001");
        Customer customer2 = (Customer) userService.findUserById("U1002");
        Vehicle target = vehicleService.searchVehicleById("V102");

        if (customer1 != null && customer2 != null && target != null) {
            RaceConditionDemo.demoSynchronizedBooking(bookingService, customer1, customer2, target);
        } else {
            System.out.println("[Error] Missing sample entities for concurrent booking simulation.");
        }
    }

    private void handleRaceConditionVsSyncDemo() {
        System.out.println("\n--- 11. Side-by-Side: Race Condition vs Synchronized Booking ---");
        Customer customer1 = (Customer) userService.findUserById("U1001");
        Customer customer2 = (Customer) userService.findUserById("U1002");
        Vehicle target = vehicleService.searchVehicleById("V101");

        if (customer1 != null && customer2 != null && target != null) {
            RaceConditionDemo.demoUnsafeBooking(customer1, customer2, target);
            RaceConditionDemo.demoSynchronizedBooking(bookingService, customer1, customer2, target);
        } else {
            System.out.println("[Error] Missing sample entities for race condition comparison.");
        }
    }

    // ==========================================================
    // Milestone 4: Thread Coordination Submenu
    // ==========================================================

    private void handleThreadCoordinationSubmenu() {
        boolean inSubmenu = true;
        while (inSubmenu) {
            System.out.println("\n======= Thread Coordination Demonstrations (Phase 2 - Milestone 4) =======");
            System.out.println("1. Demonstrate wait() (Consumer Waiting on Empty Buffer)");
            System.out.println("2. Demonstrate notify() (Producer Signaling Waiting Consumer)");
            System.out.println("3. Demonstrate notifyAll() (Multiple Waiting Consumers & Condition Recheck)");
            System.out.println("4. Demonstrate guarded blocks (while loop condition testing & spurious wakeups)");
            System.out.println("5. Run Producer-Consumer simulation (Multi-threaded buffer processing)");
            System.out.println("6. Run integrated RentIQ booking request processing");
            System.out.println("7. Demonstrate graceful thread shutdown (Buffer shutdown & consumer termination)");
            System.out.println("8. Return to Main Menu");
            System.out.println("==========================================================================");
            System.out.print("Enter your choice (1-8): ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();
            int subChoice = -1;
            try {
                subChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid numeric choice: '" + input + "'.\n");
                continue;
            }

            switch (subChoice) {
                case 1:
                    handleWaitDemo();
                    break;
                case 2:
                    handleNotifyDemo();
                    break;
                case 3:
                    handleNotifyAllDemo();
                    break;
                case 4:
                    handleGuardedBlockDemo();
                    break;
                case 5:
                    handleProducerConsumerSimulation();
                    break;
                case 6:
                    handleIntegratedBookingProcessing();
                    break;
                case 7:
                    handleGracefulShutdownDemo();
                    break;
                case 8:
                    inSubmenu = false;
                    break;
                default:
                    System.out.println(
                            "[Error] Invalid selection (" + subChoice + "). Enter a number between 1 and 8.\n");
                    break;
            }
        }
    }

    private void handleWaitDemo() {
        System.out.println("\n--- 1. wait() Demonstration: Atomic Lock Release & State Transition ---");
        System.out.println("A calling thread must hold the object's monitor lock before invoking wait().");
        System.out.println(
                "Upon invoking wait(), the thread ATOMICALLY releases the monitor and enters the WAITING state.");
        Customer customer = (Customer) userService.findUserById("U1001");
        Vehicle vehicle = vehicleService.searchVehicleById("V101");
        if (customer == null || vehicle == null) {
            customer = new Customer("U1001", "Alice Johnson", "alice@example.com", "+1-555-0101", "pass", "DL-123",
                    "Standard");
            vehicle = vehicleService.getAllVehicles().get(0);
        }
        WaitNotifyDemo.runDemo(customer, vehicle);
    }

    private void handleNotifyDemo() {
        System.out.println("\n--- 2. notify() Demonstration: Waking a Single Waiting Thread ---");
        System.out.println(
                "A calling thread holding the monitor lock calls notify() to wake an arbitrary waiting thread.");
        System.out.println(
                "Important: The awakened thread does NOT resume execution immediately; it must wait until the signaling thread exits its synchronized block.");
        Customer customer = (Customer) userService.findUserById("U1001");
        Vehicle vehicle = vehicleService.searchVehicleById("V102");
        if (customer == null || vehicle == null) {
            customer = new Customer("U1001", "Alice Johnson", "alice@example.com", "+1-555-0101", "pass", "DL-123",
                    "Standard");
            vehicle = vehicleService.getAllVehicles().get(0);
        }
        WaitNotifyDemo.runDemo(customer, vehicle);
    }

    private void handleNotifyAllDemo() {
        System.out.println("\n--- 3. notifyAll() Demonstration: Waking Multiple Waiting Consumers ---");
        Customer customer = (Customer) userService.findUserById("U1001");
        Vehicle v1 = vehicleService.searchVehicleById("V101");
        Vehicle v2 = vehicleService.searchVehicleById("V102");
        if (customer == null || v1 == null || v2 == null) {
            customer = new Customer("U1001", "Alice Johnson", "alice@example.com", "+1-555-0101", "pass", "DL-123",
                    "Standard");
            v1 = vehicleService.getAllVehicles().get(0);
            v2 = vehicleService.getAllVehicles().get(1);
        }
        NotifyAllDemo.runDemo(customer, v1, v2);
    }

    private void handleGuardedBlockDemo() {
        System.out.println("\n--- 4. Guarded Block Demonstration: Polling Conditions inside while loop ---");
        GuardedBlockDemo.runDemo();
    }

    private void handleProducerConsumerSimulation() {
        System.out.println("\n--- 5. Producer-Consumer Simulation: Bounded Buffer with Worker Threads ---");
        Customer c1 = (Customer) userService.findUserById("U1001");
        Customer c2 = (Customer) userService.findUserById("U1002");
        Customer c3 = (Customer) userService.findUserById("U1003");
        if (c1 == null)
            c1 = new Customer("U1001", "Alice", "a@test.com", "123", "pass", "DL1", "Gold");
        if (c2 == null)
            c2 = new Customer("U1002", "Bob", "b@test.com", "456", "pass", "DL2", "Standard");
        if (c3 == null)
            c3 = new Customer("U1003", "Elena", "e@test.com", "789", "pass", "DL3", "VIP");
        ProducerConsumerDemo.runSimulation(bookingService, vehicleService, c1, c2, c3);
    }

    private void handleIntegratedBookingProcessing() {
        System.out.println("\n--- 6. Integrated RentIQ Booking Request Processing ---");
        Customer customer = (Customer) userService.findUserById("U1001");
        if (customer == null)
            customer = new Customer("U1001", "Alice", "a@test.com", "123", "pass", "DL1", "Gold");
        List<Vehicle> fleet = vehicleService.getAllVehicles();
        if (fleet.size() < 2) {
            System.out.println("[Error] Insufficient fleet vehicles.");
            return;
        }

        BookingRequestBuffer buffer = new BookingRequestBuffer(2);
        AtomicInteger processed = new AtomicInteger(0);
        BookingConsumer consumer = new BookingConsumer(buffer, bookingService, "RentIQ-Worker-1", processed);
        Thread cThread = new Thread(consumer, "RentIQ-Worker-1");
        cThread.start();

        List<BookingRequest> reqs = new ArrayList<>();
        reqs.add(new BookingRequest(BookingRequest.generateNextRequestId(), customer, fleet.get(0), 2));
        reqs.add(new BookingRequest(BookingRequest.generateNextRequestId(), customer, fleet.get(1), 3));
        BookingProducer producer = new BookingProducer(buffer, reqs, 60);
        Thread pThread = new Thread(producer, "RentIQ-Producer");
        pThread.start();

        try {
            pThread.join();
            while (!buffer.isEmpty()) {
                Thread.sleep(20);
            }
            buffer.shutdown();
            cThread.join();
            System.out.println("[Integrated Processing] Successfully processed " + processed.get() + " bookings.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleGracefulShutdownDemo() {
        System.out.println("\n--- 7. Graceful Thread Shutdown Demonstration ---");
        System.out.println("Demonstrating how buffer.shutdown() unblocks waiting consumer threads cleanly.");
        BookingRequestBuffer buffer = new BookingRequestBuffer(3);
        AtomicInteger processed = new AtomicInteger(0);
        Thread c1 = new Thread(new BookingConsumer(buffer, bookingService, "ShutdownWorker-1", processed),
                "ShutdownWorker-1");
        Thread c2 = new Thread(new BookingConsumer(buffer, bookingService, "ShutdownWorker-2", processed),
                "ShutdownWorker-2");
        c1.start();
        c2.start();

        try {
            Thread.sleep(100);
            System.out.println("  Inspected state of ShutdownWorker-1: " + c1.getState() + " (Waiting on buffer)");
            System.out.println("  Inspected state of ShutdownWorker-2: " + c2.getState() + " (Waiting on buffer)");
            System.out.println("  Invoking buffer.shutdown() to wake all waiting threads...");
            buffer.shutdown();
            c1.join(500);
            c2.join(500);
            System.out.println("  State of ShutdownWorker-1 after shutdown: " + c1.getState());
            System.out.println("  State of ShutdownWorker-2 after shutdown: " + c2.getState());
            System.out.println("  Graceful shutdown completed with 0 hung threads.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ==========================================================
    // Phase 3 - Milestones 1 & 2: Database Connectivity Submenu
    // ==========================================================

    private void handleDatabaseConnectivitySubmenu() {
        boolean inSubmenu = true;
        while (inSubmenu) {
            System.out.println("\n======= Database Connectivity Submenu (Phase 3 - Milestones 1 & 2) =======");
            System.out.println("1. Test MySQL Connection (Ping & Diagnostics)");
            System.out.println("2. Display Database Connection Status & Configuration");
            System.out.println("3. Execute Simple Database Verification Query");
            System.out.println("4. Return to Main Menu");
            System.out.println("==========================================================================");
            System.out.print("Enter your choice (1-4): ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();
            int subChoice = -1;
            try {
                subChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid numeric choice: '" + input + "'.\n");
                continue;
            }

            switch (subChoice) {
                case 1:
                    DatabaseConnectionTest.runConnectionTest();
                    break;
                case 2:
                    handleDisplayDatabaseStatus();
                    break;
                case 3:
                    handleExecuteVerificationQuery();
                    break;
                case 4:
                    inSubmenu = false;
                    break;
                default:
                    System.out.println("[Error] Invalid selection (" + subChoice + "). Enter a number between 1 and 4.\n");
                    break;
            }
        }
    }

    private void handleDisplayDatabaseStatus() {
        System.out.println("\n--- Database Connection Status & Configuration ---");
        DatabaseConfig config = DatabaseConnection.getDefaultConfig();
        config.displayConfig();

        System.out.println("Probing live connection to MySQL server...");
        DatabaseConnection.ConnectionResult result = DatabaseConnection.testConnection();
        if (result.isSuccess()) {
            System.out.println("[Status] ONLINE / HEALTHY");
            System.out.println("  Message        : " + result.getMessage());
            System.out.println("  Database Name  : " + result.getDatabaseName());
            System.out.println("  Server Version : " + result.getServerVersion());
            System.out.println("  Response Time  : " + result.getResponseTimeMs() + " ms");
        } else {
            System.out.println("[Status] OFFLINE / UNREACHABLE");
            System.out.println("  Diagnostic     : " + result.getMessage());
            System.out.println("  Note           : In-memory services continue to function normally without MySQL.");
        }
    }

    private void handleExecuteVerificationQuery() {
        System.out.println("\n--- Execute Verification Query ---");
        System.out.println("Enter a SELECT query (or press Enter for default: 'SELECT vehicle_id, name, brand, model, price_per_day, availability_status FROM vehicles LIMIT 5'):");
        String sql = readLine();
        if (sql.isEmpty()) {
            sql = "SELECT vehicle_id, name, brand, model, price_per_day, availability_status FROM vehicles LIMIT 5";
        }
        DatabaseConnectionTest.executeVerificationQuery(sql);
    }

    // ==========================================================
    // Milestone 2 Submenu (Preserved)
    // ==========================================================

    private void handleMilestone2Submenu() {
        boolean inSubmenu = true;
        while (inSubmenu) {
            System.out.println("\n======= Maps, Queues, Iterators & Sorting Submenu =======");
            System.out.println("1.  Search User by ID using HashMap");
            System.out.println("2.  Search Vehicle by ID using HashMap");
            System.out.println("3.  Search Booking by ID using HashMap");
            System.out.println("4.  Display Insertion-Ordered Records using LinkedHashMap");
            System.out.println("5.  Display Sorted Records using TreeMap");
            System.out.println("6.  Submit a Pending Booking Request (FIFO Queue)");
            System.out.println("7.  Display Pending Booking Requests (FIFO Queue)");
            System.out.println("8.  Process Next FIFO Booking Request");
            System.out.println("9.  Demonstrate Deque Operations (ArrayDeque Front/Rear)");
            System.out.println("10. Demonstrate PriorityQueue Booking Processing (VIP Priority)");
            System.out.println("11. Demonstrate Iterator and ListIterator");
            System.out.println("12. Sort Vehicles using Comparable (Natural Order by ID)");
            System.out.println("13. Sort Vehicles using Comparators (Price, Name, Rating)");
            System.out.println("14. Return to Main Menu");
            System.out.println("=========================================================");
            System.out.print("Enter your choice (1-14): ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String subInput = scanner.nextLine().trim();
            int subChoice = -1;
            try {
                subChoice = Integer.parseInt(subInput);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid numeric choice: '" + subInput + "'.\n");
                continue;
            }

            switch (subChoice) {
                case 1:
                    handleSearchUserHashMap();
                    break;
                case 2:
                    handleSearchVehicleHashMap();
                    break;
                case 3:
                    handleSearchBookingHashMap();
                    break;
                case 4:
                    handleDisplayLinkedHashMap();
                    break;
                case 5:
                    handleDisplayTreeMap();
                    break;
                case 6:
                    handleSubmitBookingRequestQueue();
                    break;
                case 7:
                    bookingService.displayPendingRequests();
                    break;
                case 8:
                    handleProcessNextQueueRequest();
                    break;
                case 9:
                    handleDemonstrateDeque();
                    break;
                case 10:
                    handleDemonstratePriorityQueue();
                    break;
                case 11:
                    handleDemonstrateIterators();
                    break;
                case 12:
                    vehicleService.displayVehiclesSortedNaturally();
                    break;
                case 13:
                    handleComparatorSortingMenu();
                    break;
                case 14:
                    inSubmenu = false;
                    break;
                default:
                    System.out
                            .println("[Error] Invalid option (" + subChoice + "). Enter a number between 1 and 14.\n");
                    break;
            }
        }
    }

    private void handleSearchUserHashMap() {
        System.out.print("\n[HashMap] Enter User ID to lookup (e.g. U1001, A1003): ");
        String userId = readLine();
        User user = userService.findUserById(userId);
        if (user != null) {
            System.out.println("[HashMap O(1) Match Found]");
            user.displayUserInfo();
        } else {
            System.out.println("[HashMap] No user found with ID: '" + userId + "'");
        }
    }

    private void handleSearchVehicleHashMap() {
        System.out.print("\n[HashMap] Enter Vehicle ID to lookup (e.g. V101, B201): ");
        String vehicleId = readLine();
        Vehicle vehicle = vehicleService.searchVehicleById(vehicleId);
        if (vehicle != null) {
            System.out.println("[HashMap O(1) Match Found]");
            vehicleService.displayVehicleDetails(vehicleId);
        } else {
            System.out.println("[HashMap] No vehicle found with ID: '" + vehicleId + "'");
        }
    }

    private void handleSearchBookingHashMap() {
        System.out.print("\n[HashMap] Enter Booking ID to lookup (e.g. BK-1001): ");
        String bookingId = readLine();
        Booking booking = bookingService.searchBookingById(bookingId);
        if (booking != null) {
            System.out.println("[HashMap O(1) Match Found]");
            booking.displayBookingDetails();
        } else {
            System.out.println("[HashMap] No booking found with ID: '" + bookingId + "'");
        }
    }

    private void handleDisplayLinkedHashMap() {
        System.out.println("\n--- LinkedHashMap: Chronological Insertion Order Display ---");
        userService.displayRecentRegistrations();
        vehicleService.displayVehiclesByInsertionOrder();
        bookingService.displayBookingHistory();
    }

    private void handleDisplayTreeMap() {
        System.out.println("\n--- TreeMap: Naturally Sorted Key Index Display ---");
        vehicleService.displayVehiclesSortedById();
        bookingService.displayBookingsSortedById();
    }

    private void handleSubmitBookingRequestQueue() {
        System.out.println("\n--- Submit Pending Booking Request (FIFO Queue) ---");
        System.out.print("Enter Customer User ID: ");
        String userId = readLine();
        User user = userService.findUserById(userId);
        if (user == null || !(user instanceof Customer)) {
            System.out.println("[Error] Valid Customer ID required to submit request.");
            return;
        }

        System.out.print("Enter Vehicle ID: ");
        String vehicleId = readLine();
        Vehicle vehicle = vehicleService.searchVehicleById(vehicleId);
        if (vehicle == null) {
            System.out.println("[Error] Vehicle not found in fleet.");
            return;
        }

        System.out.print("Enter Rental Days: ");
        String daysStr = readLine();
        try {
            int days = Integer.parseInt(daysStr.trim());
            bookingService.submitBookingRequest((Customer) user, vehicle, days);
        } catch (NumberFormatException e) {
            System.out.println("[Error] Invalid integer duration: '" + daysStr + "'");
        }
    }

    private void handleProcessNextQueueRequest() {
        try {
            bookingService.processNextBookingRequest();
        } catch (InvalidBookingException | VehicleNotAvailableException e) {
            System.out.println("[Queue Processing Error] " + e.getMessage());
        }
    }

    private void handleDemonstrateDeque() {
        System.out.println("\n--- ArrayDeque Dual-Ended Operations Demonstration ---");
        Customer customer = (Customer) userService.findUserById("U1001");
        Vehicle v1 = vehicleService.searchVehicleById("V101");
        Vehicle v2 = vehicleService.searchVehicleById("V102");

        if (customer != null && v1 != null && v2 != null) {
            BookingRequest reqStandard = new BookingRequest(BookingRequest.generateNextRequestId(), customer, v1, 3);
            BookingRequest reqExpress = new BookingRequest(BookingRequest.generateNextRequestId(), customer, v2, 2);

            bookingService.addRequestToRear(reqStandard);
            bookingService.addRequestToFront(reqExpress);
            bookingService.displayDequeRequests();
            bookingService.removeRequestFromFront();
            bookingService.removeRequestFromRear();
            bookingService.displayDequeRequests();
        } else {
            System.out.println("[Deque] Insufficient sample data for interactive deque demo.");
        }
    }

    private void handleDemonstratePriorityQueue() {
        System.out.println("\n--- PriorityQueue Priority Processing Demonstration ---");
        Customer vip = (Customer) userService.findUserById("U1001");
        Customer regular = (Customer) userService.findUserById("U1002");
        Vehicle v1 = vehicleService.searchVehicleById("V102");
        Vehicle v2 = vehicleService.searchVehicleById("B201");

        if (vip != null && regular != null && v1 != null && v2 != null) {
            BookingRequest reqReg = new BookingRequest(BookingRequest.generateNextRequestId(), regular, v1, 2);
            BookingRequest reqVip = new BookingRequest(BookingRequest.generateNextRequestId(), vip, v2, 4);

            System.out.println("1. Enqueuing Regular customer first: " + reqReg);
            bookingService.submitPriorityBookingRequest(reqReg);

            System.out.println("2. Enqueuing VIP customer second: " + reqVip);
            bookingService.submitPriorityBookingRequest(reqVip);

            bookingService.displayPriorityQueue();
            try {
                System.out.println("3. Processing next priority request (VIP should process first):");
                bookingService.processNextPriorityRequest();
            } catch (Exception e) {
                System.out.println("[PriorityQueue Error] " + e.getMessage());
            }
        }
    }

    private void handleDemonstrateIterators() {
        System.out.println("\n--- Iterator and ListIterator Showcase ---");
        vehicleService.traverseVehiclesBidirectional();
        vehicleService.removeUnavailableVehiclesWithIterator();
    }

    private void handleComparatorSortingMenu() {
        System.out.println("\nChoose Comparator Sorting Criteria:");
        System.out.println("1. Price: Lowest to Highest (Ascending)");
        System.out.println("2. Price: Highest to Lowest (Descending)");
        System.out.println("3. Name: Alphabetical Order (Ascending)");
        System.out.println("4. Rating: Highest to Lowest (Descending)");
        System.out.print("Enter choice (1-4): ");
        String opt = readLine();
        switch (opt) {
            case "1":
                vehicleService.displayVehiclesSortedByPriceAsc();
                break;
            case "2":
                vehicleService.displayVehiclesSortedByPriceDesc();
                break;
            case "3":
                vehicleService.displayVehiclesSortedByName();
                break;
            case "4":
                vehicleService.displayVehiclesSortedByRatingDesc();
                break;
            default:
                System.out.println("[Error] Invalid comparator option.");
                break;
        }
    }

    private void handleRegisterUser() {
        System.out.println("\n--- 1. Register New Customer ---");
        try {
            System.out.print("Enter Full Name: ");
            String name = readLine();

            System.out.print("Enter Email Address: ");
            String email = readLine();

            System.out.print("Enter Phone Number: ");
            String phone = readLine();

            System.out.print("Enter Password: ");
            String password = readLine();

            System.out.print("Enter Driving License Number: ");
            String license = readLine();

            System.out.print("Enter Membership Level (Standard/Gold/VIP): ");
            String membership = readLine();

            Customer customer = userService.registerCustomer(name, email, phone, password, license, membership);
            System.out.println("[Success] Registered customer with User ID: " + customer.getUserId());
        } catch (InvalidUserException e) {
            System.out.println("[User Registration Error] " + e.getMessage());
        }
    }

    private void handleViewUsers() {
        System.out.println("\n--- 2. Registered Users ---");
        userService.displayAllUsers();
    }

    private void handleViewVehicles() {
        System.out.println("\n--- 3. Available Vehicles in Fleet ---");
        vehicleService.displayAllVehicles();
    }

    private void handleSearchVehicles() {
        System.out.println("\n--- 4. Search Vehicles ---");
        System.out.print("Enter search keyword (Brand, Model, or Category): ");
        String keyword = readLine();
        int matches = vehicleService.searchVehicles(keyword);
        if (matches > 0) {
            System.out.print("Enter Vehicle ID to view details & add to recently viewed (or press Enter to skip): ");
            String vId = readLine();
            if (!vId.isEmpty()) {
                vehicleService.displayVehicleDetails(vId);
            }
        }
    }

    private void handleBookVehicle() {
        System.out.println("\n--- 5. Book a Vehicle ---");
        try {
            System.out.print("Enter Customer User ID: ");
            String userId = readLine();
            User user = userService.findUserById(userId);
            if (user == null) {
                throw new InvalidBookingException("User with ID '" + userId + "' does not exist.");
            }
            if (!(user instanceof Customer)) {
                throw new InvalidBookingException("Only registered Customers can book vehicles (User is an Admin).");
            }

            System.out.print("Enter Vehicle ID to Book: ");
            String vehicleId = readLine();
            Vehicle vehicle = vehicleService.searchVehicleById(vehicleId);
            if (vehicle == null) {
                throw new InvalidBookingException("Vehicle with ID '" + vehicleId + "' does not exist in fleet.");
            }

            System.out.print("Enter Rental Duration (days): ");
            String durationInput = readLine();
            int days;
            try {
                days = Integer.parseInt(durationInput.trim());
            } catch (NumberFormatException e) {
                throw new InvalidBookingException("Invalid duration: '" + durationInput + "' is not a valid integer.");
            }

            Booking booking = bookingService.createBooking((Customer) user, vehicle, days);
            if (booking != null) {
                System.out.println("\n>>> Booking Confirmation <<<");
                booking.displayBookingDetails();
            }
        } catch (InvalidBookingException e) {
            System.out.println("[Booking Error] " + e.getMessage());
        } catch (VehicleNotAvailableException e) {
            System.out.println("[Vehicle Unavailable] " + e.getMessage());
        }
    }

    private void handleViewBookings() {
        System.out.println("\n--- 6. View All Bookings ---");
        bookingService.displayAllBookings();
    }

    private void handleCancelBooking() {
        System.out.println("\n--- 7. Cancel Booking ---");
        try {
            System.out.print("Enter Booking ID to cancel: ");
            String bookingId = readLine();
            boolean success = bookingService.cancelBooking(bookingId);
            if (success) {
                System.out.println("[Success] Booking " + bookingId + " has been successfully cancelled.");
            }
        } catch (InvalidBookingException e) {
            System.out.println("[Cancellation Error] " + e.getMessage());
        }
    }

    private void handleRecentlyViewedVehicles() {
        System.out.println("\n--- 8. Recently Viewed Vehicles (LinkedList) ---");
        vehicleService.displayRecentlyViewedVehicles();
        System.out.print("Enter Vehicle ID to remove from recently viewed (or press Enter to skip): ");
        String vId = readLine();
        if (!vId.isEmpty()) {
            boolean removed = vehicleService.removeRecentlyViewed(vId);
            if (removed) {
                System.out.println("[LinkedList] Successfully removed vehicle '" + vId + "' from recently viewed.");
            } else {
                System.out.println("[LinkedList] Vehicle '" + vId + "' was not found in recently viewed list.");
            }
        }
    }

    private void handleUniqueCategories() {
        System.out.println("\n--- 9. Unique Vehicle Categories ---");
        System.out.println("HashSet (Constant-Time O(1) Unique Set): " + vehicleService.getCategoryHashSet());
        System.out.println(
                "LinkedHashSet (Unique & Preserving Insertion Sequence): " + vehicleService.getCategoryLinkedHashSet());
    }

    private void handleSortedCategories() {
        System.out.println("\n--- 10. Sorted Vehicle Categories (TreeSet) ---");
        System.out.println("TreeSet (Unique & Natural Alphabetical Order): " + vehicleService.getCategoryTreeSet());
    }

    private void handleCollectionsDemo() {
        System.out.println("\n--- 11. Running Collections Framework Showcase ---");
        CollectionDemo.runAllDemos();
    }

    private String readLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return "";
    }
}
