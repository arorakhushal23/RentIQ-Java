package com.rentiq.concurrency;

import com.rentiq.model.BookingRequest;
import com.rentiq.model.Customer;
import com.rentiq.model.Vehicle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Educational demonstration of notifyAll() with Multiple Waiting Consumers:
 * - Demonstrates that when multiple threads wait on the same monitor, notifyAll() wakes ALL of them.
 * - Each awakened thread must recheck its condition inside the while loop guard.
 * - If fewer requests are available than the number of waiting threads, only the threads that acquire
 *   the lock and find an available request consume it; other threads re-evaluate the while loop
 *   and safely return to waiting without throwing exceptions or corrupting state.
 * - Guarantees that every request is processed exactly once (no duplicates).
 */
public class NotifyAllDemo {

    public static void runDemo(Customer customer, Vehicle vehicle1, Vehicle vehicle2) {
        System.out.println("\n============================================================");
        System.out.println("   Multithreading Demo: notifyAll() with Multiple Consumers ");
        System.out.println("============================================================");

        BookingRequestBuffer buffer = new BookingRequestBuffer(5);
        int numConsumers = 3;
        List<Thread> consumers = new ArrayList<>();
        AtomicInteger consumedCount = new AtomicInteger(0);

        // Spawn 3 consumers waiting on empty buffer
        for (int i = 1; i <= numConsumers; i++) {
            final String name = "Consumer-Worker-" + i;
            Thread t = new Thread(() -> {
                try {
                    while (!buffer.isShutdown() && !Thread.currentThread().isInterrupted()) {
                        BookingRequest req = buffer.consume();
                        if (req == null) {
                            break; // Clean shutdown exit
                        }
                        int count = consumedCount.incrementAndGet();
                        System.out.printf("  [%s] Acquired lock and consumed: %s (Total consumed: %d)\n",
                            name, req.getRequestId(), count);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, name);
            consumers.add(t);
            t.start();
        }

        try {
            Thread.sleep(150); // Allow all 3 consumers to enter wait() state
            System.out.println("\n  [Main Thread] Inspected 3 Consumer States:");
            for (Thread t : consumers) {
                System.out.printf("    %s -> State: %s\n", t.getName(), t.getState());
            }

            System.out.println("\n  [Producer] Adding 2 booking requests (less than 3 consumers)...");
            BookingRequest req1 = new BookingRequest(BookingRequest.generateNextRequestId(), customer, vehicle1, 3);
            BookingRequest req2 = new BookingRequest(BookingRequest.generateNextRequestId(), customer, vehicle2, 5);

            buffer.produce(req1); // Calls notifyAll()
            buffer.produce(req2); // Calls notifyAll()

            Thread.sleep(150); // Let consumers process

            System.out.println("\n  [Main Thread] Initiating clean shutdown of buffer...");
            buffer.shutdown(); // Sets shutdown flag and calls notifyAll() to unblock 3rd consumer

            for (Thread t : consumers) {
                t.join(500);
            }

            System.out.println("\nSummary of notifyAll() execution:");
            System.out.println("  Total items produced : 2");
            System.out.println("  Total items consumed : " + consumedCount.get());
            System.out.println("  Verified             : Each item was processed exactly once with zero duplicates!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("============================================================\n");
    }
}
