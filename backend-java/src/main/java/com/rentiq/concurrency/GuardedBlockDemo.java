package com.rentiq.concurrency;

/**
 * Educational demonstration explaining Guarded Blocks in Java:
 * - A guarded block polls a condition that must be true before executing actions.
 * - Guard condition is ALWAYS checked inside a 'while' loop, never an 'if' statement:
 *
 *     synchronized (lock) {
 *         while (!condition) {
 *             lock.wait(); // Releases lock and waits
 *         }
 *         // Perform action when condition is verified
 *     }
 *
 * Reasons why 'while' is strictly required:
 * 1. Spurious Wakeups: The JVM/OS thread scheduler may wake a thread without an explicit notify/notifyAll.
 * 2. Race Conditions / Stolen Signals: Another thread may wake up first, acquire the lock, and invalidate
 *    the condition before this thread gets scheduled.
 * 3. Contract Safety: Ensures the thread never executes past the guard unless the condition is genuinely true.
 */
public class GuardedBlockDemo {

    public static void runDemo() {
        System.out.println("\n============================================================");
        System.out.println("   Multithreading Demo: Guarded Blocks & Condition Testing   ");
        System.out.println("============================================================");

        final Object monitor = new Object();
        final boolean[] permissionGranted = { false };

        Thread waitingWorker = new Thread(() -> {
            synchronized (monitor) {
                System.out.println("  [GuardedWorker] Checking permission condition in guarded block...");
                // Guarded Block using while loop
                while (!permissionGranted[0]) {
                    System.out.println("  [GuardedWorker] Condition is false (permissionGranted == false).");
                    System.out.println("  [GuardedWorker] Entering monitor.wait(). Lock is released now.");
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    System.out.println("  [GuardedWorker] Woken up! Re-checking condition in while loop guard...");
                }
                System.out.println("  [GuardedWorker] Condition verified TRUE! Proceeding with critical work.");
            }
        }, "GuardedWorker-Thread");

        waitingWorker.start();

        try {
            Thread.sleep(150); // Ensure worker enters wait()
            System.out.println("\n  [SignalingThread] Acquiring monitor lock to update condition...");
            synchronized (monitor) {
                permissionGranted[0] = true;
                System.out.println("  [SignalingThread] Set permissionGranted = true.");
                System.out.println("  [SignalingThread] Invoking monitor.notify(). Note: lock NOT released yet!");
                monitor.notify();
                System.out.println("  [SignalingThread] Exiting synchronized block. Lock released now.");
            }
            waitingWorker.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("============================================================\n");
    }
}
