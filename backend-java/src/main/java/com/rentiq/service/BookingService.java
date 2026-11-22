package com.rentiq.service;

import com.rentiq.model.Booking;
import com.rentiq.model.Customer;
import com.rentiq.model.Vehicle;

/**
 * Service class for handling vehicle booking operations in the RentIQ system.
 * Uses standard arrays for storing bookings without Collections.
 * Implements validation, availability management, rental calculation, and cancellation.
 */
public class BookingService {
    private Booking[] bookings;
    private int bookingCount;
    private static final int INITIAL_CAPACITY = 10;

    public BookingService() {
        this(INITIAL_CAPACITY);
    }

    public BookingService(int capacity) {
        this.bookings = new Booking[capacity];
        this.bookingCount = 0;
    }

    // 1. Check vehicle availability
    public boolean checkVehicleAvailability(Vehicle vehicle) {
        if (vehicle == null) {
            System.out.println("[BookingService] Error: Vehicle reference is null.");
            return false;
        }
        return vehicle.isAvailable();
    }

    // 2. Calculate rental amount
    public double calculateRentalAmount(Vehicle vehicle, int rentalDays) {
        if (vehicle == null) {
            System.out.println("[BookingService] Error: Vehicle is null.");
            return 0.0;
        }
        if (rentalDays <= 0) {
            System.out.println("[BookingService] Error: Invalid rental duration (" + rentalDays + " days). Duration must be >= 1.");
            return 0.0;
        }
        return vehicle.calculateRentalCost(rentalDays);
    }

    // 3. Create and confirm booking
    public Booking createBooking(Customer customer, Vehicle vehicle, int rentalDays) {
        System.out.println("\n[BookingService] Processing new booking request...");

        // Validate customer
        if (customer == null) {
            System.out.println("[Error] Booking failed: Customer cannot be null.");
            return null;
        }

        // Validate vehicle
        if (vehicle == null) {
            System.out.println("[Error] Booking failed: Vehicle cannot be null.");
            return null;
        }

        // Validate rental duration
        if (rentalDays <= 0) {
            System.out.println("[Error] Booking failed: Invalid rental duration (" + rentalDays + " days). Must be at least 1 day.");
            return null;
        }

        // Check vehicle availability (Prevent booking unavailable vehicles)
        if (!checkVehicleAvailability(vehicle)) {
            System.out.println("[Error] Booking failed: Vehicle " + vehicle.getBrand() + " " + vehicle.getModel() +
                               " [" + vehicle.getVehicleId() + "] is currently UNAVAILABLE.");
            return null;
        }

        // Create Booking object
        Booking booking = new Booking(customer, vehicle, rentalDays);

        // Confirm booking & update vehicle availability
        boolean confirmed = confirmBooking(booking);
        if (!confirmed) {
            System.out.println("[Error] Booking confirmation failed.");
            return null;
        }

        // Store in array
        addBookingToArray(booking);

        System.out.println("[Success] Booking successfully created and confirmed! Booking ID: " + booking.getBookingId());
        return booking;
    }

    // 4. Confirm booking and update vehicle availability
    public boolean confirmBooking(Booking booking) {
        if (booking == null) {
            return false;
        }
        Vehicle vehicle = booking.getVehicle();
        if (vehicle != null && vehicle.isAvailable()) {
            vehicle.bookVehicle(); // Bookable interface method updates availability
            booking.setBookingStatus("CONFIRMED");
            return true;
        }
        return false;
    }

    // 5. Cancel booking and restore vehicle availability
    public boolean cancelBooking(String bookingId) {
        System.out.println("\n[BookingService] Processing cancellation for Booking ID: " + bookingId);
        Booking booking = searchBookingById(bookingId);

        if (booking == null) {
            System.out.println("[Error] Cancellation failed: Booking ID '" + bookingId + "' not found.");
            return false;
        }

        if ("CANCELLED".equalsIgnoreCase(booking.getBookingStatus())) {
            System.out.println("[Warning] Booking '" + bookingId + "' is already cancelled.");
            return false;
        }

        // Update booking status
        booking.setBookingStatus("CANCELLED");

        // Restore vehicle availability
        Vehicle vehicle = booking.getVehicle();
        if (vehicle != null) {
            vehicle.cancelBooking(); // Bookable interface method restores availability
            System.out.println("[Success] Booking " + bookingId + " cancelled successfully. Availability restored for " +
                               vehicle.getBrand() + " " + vehicle.getModel() + " [" + vehicle.getVehicleId() + "].");
        }
        return true;
    }

    // 6. Display booking details
    public void displayBookingDetails(String bookingId) {
        Booking booking = searchBookingById(bookingId);
        if (booking != null) {
            booking.displayBookingDetails();
        } else {
            System.out.println("[BookingService] Booking ID '" + bookingId + "' not found.");
        }
    }

    // Display all bookings
    public void displayAllBookings() {
        System.out.println("\n============== System Bookings Registry (" + bookingCount + ") ==============");
        if (bookingCount == 0) {
            System.out.println("No bookings recorded in the system.");
            System.out.println("============================================================\n");
            return;
        }
        for (int i = 0; i < bookingCount; i++) {
            bookings[i].displayBookingDetails();
        }
        System.out.println("============================================================\n");
    }

    // Search booking by ID using linear search across array
    public Booking searchBookingById(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            return null;
        }
        for (int i = 0; i < bookingCount; i++) {
            if (bookings[i].getBookingId().equalsIgnoreCase(bookingId.trim())) {
                return bookings[i];
            }
        }
        return null;
    }

    // Internal helper to add booking into array
    private void addBookingToArray(Booking booking) {
        if (bookingCount >= bookings.length) {
            // Expand array dynamically
            Booking[] expanded = new Booking[bookings.length * 2];
            for (int i = 0; i < bookings.length; i++) {
                expanded[i] = bookings[i];
            }
            bookings = expanded;
        }
        bookings[bookingCount] = booking;
        bookingCount++;
    }

    public int getBookingCount() {
        return this.bookingCount;
    }
}
