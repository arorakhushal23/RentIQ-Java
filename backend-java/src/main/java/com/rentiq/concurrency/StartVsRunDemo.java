package com.rentiq.concurrency;

/**
 * Educational demonstration contrasting start() vs run() in Java multithreading:
 * - calling run() directly executes sequentially on the calling thread (e.g. main)
 *   without starting a new thread of execution.
 * - calling start() schedules a new OS/JVM thread and executes run() asynchronously.
 */
public class StartVsRunDemo {

    public static void runDemo() {
        System.out.println("\n============================================================");
        System.out.println("   Multithreading Demo: start() vs run() Execution          ");
        System.out.println("============================================================");

        Runnable task = () -> {
            System.out.println("  -> Task executing on Thread: " + Thread.currentThread().getName() +
                               " (ID: " + Thread.currentThread().threadId() + ")");
        };

        System.out.println("1. Invoking run() directly (Synchronous method call on caller thread):");
        System.out.println("   Calling thread before: " + Thread.currentThread().getName());
        Thread directThread = new Thread(task, "Direct-Run-Worker");
        directThread.run(); // Executes on caller thread!
        System.out.println("   Calling thread after:  " + Thread.currentThread().getName());

        System.out.println("\n2. Invoking start() (Asynchronous new thread creation):");
        System.out.println("   Calling thread before: " + Thread.currentThread().getName());
        Thread asyncThread = new Thread(task, "Async-Worker-Thread");
        asyncThread.start(); // Spawns new thread!
        try {
            asyncThread.join(); // Wait for completion
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("   Calling thread after async worker joined: " + Thread.currentThread().getName());
        System.out.println("============================================================\n");
    }
}
