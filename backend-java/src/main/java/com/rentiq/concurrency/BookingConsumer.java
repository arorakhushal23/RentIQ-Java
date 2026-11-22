package com.rentiq.concurrency;

import com.rentiq.exception.InvalidBookingException;
import com.rentiq.exception.VehicleNotAvailableException;
import com.rentiq.model.Booking;
import com.rentiq.model.BookingRequest;
import com.rentiq.service.BookingService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Consumer worker task implementing Runnable.
 * Retrieves BookingRequest instances from the shared BookingRequestBuffer and
 * executes thread-safe vehicle reservations through BookingService.bookVehicleSynchronized.
 */
public class BookingConsumer implements Runnable {
    private final BookingRequestBuffer buffer;
    private final BookingService bookingService;
    private final AtomicInteger processedCount;
    private final String consumerName;

    public BookingConsumer(BookingRequestBuffer buffer, BookingService bookingService, String consumerName, AtomicInteger processedCount) {
        this.buffer = buffer;
        this.bookingService = bookingService;
        this.consumerName = consumerName;
        this.processedCount = processedCount;
    }

    @Override
    public void run() {
        System.out.printf("[%s] BookingConsumer worker started, waiting for requests...\n", consumerName);

        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Blocks via wait() if buffer is empty; returns null on buffer shutdown
                BookingRequest req = buffer.consume();
                if (req == null) {
                    // Buffer was shut down and drained
                    break;
                }

                // Process booking via synchronized booking service to prevent double-booking
                try {
                    System.out.printf("[%s] Processing request %s for %s (%s)...\n",
                        consumerName, req.getRequestId(), req.getCustomer().getName(), req.getVehicle().getVehicleId());

                    Booking booking = bookingService.bookVehicleSynchronized(
                        req.getCustomer(), req.getVehicle(), req.getRentalDays());

                    if (booking != null) {
                        processedCount.incrementAndGet();
                        System.out.printf("[%s] [BOOKING CONFIRMED] Booking ID: %s | Vehicle: %s %s | Total: $%.2f\n",
                            consumerName, booking.getBookingId(),
                            booking.getVehicle().getBrand(), booking.getVehicle().getModel(),
                            booking.getTotalAmount());
                    }
                } catch (VehicleNotAvailableException e) {
                    processedCount.incrementAndGet();
                    System.out.printf("[%s] [BOOKING REJECTED] Vehicle unavailable: %s\n",
                        consumerName, e.getMessage());
                } catch (InvalidBookingException e) {
                    processedCount.incrementAndGet();
                    System.out.printf("[%s] [BOOKING ERROR] Invalid parameters: %s\n",
                        consumerName, e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            System.out.printf("[%s] Consumer received interruption signal during wait. Terminating cleanly...\n", consumerName);
            Thread.currentThread().interrupt();
        } finally {
            System.out.printf("[%s] BookingConsumer worker terminated.\n", consumerName);
        }
    }
}
