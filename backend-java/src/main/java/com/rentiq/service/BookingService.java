package com.rentiq.service;

import com.rentiq.exception.InvalidBookingException;
import com.rentiq.exception.VehicleNotAvailableException;
import com.rentiq.model.Booking;
import com.rentiq.model.Customer;
import com.rentiq.model.Vehicle;

/**
 * Service class for handling vehicle booking operations in the RentIQ system.
 * Uses standard arrays for storing bookings without Collections.
 * Demonstrates Wrapper classes (Integer, Double, Boolean), autoboxing, unboxing,
 * custom exceptions (VehicleNotAvailableException, InvalidBookingException),
 * and thread-safe StringBuffer for audit logging.
 */
public class BookingService {
    private Booking[] bookings;
    private int bookingCount;
    private static final int INITIAL_CAPACITY = 10;

    // StringBuffer demonstrating mutable thread-safe audit logging
    private StringBuffer bookingAuditLog;

    public BookingService() {
        this(INITIAL_CAPACITY);
    }

    public BookingService(int capacity) {
        this.bookings = new Booking[capacity];
        this.bookingCount = 0;
        this.bookingAuditLog = new StringBuffer("=== Booking Service Audit Log ===\n");
    }

    // 1. Check vehicle availability
    public boolean checkVehicleAvailability(Vehicle vehicle) {
        if (vehicle == null) {
            return false;
        }
        return vehicle.isAvailable();
    }

    /**
     * Calculates rental amount demonstrating wrapper classes, autoboxing, and unboxing.
     */
    public double calculateRentalAmount(Vehicle vehicle, int rentalDays) throws InvalidBookingException {
        if (vehicle == null) {
            throw new InvalidBookingException("Calculation Error: Vehicle cannot be null.");
        }

        // Wrapper class demonstration: Autoboxing int primitive to Integer object
        Integer daysObj = Integer.valueOf(rentalDays);

        if (daysObj.intValue() <= 0) {
            throw new InvalidBookingException("Calculation Error: Invalid rental duration (" + daysObj + " days). Must be at least 1 day.");
        }

        // Wrapper class demonstration: Autoboxing double primitive to Double object
        Double rateObj = Double.valueOf(vehicle.getPricePerDay());

        // Wrapper class demonstration: Unboxing both Double and Integer to primitive double
        double total = rateObj.doubleValue() * daysObj.intValue();
        return total;
    }

    /**
     * Creates and confirms a new vehicle booking.
     * Throws custom checked exceptions on invalid input or unavailable vehicles.
     * Demonstrates try-catch-finally and StringBuffer audit logging.
     */
    public Booking createBooking(Customer customer, Vehicle vehicle, int rentalDays)
            throws InvalidBookingException, VehicleNotAvailableException {

        // Validate customer
        if (customer == null) {
            throw new InvalidBookingException("Booking Creation Failed: Customer reference cannot be null.");
        }

        // Validate vehicle
        if (vehicle == null) {
            throw new InvalidBookingException("Booking Creation Failed: Vehicle reference cannot be null.");
        }

        // Validate rental duration with wrapper class
        Integer durationWrapper = Integer.valueOf(rentalDays); // Autoboxing
        int unboxedDuration = durationWrapper;                  // Unboxing
        if (unboxedDuration <= 0) {
            throw new InvalidBookingException("Booking Creation Failed: Rental duration (" + unboxedDuration + " days) must be at least 1 day.");
        }

        // Check vehicle availability
        if (!checkVehicleAvailability(vehicle)) {
            throw new VehicleNotAvailableException(
                "Booking Creation Failed: Vehicle " + vehicle.getBrand() + " " + vehicle.getModel() +
                " [" + vehicle.getVehicleId() + "] is currently UNAVAILABLE.",
                vehicle.getVehicleId()
            );
        }

        Booking booking = null;
        try {
            // Instantiate booking
            booking = new Booking(customer, vehicle, unboxedDuration);

            // Confirm booking
            boolean confirmed = confirmBooking(booking);
            if (!confirmed) {
                throw new InvalidBookingException("Booking Confirmation Failed: Unable to lock vehicle availability.");
            }

            // Store in array
            addBookingToArray(booking);

            // StringBuffer thread-safe logging
            bookingAuditLog.append(System.currentTimeMillis())
                           .append(" - Confirmed Booking [")
                           .append(booking.getBookingId())
                           .append("] for Vehicle [")
                           .append(vehicle.getVehicleId())
                           .append("] by User [")
                           .append(customer.getUserId())
                           .append("] Total: $")
                           .append(String.format("%.2f", booking.getTotalAmount()))
                           .append("\n");

            System.out.println("[BookingService] Booking successfully created! Booking ID: " + booking.getBookingId());
        } finally {
            // Demonstrate finally block execution
            System.out.println("[Audit] createBooking transaction completed for customer: " + customer.getName());
        }

        return booking;
    }

    // 4. Confirm booking
    public boolean confirmBooking(Booking booking) {
        if (booking == null) {
            return false;
        }
        Vehicle vehicle = booking.getVehicle();
        if (vehicle != null && vehicle.isAvailable()) {
            vehicle.bookVehicle();
            booking.setBookingStatus("CONFIRMED");
            return true;
        }
        return false;
    }

    /**
     * Cancels an existing booking and restores vehicle availability.
     * Throws InvalidBookingException if booking is not found or already cancelled.
     */
    public boolean cancelBooking(String bookingId) throws InvalidBookingException {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new InvalidBookingException("Cancellation Failed: Booking ID cannot be empty.");
        }

        Booking booking = searchBookingById(bookingId.trim());
        if (booking == null) {
            throw new InvalidBookingException("Cancellation Failed: Booking with ID '" + bookingId + "' not found.");
        }

        if ("CANCELLED".equalsIgnoreCase(booking.getBookingStatus())) {
            throw new InvalidBookingException("Cancellation Failed: Booking '" + bookingId + "' is already cancelled.");
        }

        try {
            booking.setBookingStatus("CANCELLED");
            Vehicle vehicle = booking.getVehicle();
            if (vehicle != null) {
                vehicle.cancelBooking();
                System.out.println("[BookingService] Booking " + bookingId + " cancelled. Availability restored for " +
                                   vehicle.getBrand() + " " + vehicle.getModel() + " [" + vehicle.getVehicleId() + "].");
            }
            bookingAuditLog.append(System.currentTimeMillis())
                           .append(" - Cancelled Booking [")
                           .append(bookingId)
                           .append("]\n");
            return true;
        } finally {
            System.out.println("[Audit] cancelBooking process finished for Booking ID: " + bookingId);
        }
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

    private void addBookingToArray(Booking booking) {
        if (bookingCount >= bookings.length) {
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

    public String getAuditLog() {
        return this.bookingAuditLog.toString();
    }
}
