package com.rentiq.ui.console;

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
import java.util.Scanner;

/**
 * Console interface for the RentIQ Vehicle Rental Management System.
 * Exposes all Phase 1 and Phase 2 (Milestones 1 & 2) features:
 * - Lists, Sets, Maps, Queues, Iterators, Comparable & Comparator Sorting.
 */
public class ConsoleApp {
    private final UserService userService;
    private final VehicleService vehicleService;
    private final BookingService bookingService;
    private final Scanner scanner;

    public ConsoleApp(UserService userService, VehicleService vehicleService, BookingService bookingService, Scanner scanner) {
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
            System.out.print("Enter your choice (1-13): ");

            if (!scanner.hasNextLine()) {
                System.out.println("\n[Console] No input stream detected. Exiting console session.");
                break;
            }

            String input = scanner.nextLine().trim();
            int choice = -1;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid numeric input: '" + input + "'. Please enter a number between 1 and 13.\n");
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
                    System.out.println("\n[Console] Thank you for using RentIQ. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("[Error] Invalid selection (" + choice + "). Please choose a valid option from 1 to 13.\n");
                    break;
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n----------------- RentIQ Main Menu -----------------");
        System.out.println("1. Register User");
        System.out.println("2. View Users");
        System.out.println("3. View Vehicles");
        System.out.println("4. Search Vehicles");
        System.out.println("5. Book Vehicle");
        System.out.println("6. View Bookings");
        System.out.println("7. Cancel Booking");
        System.out.println("8. View Recently Viewed Vehicles (LinkedList)");
        System.out.println("9. View Unique Vehicle Categories (HashSet & LinkedHashSet)");
        System.out.println("10. View Sorted Vehicle Categories (TreeSet)");
        System.out.println("11. Run Complete Collections Framework Demonstration");
        System.out.println("12. Maps, Queues, Iterators & Sorting Submenu (Milestone 2)");
        System.out.println("13. Exit");
        System.out.println("----------------------------------------------------");
    }

    /**
     * Milestone 2 Submenu exposing all Map, Queue, Iterator, and Sorting features.
     */
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
                    System.out.println("[Error] Invalid option (" + subChoice + "). Enter a number between 1 and 14.\n");
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
        Customer vip = (Customer) userService.findUserById("U1001"); // VIP
        Customer regular = (Customer) userService.findUserById("U1002"); // Standard
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

    // ==========================================
    // Phase 1 and Milestone 1 Handlers (Preserved)
    // ==========================================

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
        System.out.println("LinkedHashSet (Unique & Preserving Insertion Sequence): " + vehicleService.getCategoryLinkedHashSet());
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
