package com.rentiq.concurrency;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the core concurrency concepts:
 * 1. Atomicity: Unsafe Counter vs Synchronized Counter vs AtomicInteger.
 *    Shows lost updates in unsynchronized counter due to non-atomic read-modify-write.
 * 2. Visibility: Demonstrates how volatile variables guarantee immediate memory visibility
 *    across multiple CPU cores and avoid stale cached values.
 */
public class AtomicityVisibilityDemo {

    public static void runDemo() {
        System.out.println("\n============================================================");
        System.out.println("   Multithreading Demo: Atomicity and Visibility            ");
        System.out.println("============================================================");

        demonstrateAtomicity();
        demonstrateVisibility();

        System.out.println("============================================================\n");
    }

    /**
     * Demonstrates Atomicity: 10 threads, each incrementing 1,000 times (Expected = 10,000).
     */
    public static void demonstrateAtomicity() {
        System.out.println("--- 1. Atomicity Demonstration (10 Threads x 1,000 increments = 10,000 expected) ---");
        int numThreads = 10;
        int incrementsPerThread = 1000;
        int expected = numThreads * incrementsPerThread;

        SynchronizedCounter.UnsafeCounter unsafe = new SynchronizedCounter.UnsafeCounter();
        SynchronizedCounter.SyncCounter sync = new SynchronizedCounter.SyncCounter();
        SynchronizedCounter.AtomicCounter atomic = new SynchronizedCounter.AtomicCounter();

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    unsafe.increment();
                    sync.increment();
                    atomic.increment();
                }
            });
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Expected Final Count         : " + expected);
        System.out.printf("1. Unsafe Counter (count++)   : %-6d (Lost Updates: %s)\n",
            unsafe.getCount(), (unsafe.getCount() != expected ? "YES - Race Condition detected!" : "None (rare lucky run)"));
        System.out.printf("2. Synchronized Counter       : %-6d (Thread-safe mutual exclusion)\n", sync.getCount());
        System.out.printf("3. AtomicInteger Counter      : %-6d (Lock-free CAS atomic)\n", atomic.getCount());
    }

    /**
     * Demonstrates Memory Visibility using volatile stop flag.
     */
    public static void demonstrateVisibility() {
        System.out.println("\n--- 2. Visibility Demonstration (Volatile Stop Flag) ---");

        class VolatileWorker implements Runnable {
            // volatile guarantees reads and writes happen directly to main memory, avoiding CPU register caching
            private volatile boolean stopRequested = false;
            private long loopCount = 0;

            @Override
            public void run() {
                System.out.println("  [VolatileWorker] Background thread started running...");
                while (!stopRequested) {
                    loopCount++;
                }
                System.out.printf("  [VolatileWorker] Stop flag observed! Loop terminated cleanly at count: %,d\n", loopCount);
            }

            public void stop() {
                this.stopRequested = true;
            }
        }

        VolatileWorker worker = new VolatileWorker();
        Thread workerThread = new Thread(worker, "Volatile-Worker");
        workerThread.start();

        try {
            Thread.sleep(50); // Let worker loop spin
            System.out.println("  [Main Thread] Requesting worker thread to stop via volatile flag...");
            worker.stop();
            workerThread.join(1000); // Wait for worker to see flag and exit
            System.out.println("  [Main Thread] Worker thread successfully joined and terminated.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
