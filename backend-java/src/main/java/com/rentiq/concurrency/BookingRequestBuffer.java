package com.rentiq.concurrency;

import com.rentiq.model.BookingRequest;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Thread-safe bounded buffer for pending BookingRequest instances.
 * Implements the classic Producer-Consumer pattern using Java thread coordination:
 * - synchronized monitor lock
 * - wait() when buffer is full (producer) or empty (consumer)
 * - notify() / notifyAll() to signal waiting threads upon state change
 * - guarded blocks with while loops to recheck conditions and prevent spurious wakeups
 * - graceful shutdown signaling
 */
public class BookingRequestBuffer {
    private final Queue<BookingRequest> buffer;
    private final int capacity;
    private final Object lock = new Object();
    private volatile boolean shutdown = false;

    public BookingRequestBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Buffer capacity must be strictly positive.");
        }
        this.capacity = capacity;
        this.buffer = new LinkedList<>();
    }

    /**
     * Producer method: Adds a booking request into the shared buffer.
     * Uses a guarded block with wait() when the buffer is full.
     * Signals waiting consumers using notifyAll().
     */
    public boolean produce(BookingRequest request) throws InterruptedException {
        if (request == null) {
            return false;
        }

        synchronized (lock) {
            // Guarded block: MUST recheck condition in a while loop, never an if statement
            while (buffer.size() >= capacity && !shutdown) {
                System.out.printf("[%s] Buffer is FULL (%d/%d). Producer waiting via wait()...\n",
                    Thread.currentThread().getName(), buffer.size(), capacity);
                lock.wait();
            }

            if (shutdown) {
                System.out.printf("[%s] Buffer is in shutdown mode. Producer discarding request: %s\n",
                    Thread.currentThread().getName(), request.getRequestId());
                return false;
            }

            buffer.offer(request);
            System.out.printf("[%s] [PRODUCED] %s for %s (%s) | Buffer size: %d/%d. Calling notifyAll()...\n",
                Thread.currentThread().getName(), request.getRequestId(),
                request.getCustomer().getName(), request.getVehicle().getVehicleId(),
                buffer.size(), capacity);

            // Notify waiting consumers that data is now available
            lock.notifyAll();
            return true;
        }
    }

    /**
     * Consumer method: Retrieves and removes a booking request from the buffer.
     * Uses a guarded block with wait() when the buffer is empty.
     * Signals waiting producers using notifyAll().
     */
    public BookingRequest consume() throws InterruptedException {
        synchronized (lock) {
            // Guarded block: Recheck condition to guard against spurious wakeups
            while (buffer.isEmpty() && !shutdown) {
                System.out.printf("[%s] Buffer is EMPTY (0/%d). Consumer waiting via wait()...\n",
                    Thread.currentThread().getName(), capacity);
                lock.wait();
            }

            // If empty and shutdown was signaled, return null to allow consumer thread to terminate
            if (buffer.isEmpty() && shutdown) {
                System.out.printf("[%s] Buffer empty and shutdown confirmed. Consumer exiting.\n",
                    Thread.currentThread().getName());
                return null;
            }

            BookingRequest request = buffer.poll();
            System.out.printf("[%s] [CONSUMED] %s | Buffer size: %d/%d. Calling notifyAll()...\n",
                Thread.currentThread().getName(), request.getRequestId(), buffer.size(), capacity);

            // Notify waiting producers that a slot has freed up
            lock.notifyAll();
            return request;
        }
    }

    /**
     * Initiates a graceful shutdown of the buffer.
     * Sets shutdown flag and wakes all waiting producers and consumers.
     */
    public void shutdown() {
        synchronized (lock) {
            this.shutdown = true;
            System.out.printf("[%s] [Buffer] Graceful shutdown initiated. Waking all waiting threads via notifyAll()...\n",
                Thread.currentThread().getName());
            lock.notifyAll();
        }
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public int size() {
        synchronized (lock) {
            return buffer.size();
        }
    }

    public boolean isEmpty() {
        synchronized (lock) {
            return buffer.isEmpty();
        }
    }

    public int getCapacity() {
        return capacity;
    }
}
