package com.rentiq.ui;

import com.rentiq.model.Admin;
import com.rentiq.model.Booking;
import com.rentiq.model.Customer;
import com.rentiq.model.User;
import com.rentiq.model.Vehicle;
import com.rentiq.service.BookingService;
import com.rentiq.service.UserService;
import com.rentiq.service.VehicleService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private final UserService userService;
    private final VehicleService vehicleService;
    private final BookingService bookingService;
    private final Scanner scanner;
    private User currentUser;

    public ConsoleApp(UserService userService, VehicleService vehicleService,
                      BookingService bookingService, Scanner scanner) {
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.bookingService = bookingService;
        this.scanner = scanner;
        this.currentUser = null;
    }

    public void start() {
        System.out.println("=======================================================");
        System.out.println("   Welcome to RentIQ - Vehicle Rental Console System   ");
        System.out.println("=======================================================\n");

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = readInt("Enter your choice (1-12): ", 1, 12);

            switch (choice) {
                case 1:
                    handleRegisterAdmin();
                    break;
                case 2:
                    try {
                        userService.displayDatabaseStatistics();
                    } catch (Exception e) {
                        System.out.println("[Database Error] Unable to fetch statistics: " + e.getMessage());
                    }
                    break;
                case 3:
                    handleRegisterCustomer();
                    break;
                case 4:
                    handleAccountOrLogin();
                    break;
                case 5:
                    handleViewVehicles();
                    break;
                case 6:
                    handleSearchVehicles();
                    break;
                case 7:
                    handleCompareVehicles();
                    break;
                case 8:
                    handleBookVehicle();
                    break;
                case 9:
                    handleViewMyBookings();
                    break;
                case 10:
                    handleCancelBooking();
                    break;
                case 11:
                    handleRecentlyViewedVehicles();
                    break;
                case 12:
                    System.out.println("\n[Console] Thank you for using RentIQ Vehicle Rental System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("[Error] Invalid option.");
                    break;
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("----------------------------------------------------");
        System.out.println("                 RENTIQ MAIN MENU                   ");
        System.out.println("----------------------------------------------------");

        if (currentUser != null) {
            String roleText;

            if (currentUser instanceof Customer) {
                roleText = "CUSTOMER";
            } else {
                roleText = "ADMIN";
            }

            System.out.printf("  Current Session: %s (%s, ID: %s)\n",
                    currentUser.getName(), roleText, currentUser.getUserId());
        } else {
            System.out.println("  Current Session: Guest (Options available for all users)");
        }

        System.out.println("----------------------------------------------------");
        System.out.println(" 1. Register Admin");
        System.out.println(" 2. Database Statistics");
        System.out.println(" 3. Register Customer");

        if (currentUser != null) {
            System.out.printf(" 4. Manage Account / Logout (%s)\n", currentUser.getName());
        } else {
            System.out.println(" 4. Login");
        }

        System.out.println(" 5. View All Vehicles");
        System.out.println(" 6. Search Vehicles");
        System.out.println(" 7. Compare Vehicles");
        System.out.println(" 8. Book Vehicle");
        System.out.println(" 9. View My Bookings");
        System.out.println("10. Cancel Booking");
        System.out.println("11. Recently Viewed Vehicles");
        System.out.println("12. Exit");
        System.out.println("----------------------------------------------------");
    }

    private void handleRegisterCustomer() {
        System.out.println("\n----------------------------------------------------");
        System.out.println("           3. CUSTOMER REGISTRATION                 ");
        System.out.println("----------------------------------------------------");

        System.out.print("Enter Full Name: ");
        String name = readLine();

        System.out.print("Enter Email Address (e.g. user@example.com): ");
        String email = readLine();

        System.out.print("Enter Phone Number (min 7 digits): ");
        String phone = readLine();

        System.out.print("Enter Password: ");
        String password = readLine();

        System.out.print("Enter Driving License Number (e.g. DL-12345): ");
        String license = readLine();

        try {
            Customer registered = userService.registerCustomer(
                    name, email, phone, password, license
            );

            System.out.println("\nCustomer registered successfully in MySQL!\n");
            System.out.println("Customer ID   : " + registered.getUserId());
            System.out.println("Name          : " + registered.getName());
            System.out.println("License Number: " + registered.getDrivingLicenseNumber() + "\n");

            System.out.printf("Would you like to log in as %s now? (y/N): ",
                    registered.getName());

            String autoLogin = readLine();

            if ("y".equalsIgnoreCase(autoLogin)) {
                this.currentUser = registered;
                System.out.println("[Session] You are now logged in as "
                        + registered.getName() + ".\n");
            }

        } catch (Exception e) {
            System.out.println("\n[Registration Error] " + e.getMessage() + "\n");
        }
    }

    private void handleRegisterAdmin() {
        System.out.println("\n----------------------------------------------------");
        System.out.println("              ADMIN REGISTRATION                    ");
        System.out.println("----------------------------------------------------");

        System.out.print("Enter Full Name: ");
        String name = readLine();

        System.out.print("Enter Email Address: ");
        String email = readLine();

        System.out.print("Enter Phone Number: ");
        String phone = readLine();

        System.out.print("Enter Password: ");
        String password = readLine();

        System.out.print("Enter Department: ");
        String department = readLine();

        System.out.print("Enter Admin Role: ");
        String adminRole = readLine();

        try {
            Admin admin = userService.registerAdmin(
                    name,
                    email,
                    phone,
                    password,
                    department,
                    adminRole
            );

            System.out.println("\nAdmin registered successfully in MySQL!");
            System.out.println("Admin ID   : " + admin.getUserId());
            System.out.println("Name       : " + admin.getName());
            System.out.println("Email      : " + admin.getEmail());
            System.out.println("Department : " + admin.getDepartment());
            System.out.println("Admin Role : " + admin.getAdminRole());

        } catch (Exception e) {
            System.out.println("\n[Admin Registration Error] " + e.getMessage());
        }
    }

    private void handleAccountOrLogin() {
        System.out.println("\n----------------------------------------------------");
        System.out.println("          4. LOGIN & SESSION MANAGEMENT             ");
        System.out.println("----------------------------------------------------");

        if (currentUser != null) {
            System.out.println("Currently logged in as: "
                    + currentUser.getName() + " (" + currentUser.getEmail() + ")");

            System.out.println("1. Logout");
            System.out.println("2. Switch Account");
            System.out.println("3. Return to Main Menu");

            int opt = readInt("Select option (1-3): ", 1, 3);

            if (opt == 1) {
                System.out.println("[Session] Logged out successfully from "
                        + currentUser.getName() + ".\n");
                currentUser = null;
            } else if (opt == 2) {
                currentUser = null;
                performLogin();
            }

        } else {
            performLogin();
        }
    }

    private void performLogin() {
        System.out.print("Enter Email Address: ");
        String email = readLine();

        System.out.print("Enter Password: ");
        String password = readLine();

        User user = userService.authenticate(email, password);

        if (user != null) {
            currentUser = user;
            System.out.println("\n[Success] Login successful! Welcome back, "
                    + user.getName() + ".\n");
        } else {
            System.out.println("\n[Login Failed] Invalid email or password. Please verify credentials.\n");
        }
    }

    private void handleViewVehicles() {
        System.out.println("\n----------------------------------------------------");
        System.out.println("                5. VEHICLE CATALOG                  ");
        System.out.println("----------------------------------------------------");

        System.out.println("1. View All Fleet Vehicles");
        System.out.println("2. View Available Vehicles Only");
        System.out.println("3. Sort by Price (Low to High)");
        System.out.println("4. Sort by Price (High to Low)");
        System.out.println("5. Sort by Rating (Highest First)");
        System.out.println("6. Sort by Name (Alphabetical)");
        System.out.println("7. Return to Main Menu");

        int choice = readInt("Enter choice (1-7): ", 1, 7);

        switch (choice) {
            case 1:
                List<Vehicle> list = vehicleService.getAllVehicles();
                printVehicleTable("All Fleet Vehicles", list);
                break;

            case 2:
                List<Vehicle> availableVehicles = new ArrayList<>();

                for (Vehicle v : vehicleService.getAllVehicles()) {
                    if (v.isAvailable()) {
                        availableVehicles.add(v);
                    }
                }

                printVehicleTable("Available Vehicles", availableVehicles);
                break;

            case 3:
                vehicleService.displayVehiclesSortedByPriceAsc();
                break;

            case 4:
                vehicleService.displayVehiclesSortedByPriceDesc();
                break;

            case 5:
                vehicleService.displayVehiclesSortedByRatingDesc();
                break;

            case 6:
                vehicleService.displayVehiclesSortedByName();
                break;

            default:
                break;
        }
    }

    private void printVehicleTable(String title, List<Vehicle> list) {
        System.out.println("\n==================== " + title + " (" + list.size() + ") ====================");

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

    private void handleSearchVehicles() {
        System.out.println("\n----------------------------------------------------");
        System.out.println("                6. SEARCH VEHICLES                  ");
        System.out.println("----------------------------------------------------");

        System.out.print("Enter search keyword (Brand, Model, or Car/Bike): ");
        String keyword = readLine();

        List<Vehicle> results = vehicleService.searchVehicles(keyword);

        if (results.isEmpty()) {
            System.out.println("No matching vehicles found for '" + keyword + "'.\n");
        } else {
            printVehicleTable("Search Results for '" + keyword + "'", results);

            for (Vehicle v : results) {
                vehicleService.recordRecentlyViewed(v);
            }
        }
    }

    private void handleCompareVehicles() {
        System.out.println("\n----------------------------------------------------");
        System.out.println("                7. COMPARE VEHICLES                 ");
        System.out.println("----------------------------------------------------");

        System.out.print("Enter First Vehicle ID (e.g. V101): ");
        String id1 = readLine();

        System.out.print("Enter Second Vehicle ID (e.g. V102, B201): ");
        String id2 = readLine();

        vehicleService.compareVehicles(id1, id2);
    }

    private void handleBookVehicle() {
        System.out.println("\n----------------------------------------------------");
        System.out.println("                8. BOOK A VEHICLE                   ");
        System.out.println("----------------------------------------------------");

        Customer customerToBook;

        if (currentUser instanceof Customer) {
            Customer c = (Customer) currentUser;

            System.out.printf("Current Customer: %s (ID: %s)\n",
                    c.getName(), c.getUserId());

            System.out.print("Book for this account? (Y/n): ");
            String ans = readLine();

            if (ans.isEmpty() || ans.equalsIgnoreCase("y")) {
                customerToBook = c;
            } else {
                customerToBook = promptForCustomer();
            }

        } else {
            customerToBook = promptForCustomer();
        }

        if (customerToBook == null) {
            return;
        }

        System.out.print("Enter Vehicle ID to book (e.g. V101, B201): ");
        String vehicleId = readLine();

        Vehicle vehicle = vehicleService.searchVehicleById(vehicleId);

        if (vehicle == null) {
            System.out.println("[Error] Vehicle with ID '" + vehicleId + "' does not exist.\n");
            return;
        }

        if (!vehicle.isAvailable()) {
            System.out.println("[Error] Vehicle '" + vehicle.getName()
                    + "' is currently unavailable for rent.\n");
            return;
        }

        vehicleService.recordRecentlyViewed(vehicle);

        System.out.print("Enter Rental Duration in Days (min 1): ");
        int days = readInt("", 1, 365);

        double totalAmount = vehicle.getPricePerDay() * days;

        System.out.println("\n================ BOOKING QUOTATION ================");
        System.out.println("Customer       : " + customerToBook.getName()
                + " [" + customerToBook.getUserId() + "]");
        System.out.println("Vehicle        : " + vehicle.getName()
                + " [" + vehicle.getVehicleId() + "]");
        System.out.println("Daily Rate     : $" + String.format("%.2f", vehicle.getPricePerDay()) + " / day");
        System.out.println("Duration       : " + days + " day(s)");
        System.out.println("Total Amount   : $" + String.format("%.2f", totalAmount));
        System.out.println("===================================================");

        System.out.print("Confirm this booking? (y/N): ");
        String confirm = readLine();

        if ("y".equalsIgnoreCase(confirm)) {
            try {
                Booking booking = bookingService.createBooking(customerToBook, vehicle, days);

                System.out.println("\n[Success] Vehicle booked successfully!");
                booking.displayBookingDetails();

            } catch (Exception e) {
                System.out.println("\n[Booking Error] " + e.getMessage() + "\n");
            }

        } else {
            System.out.println("[Cancelled] Booking discarded.\n");
        }
    }

    private Customer promptForCustomer() {
        System.out.print("Enter Customer User ID: ");
        String uid = readLine();

        User u = userService.findUserById(uid);

        if (u instanceof Customer) {
            return (Customer) u;
        }

        System.out.println("[Error] Customer with ID '" + uid
                + "' not found. Please register first.\n");

        return null;
    }

    private void handleViewMyBookings() {
        System.out.println("\n----------------------------------------------------");
        System.out.println("                9. VIEW BOOKINGS                    ");
        System.out.println("----------------------------------------------------");

        String customerId;

        if (currentUser instanceof Customer) {
            Customer c = (Customer) currentUser;
            customerId = c.getUserId();

            System.out.println("Showing booking history for logged-in customer: "
                    + c.getName() + " [" + customerId + "]");

        } else {
            System.out.print("Enter Customer User ID to lookup: ");
            customerId = readLine();
        }

        List<Booking> list = bookingService.getBookingsByCustomerId(customerId);

        if (list.isEmpty()) {
            System.out.println("No booking records found for customer ID: "
                    + customerId + "\n");
        } else {
            System.out.println("\n========== Booking History (" + list.size() + ") ==========");

            for (Booking b : list) {
                b.displayBookingDetails();
            }

            System.out.println("====================================================\n");
        }
    }

    private void handleCancelBooking() {
        System.out.println("\n----------------------------------------------------");
        System.out.println("               10. CANCEL A BOOKING                  ");
        System.out.println("----------------------------------------------------");

        System.out.print("Enter Booking ID to cancel (e.g. BK-1001): ");
        String bookingId = readLine();

        try {
            boolean success = bookingService.cancelBooking(bookingId);

            if (success) {
                System.out.println("\n[Success] Booking " + bookingId + " has been cancelled.");
                vehicleService.loadVehiclesFromDatabase();

            }

        } catch (Exception e) {
            System.out.println("\n[Cancellation Error] " + e.getMessage() + "\n");
        }
    }

    private void handleRecentlyViewedVehicles() {
        System.out.println("\n----------------------------------------------------");
        System.out.println("           11. RECENTLY VIEWED VEHICLES              ");
        System.out.println("----------------------------------------------------");

        List<Vehicle> recent = vehicleService.getRecentlyViewedVehicles();

        if (recent.isEmpty()) {
            System.out.println("No vehicles viewed recently in this session.\n");
        } else {
            printVehicleTable("Recently Viewed Vehicles", recent);
        }
    }

    private String readLine() {
        String input = scanner.nextLine();

        if (input != null) {
            return input.trim();
        }

        return "";
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            if (!prompt.isEmpty()) {
                System.out.print(prompt);
            }

            String line = readLine();

            try {
                int val = Integer.parseInt(line);

                if (val >= min && val <= max) {
                    return val;
                }

                System.out.printf(
                        "[Validation Error] Please enter a number between %d and %d.\n",
                        min, max);

            } catch (NumberFormatException e) {
                System.out.println("[Validation Error] Invalid input. Please enter a valid integer.");
            }
        }
    }
}