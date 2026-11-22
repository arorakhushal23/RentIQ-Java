package com.rentiq.concurrency;

import com.rentiq.model.BookingRequest;
import com.rentiq.model.Customer;
import com.rentiq.model.Vehicle;
import java.util.concurrent.CountDownLatch;

/**
 * Educational demonstration of wait() and notify() in Java thread coordination:
 * 1. Consumer thread starts first, inspects shared buffer, finds it empty, and calls wait().
 * 2. Calling wait() causes consumer to atomically release the monitor lock and enter WAITING state.
 * 3. Producer thread acquires monitor lock, deposits a BookingRequest, and invokes notify().
 * 4. Producer finishes its synchronized block, releasing the lock.
 * 5. Consumer wakes up, re-acquires lock, verifies the while loop guard, and processes the request.
 */
public class WaitNotifyDemo {

    public static void runDemo(Customer customer, Vehicle vehicle) {
        System.out.println("\n============================================================");
        System.out.println("   Multithreading Demo: wait() and notify() Single-Consumer ");
        System.out.println("============================================================");

        BookingRequestBuffer buffer = new BookingRequestBuffer(5);
        CountDownLatch consumerWaitingLatch = new CountDownLatch(1);

        Thread consumerThread = new Thread(() -> {
            try {
                System.out.println("  [Consumer] Starting up. Checking buffer...");
                consumerWaitingLatch.countDown();
                // Blocks in wait() because buffer is currently empty
                BookingRequest req = buffer.consume();
                if (req != null) {
                    System.out.println("  [Consumer] Successfully retrieved: " + req.getRequestId() +
                                       " for customer " + req.getCustomer().getName());
                }
            } catch (InterruptedException e) {
                System.out.println("  [Consumer] Interrupted during wait().");
                Thread.currentThread().interrupt();
            }
        }, "WaitNotify-Consumer");

        consumerThread.start();

        try {
            consumerWaitingLatch.await(); // Ensure consumer has launched and is checking buffer
            Thread.sleep(150); // Allow consumer to enter wait() state
            System.out.printf("  [Main Thread] Inspected Consumer State: %s (Confirmed in wait)\n\n",
                consumerThread.getState());

            Thread producerThread = new Thread(() -> {
                try {
                    BookingRequest request = new BookingRequest(
                        BookingRequest.generateNextRequestId(), customer, vehicle, 2);

                    System.out.println("  [Producer] Submitting new request: " + request.getRequestId());
                    buffer.produce(request); // Produces and calls notifyAll() / notify()
                    System.out.println("  [Producer] produce() completed. Monitor lock released.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "WaitNotify-Producer");

            producerThread.start();
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("============================================================\n");
    }
}
