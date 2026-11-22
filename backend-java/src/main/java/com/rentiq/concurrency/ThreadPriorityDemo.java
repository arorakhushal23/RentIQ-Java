package com.rentiq.concurrency;

/**
 * Demonstrates thread priorities in Java:
 * - Thread.MIN_PRIORITY (1)
 * - Thread.NORM_PRIORITY (5)
 * - Thread.MAX_PRIORITY (10)
 *
 * Explains that priority serves as a hint to the JVM/OS thread scheduler
 * and does NOT guarantee execution sequence or time slicing.
 */
public class ThreadPriorityDemo {

    public static void runDemo() {
        System.out.println("\n============================================================");
        System.out.println("   Multithreading Demo: Java Thread Priorities (1, 5, 10)   ");
        System.out.println("============================================================");

        Runnable priorityTask = () -> {
            Thread current = Thread.currentThread();
            System.out.printf("  Executing [%s] | Priority Configured: %2d | Active State: %s\n",
                current.getName(), current.getPriority(), current.getState());
        };

        Thread lowThread = new Thread(priorityTask, "LowPriority-Worker");
        Thread normThread = new Thread(priorityTask, "NormPriority-Worker");
        Thread highThread = new Thread(priorityTask, "HighPriority-Worker");

        // Configure priorities
        lowThread.setPriority(Thread.MIN_PRIORITY);   // 1
        normThread.setPriority(Thread.NORM_PRIORITY); // 5
        highThread.setPriority(Thread.MAX_PRIORITY);  // 10

        System.out.println("Configured Threads:");
        System.out.println("  " + lowThread.getName() + " -> Priority: " + lowThread.getPriority() + " (MIN_PRIORITY = 1)");
        System.out.println("  " + normThread.getName() + " -> Priority: " + normThread.getPriority() + " (NORM_PRIORITY = 5)");
        System.out.println("  " + highThread.getName() + " -> Priority: " + highThread.getPriority() + " (MAX_PRIORITY = 10)");

        System.out.println("\nStarting threads concurrently:");
        lowThread.start();
        normThread.start();
        highThread.start();

        try {
            lowThread.join();
            normThread.join();
            highThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n[Note on Thread Scheduling]:");
        System.out.println("  In Java, thread priorities are hints to the underlying OS scheduler.");
        System.out.println("  They do not guarantee that high-priority threads will always run before lower-priority ones.");
        System.out.println("============================================================\n");
    }
}
