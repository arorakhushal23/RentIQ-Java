package com.rentiq.ui.console;

import com.rentiq.exception.InvalidBookingException;
import com.rentiq.exception.InvalidUserException;
import com.rentiq.exception.VehicleNotAvailableException;
import com.rentiq.model.Booking;
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
 * Implements interactive menu options, user input validation, Scanner handling,
 * error recovery, and demonstrates Collections (ArrayList, LinkedList, HashSet, LinkedHashSet, TreeSet).
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
            displayMenu();
            System.out.print("Enter your choice (1-12): ");

            if (!scanner.hasNextLine()) {
                System.out.println("\n[Console] No input stream detected. Exiting console session.");
                break;
            }

            String input = scanner.nextLine().trim();

            int choice = -1;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid numeric input: '" + input + "'. Please enter a number between 1 and 12.\n");
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
                    System.out.println("\n[Console] Thank you for using RentIQ. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("[Error] Invalid selection (" + choice + "). Please choose a valid option from 1 to 12.\n");
                    break;
            }
        }
    }

    private void displayMenu() {
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
        System.out.println("11. Run Collections Framework Demonstration");
        System.out.println("12. Exit");
        System.out.println("----------------------------------------------------");
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
