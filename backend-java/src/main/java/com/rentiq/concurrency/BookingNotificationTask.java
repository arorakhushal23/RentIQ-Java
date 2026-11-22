package com.rentiq.concurrency;

import com.rentiq.model.Booking;

/**
 * Asynchronous customer notification task implementing Runnable.
 * Demonstrates:
 * - Runnable interface implementation
 * - Passing Runnable task to a Thread instance
 * - Using sleep() to simulate external notification service latency
 * - Handling InterruptedException
 */
public class BookingNotificationTask implements Runnable {
    private final Booking booking;
    private final String channel; // e.g. "EMAIL", "SMS"

    public BookingNotificationTask(Booking booking, String channel) {
        this.booking = booking;
        this.channel = channel;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.printf("[%s] Preparing %s notification for booking %s (Customer: %s)...\n",
            threadName, channel, booking.getBookingId(), booking.getCustomer().getName());
        try {
            // Simulate network transmission delay
            Thread.sleep(120);
            System.out.printf("[%s] [NOTIFICATION SENT] Dispatched %s to %s <%s> for %s %s [%s]. Total: $%.2f\n",
                threadName, channel, booking.getCustomer().getName(), booking.getCustomer().getEmail(),
                booking.getVehicle().getBrand(), booking.getVehicle().getModel(),
                booking.getVehicle().getVehicleId(), booking.getTotalAmount());
        } catch (InterruptedException e) {
            System.out.printf("[%s] Notification task interrupted for booking %s.\n", threadName, booking.getBookingId());
            Thread.currentThread().interrupt();
        }
    }
}
