package com.rentiq.concurrency;

import com.rentiq.model.BookingRequest;
import com.rentiq.model.Customer;
import com.rentiq.model.Vehicle;
import com.rentiq.service.BookingService;
import com.rentiq.service.VehicleService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Complete Producer-Consumer workflow simulation integrated with RentIQ:
 * - BookingProducer submits customer booking requests into a bounded BookingRequestBuffer.
 * - Multiple BookingConsumer workers wait via wait(), wake via notifyAll(), and execute
 *   thread-safe vehicle reservations using BookingService.bookVehicleSynchronized.
 * - Demonstrates bounded buffer coordination, condition testing in guarded blocks,
 *   mutual exclusion, and graceful thread shutdown.
 */
public class ProducerConsumerDemo {

    public static void runSimulation(BookingService bookingService, VehicleService vehicleService,
                                     Customer c1, Customer c2, Customer c3) {
        System.out.println("\n============================================================");
        System.out.println("   RentIQ Integrated Producer-Consumer Workflow Simulation  ");
        System.out.println("============================================================");

        // Bounded buffer with capacity 3 to demonstrate both empty and full queue coordination
        BookingRequestBuffer buffer = new BookingRequestBuffer(3);

        // Prepare 5 booking requests across different vehicles
        List<BookingRequest> requests = new ArrayList<>();
        Vehicle v1 = vehicleService.searchVehicleById("V103");
        Vehicle v2 = vehicleService.searchVehicleById("V104");
        Vehicle v3 = vehicleService.searchVehicleById("B201");
        Vehicle v4 = vehicleService.searchVehicleById("B202");
        Vehicle v5 = vehicleService.searchVehicleById("V105");

        if (v1 != null) requests.add(new BookingRequest(BookingRequest.generateNextRequestId(), c1, v1, 2));
        if (v2 != null) requests.add(new BookingRequest(BookingRequest.generateNextRequestId(), c2, v2, 3));
        if (v3 != null) requests.add(new BookingRequest(BookingRequest.generateNextRequestId(), c3, v3, 1));
        if (v4 != null) requests.add(new BookingRequest(BookingRequest.generateNextRequestId(), c1, v4, 4));
        if (v5 != null) requests.add(new BookingRequest(BookingRequest.generateNextRequestId(), c2, v5, 5));

        AtomicInteger processedCounter = new AtomicInteger(0);

        // Create 2 consumer workers
        BookingConsumer consumerTask1 = new BookingConsumer(buffer, bookingService, "BookingWorker-1", processedCounter);
        BookingConsumer consumerTask2 = new BookingConsumer(buffer, bookingService, "BookingWorker-2", processedCounter);

        Thread cThread1 = new Thread(consumerTask1, "BookingWorker-1");
        Thread cThread2 = new Thread(consumerTask2, "BookingWorker-2");

        cThread1.start();
        cThread2.start();

        // Create producer with 50ms delay between productions
        BookingProducer producerTask = new BookingProducer(buffer, requests, 50);
        Thread pThread = new Thread(producerTask, "Producer-Agent");
        pThread.start();

        try {
            // Wait for producer to finish producing all 5 requests
            pThread.join();

            // Allow consumers to finish draining the buffer
            while (!buffer.isEmpty()) {
                Thread.sleep(30);
            }

            // Signal buffer shutdown and wake any idle waiting consumers
            buffer.shutdown();

            // Wait for consumers to cleanly terminate
            cThread1.join();
            cThread2.join();

            System.out.println("\nProducer-Consumer Workflow Summary:");
            System.out.println("  Total Requests Submitted : " + requests.size());
            System.out.println("  Total Requests Processed : " + processedCounter.get());
            System.out.println("  Buffer Final State       : " + (buffer.isEmpty() ? "Empty (Drained)" : "Not Empty"));
            System.out.println("  All Worker Threads Joined: YES (Clean termination achieved)");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("============================================================\n");
    }
}
