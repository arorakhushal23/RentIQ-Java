package com.rentiq;

import com.rentiq.model.Bike;
import com.rentiq.model.Bookable;
import com.rentiq.model.Booking;
import com.rentiq.model.Car;
import com.rentiq.model.Customer;
import com.rentiq.model.Vehicle;
import com.rentiq.service.BookingService;

public class Main {
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("   RentIQ - Vehicle Rental System (Booking & Abstraction)   ");
        System.out.println("============================================================\n");

        // ------------------------------------------------------------
        // 1. Setup Customers and Vehicles
        // ------------------------------------------------------------
        System.out.println(">>> 1. Initializing Customers and Fleet Vehicles:");
        Customer customer1 = new Customer("C001", "Alice Johnson", "alice@example.com", "pass123", "+1-555-0101", "DL-98765-XYZ", "Gold VIP");
        Customer customer2 = new Customer("C002", "Bob Smith", "bob@example.com", "secret456", "+1-555-0102", "DL-12345-ABC", "Regular");

        // Abstract class references (Vehicle) pointing to concrete instances (Car, Bike)
        Vehicle teslaCar = new Car("V101", "Tesla", "Model 3", "TS-01-EV", 85.0, true, 5, "Electric");
        Vehicle yamahaBike = new Bike("B201", "Yamaha", "YZF-R3", "YM-01-BK", 45.0, true, 321, true);

        System.out.println("Customers created:");
        customer1.displayUserInfo();
        customer2.displayUserInfo();

        System.out.println("Vehicles in fleet:");
        teslaCar.displayVehicleDetails();
        yamahaBike.displayVehicleDetails();

        System.out.println("\n------------------------------------------------------------\n");

        // ------------------------------------------------------------
        // 2. Complete Booking Flow Demonstration
        // ------------------------------------------------------------
        System.out.println(">>> 2. Complete Booking Flow (Customer Alice rents Tesla Car):");
        BookingService bookingService = new BookingService();

        int rentalDaysAlice = 5;
        System.out.println("Selected Customer : " + customer1.getName());
        System.out.println("Selected Vehicle  : " + teslaCar.getBrand() + " " + teslaCar.getModel());
        System.out.println("Rental Duration   : " + rentalDaysAlice + " days");

        // Calculate rental cost before confirming
        double calculatedCost = bookingService.calculateRentalAmount(teslaCar, rentalDaysAlice);
        System.out.println("Calculated Cost   : $" + String.format("%.2f", calculatedCost));

        // Create and confirm booking
        Booking booking1 = bookingService.createBooking(customer1, teslaCar, rentalDaysAlice);

        // Display booking details
        if (booking1 != null) {
            bookingService.displayBookingDetails(booking1.getBookingId());
        }

        // Verify vehicle availability is now false
        System.out.println("Current Tesla availability after booking: " + (teslaCar.isAvailable() ? "Available" : "Rented / Unavailable"));

        System.out.println("\n------------------------------------------------------------\n");

        // ------------------------------------------------------------
        // 3. Testing Edge Case: Booking an Unavailable Vehicle
        // ------------------------------------------------------------
        System.out.println(">>> 3. Test: Attempting to Book an Unavailable Vehicle:");
        System.out.println("Customer Bob attempts to book the same Tesla (V101) while Alice has it rented...");
        Booking unavailableBookingAttempt = bookingService.createBooking(customer2, teslaCar, 3);
        System.out.println("Booking attempt result: " + (unavailableBookingAttempt == null ? "REJECTED as expected." : "ERROR: Unexpected booking created!"));

        System.out.println("\n------------------------------------------------------------\n");

        // ------------------------------------------------------------
        // 4. Testing Edge Case: Invalid Rental Duration
        // ------------------------------------------------------------
        System.out.println(">>> 4. Test: Attempting Booking with Invalid Rental Duration:");
        System.out.println("Customer Bob attempts to book Yamaha Bike with duration of 0 days...");
        Booking invalidDurationAttempt1 = bookingService.createBooking(customer2, yamahaBike, 0);
        System.out.println("0-day attempt result: " + (invalidDurationAttempt1 == null ? "REJECTED as expected." : "ERROR: Unexpected booking created!"));

        System.out.println("\nCustomer Bob attempts to book Yamaha Bike with negative duration (-3 days)...");
        Booking invalidDurationAttempt2 = bookingService.createBooking(customer2, yamahaBike, -3);
        System.out.println("Negative duration attempt result: " + (invalidDurationAttempt2 == null ? "REJECTED as expected." : "ERROR: Unexpected booking created!"));

        System.out.println("\n------------------------------------------------------------\n");

        // ------------------------------------------------------------
        // 5. Booking Cancellation and Availability Restoration
        // ------------------------------------------------------------
        System.out.println(">>> 5. Test: Booking Cancellation and Vehicle Availability Restoration:");
        System.out.println("Alice cancels her booking (" + booking1.getBookingId() + ")...");
        boolean cancelSuccess = bookingService.cancelBooking(booking1.getBookingId());
        System.out.println("Cancellation status: " + (cancelSuccess ? "SUCCESS" : "FAILED"));

        // Verify vehicle availability is restored
        System.out.println("Current Tesla availability after cancellation: " + (teslaCar.isAvailable() ? "Available" : "Rented / Unavailable"));
        teslaCar.displayVehicleDetails();

        // Bob now successfully books the newly available Tesla
        System.out.println("Bob attempts to book the freed Tesla (V101) for 4 days...");
        Booking booking2 = bookingService.createBooking(customer2, teslaCar, 4);
        if (booking2 != null) {
            bookingService.displayBookingDetails(booking2.getBookingId());
        }

        System.out.println("\n------------------------------------------------------------\n");

        // ------------------------------------------------------------
        // 6. Demonstrating Interface (Bookable) and Static Members
        // ------------------------------------------------------------
        System.out.println(">>> 6. Demonstrating Interface (Bookable) & Static Members:");
        
        // Interface reference holding concrete object
        Bookable bookableBike = yamahaBike;
        System.out.println("Bookable Bike initial availability: " + bookableBike.isAvailable());
        bookableBike.bookVehicle();
        System.out.println("Bookable Bike after bookVehicle(): " + bookableBike.isAvailable());
        bookableBike.cancelBooking();
        System.out.println("Bookable Bike after cancelBooking(): " + bookableBike.isAvailable());

        // Static members demonstration
        System.out.println("\n--- Static Members Summary ---");
        System.out.println("Total Bookings created across application lifetime: " + Booking.getTotalBookingsCount());
        System.out.println("Active bookings in BookingService array: " + bookingService.getBookingCount());

        // Display all bookings recorded
        bookingService.displayAllBookings();

        System.out.println("============================================================");
        System.out.println("   RentIQ Phase 1 Commit 3 Demonstration Completed Successfully ");
        System.out.println("============================================================");
    }
}
