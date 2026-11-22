package com.rentiq.model;

/**
 * Represents a pending booking request submitted by a customer.
 * Used across Queue implementations:
 * - Queue: Standard FIFO request processing.
 * - Deque: Dual-ended request scheduling (front express / rear regular).
 * - PriorityQueue: Priority scheduling based on customer membership tier with FIFO sequence tie-breaking.
 */
public class BookingRequest {
    private static long sequenceCounter = 0;

    private final String requestId;
    private final Customer customer;
    private final Vehicle vehicle;
    private final int rentalDays;
    private final long sequenceNumber;
    private final int priorityScore;
    private final long requestTimestamp;

    public BookingRequest(String requestId, Customer customer, Vehicle vehicle, int rentalDays) {
        this.requestId = requestId;
        this.customer = customer;
        this.vehicle = vehicle;
        this.rentalDays = rentalDays;
        this.sequenceNumber = ++sequenceCounter;
        this.priorityScore = calculatePriorityScore(customer);
        this.requestTimestamp = System.currentTimeMillis();
    }

    public static synchronized String generateNextRequestId() {
        return "REQ-" + (1000 + sequenceCounter + 1);
    }

    private static int calculatePriorityScore(Customer customer) {
        if (customer == null || customer.getMembershipLevel() == null) {
            return 1;
        }
        String membership = customer.getMembershipLevel().trim().toUpperCase();
        if (membership.contains("PLATINUM") || membership.contains("VIP")) {
            return 3; // High Priority
        } else if (membership.contains("GOLD")) {
            return 2; // Medium Priority
        } else {
            return 1; // Regular / Standard Priority
        }
    }

    public String getRequestId() {
        return requestId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    public String getPriorityLabel() {
        switch (priorityScore) {
            case 3:
                return "HIGH (VIP)";
            case 2:
                return "MEDIUM (Gold)";
            default:
                return "REGULAR (Standard)";
        }
    }

    public void displayRequestDetails() {
        System.out.printf("[%s] Seq #%d | Priority: %s | Customer: %s (%s) | Vehicle: %s %s [%s] | Duration: %d days\n",
            requestId, sequenceNumber, getPriorityLabel(),
            customer != null ? customer.getName() : "N/A",
            customer != null ? customer.getMembershipLevel() : "N/A",
            vehicle != null ? vehicle.getBrand() : "N/A",
            vehicle != null ? vehicle.getModel() : "N/A",
            vehicle != null ? vehicle.getVehicleId() : "N/A",
            rentalDays);
    }

    @Override
    public String toString() {
        return String.format("[%s | #%d | %s | %s | %s | %d days]",
            requestId, sequenceNumber, getPriorityLabel(),
            customer != null ? customer.getName() : "N/A",
            vehicle != null ? vehicle.getVehicleId() : "N/A",
            rentalDays);
    }
}
