package com.rentiq.concurrency;

import com.rentiq.exception.InvalidBookingException;
import com.rentiq.exception.VehicleNotAvailableException;
import com.rentiq.model.Booking;
import com.rentiq.model.Customer;
import com.rentiq.model.Vehicle;
import com.rentiq.service.BookingService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Educational demonstration of Race Conditions and Synchronization in vehicle rentals:
 * 1. Unsafe Concurrent Booking:
 *    Simulates two customers simultaneously attempting to book the SAME vehicle without synchronization.
 *    Shows how both threads observe available == true before either updates it, causing a Double-Booking race condition.
 * 2. Synchronized Booking:
 *    Protects the entire critical section (check availability -> update availability -> record booking)
 *    using intrinsic synchronization. Ensures exactly ONE customer successfully books, while the other receives
 *    a proper VehicleNotAvailableException.
 */
public class RaceConditionDemo {

    /**
     * Demonstrates an intentional, controlled race condition on vehicle availability.
     */
    public static void demoUnsafeBooking(Customer customer1, Customer customer2, Vehicle targetVehicle) {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("   1. Unsafe Concurrent Booking (Race Condition Simulation)  ");
        System.out.println("------------------------------------------------------------");
        System.out.println("Target Vehicle: " + targetVehicle.getBrand() + " " + targetVehicle.getModel() +
                           " [" + targetVehicle.getVehicleId() + "] (Available: " + targetVehicle.isAvailable() + ")");

        // Reset vehicle availability
        targetVehicle.updateAvailability(true);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);
        AtomicInteger successfulBookings = new AtomicInteger(0);

        Runnable bookingTask = () -> {
            try {
                startLatch.await(); // Both threads start at the exact same instant
                String threadName = Thread.currentThread().getName();

                // Step 1: Check availability (Unsynchronized!)
                if (targetVehicle.isAvailable()) {
                    System.out.printf("[%s] Observed vehicle %s as AVAILABLE!\n", threadName, targetVehicle.getVehicleId());

                    // Artificial delay to widen race condition window and guarantee context switch
                    Thread.sleep(50);

                    // Step 2: Book vehicle (Both threads reach here before either sets false!)
                    targetVehicle.bookVehicle();
                    successfulBookings.incrementAndGet();
                    System.out.printf("[%s] [RACE CONDITION SUCCESS] Completed reservation for vehicle %s!\n",
                        threadName, targetVehicle.getVehicleId());
                } else {
                    System.out.printf("[%s] Vehicle %s was UNAVAILABLE.\n", threadName, targetVehicle.getVehicleId());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                doneLatch.countDown();
            }
        };

        Thread t1 = new Thread(bookingTask, "Customer-Thread-" + customer1.getName());
        Thread t2 = new Thread(bookingTask, "Customer-Thread-" + customer2.getName());

        t1.start();
        t2.start();

        startLatch.countDown(); // Release both threads simultaneously

        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nUnsafe Booking Result Summary:");
        System.out.println("  Successful Bookings Count: " + successfulBookings.get() + " (Expected safe limit: 1)");
        if (successfulBookings.get() > 1) {
            System.out.println("  CRITICAL DEFECT: Double-Booking Race Condition occurred! Both customers booked the same vehicle!");
        }
        System.out.println("------------------------------------------------------------\n");
    }

    /**
     * Demonstrates thread-safe synchronized booking using BookingService.bookVehicleSynchronized.
     */
    public static void demoSynchronizedBooking(BookingService bookingService, Customer customer1, Customer customer2, Vehicle targetVehicle) {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("   2. Synchronized Thread-Safe Booking Solution            ");
        System.out.println("------------------------------------------------------------");
        System.out.println("Target Vehicle: " + targetVehicle.getBrand() + " " + targetVehicle.getModel() +
                           " [" + targetVehicle.getVehicleId() + "] (Available: " + targetVehicle.isAvailable() + ")");

        // Reset vehicle availability
        targetVehicle.updateAvailability(true);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        Runnable safeBookingTask = () -> {
            try {
                startLatch.await(); // Synchronized release
                String customerName = Thread.currentThread().getName();

                // Call thread-safe synchronized booking method on BookingService
                Customer c = customerName.contains(customer1.getName()) ? customer1 : customer2;
                Booking booking = bookingService.bookVehicleSynchronized(c, targetVehicle, 3);
                if (booking != null) {
                    successCount.incrementAndGet();
                    System.out.printf("[%s] [SUCCESS] Reserved vehicle %s -> Booking ID: %s\n",
                        Thread.currentThread().getName(), targetVehicle.getVehicleId(), booking.getBookingId());
                }
            } catch (VehicleNotAvailableException e) {
                conflictCount.incrementAndGet();
                System.out.printf("[%s] [SAFE REJECTION] Caught VehicleNotAvailableException: %s\n",
                    Thread.currentThread().getName(), e.getMessage());
            } catch (InvalidBookingException | InterruptedException e) {
                System.out.printf("[%s] [ERROR] %s\n", Thread.currentThread().getName(), e.getMessage());
            } finally {
                doneLatch.countDown();
            }
        };

        Thread t1 = new Thread(safeBookingTask, "Client-" + customer1.getName());
        Thread t2 = new Thread(safeBookingTask, "Client-" + customer2.getName());

        t1.start();
        t2.start();

        startLatch.countDown(); // Launch both threads concurrently

        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nSynchronized Booking Result Summary:");
        System.out.println("  Successful Bookings Count: " + successCount.get() + " (Exactly 1 allowed)");
        System.out.println("  Conflict Rejections Count: " + conflictCount.get() + " (Safely rejected)");
        System.out.println("  Vehicle Status in Fleet  : " + (targetVehicle.isAvailable() ? "Available" : "Rented / Unavailable"));
        System.out.println("  Result                   : Complete mutual exclusion achieved. No double-booking!");
        System.out.println("------------------------------------------------------------\n");
    }
}
