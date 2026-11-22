package com.rentiq.concurrency;

import java.util.concurrent.CountDownLatch;

/**
 * Demonstrates all 6 Java Thread.State lifecycle states:
 * 1. NEW: Thread instantiated but start() has not been called.
 * 2. RUNNABLE: Thread executing in the JVM (actively running or ready to run).
 * 3. TIMED_WAITING: Thread sleeping or waiting with a specified timeout (e.g. Thread.sleep).
 * 4. WAITING: Thread waiting indefinitely for another thread (e.g. join() without timeout).
 * 5. BLOCKED: Thread waiting to acquire an intrinsic monitor lock held by another thread.
 * 6. TERMINATED: Thread has finished run() execution.
 */
public class ThreadLifecycleDemo {

    public static void runDemo() {
        System.out.println("\n============================================================");
        System.out.println("   Multithreading Demo: Complete Thread Lifecycle States    ");
        System.out.println("============================================================");

        try {
            // 1. State: NEW
            Thread t1 = new Thread(() -> {}, "State-NEW-Thread");
            System.out.printf("1. State: %-15s -> Thread instantiated before start()\n", t1.getState());

            // 2. State: RUNNABLE
            java.util.concurrent.atomic.AtomicBoolean stopRunnable = new java.util.concurrent.atomic.AtomicBoolean(false);
            CountDownLatch runStartLatch = new CountDownLatch(1);
            Thread t2 = new Thread(() -> {
                runStartLatch.countDown();
                while (!stopRunnable.get()) {
                    Thread.onSpinWait();
                }
            }, "State-RUNNABLE-Thread");
            t2.start();
            runStartLatch.await();
            System.out.printf("2. State: %-15s -> Thread active and running\n", t2.getState());
            stopRunnable.set(true);
            t2.join();

            // 3. State: TIMED_WAITING
            Thread t3 = new Thread(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    // Expected interrupt
                }
            }, "State-TIMED_WAITING-Thread");
            t3.start();
            Thread.sleep(100); // Allow thread to enter sleep
            System.out.printf("3. State: %-15s -> Thread in Thread.sleep(timeout)\n", t3.getState());
            t3.interrupt();
            t3.join();

            // 4. State: WAITING
            Thread blocker = new Thread(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    // Expected interrupt
                }
            }, "Blocker-Thread");
            blocker.start();

            CountDownLatch waitLatch = new CountDownLatch(1);
            Thread t4 = new Thread(() -> {
                waitLatch.countDown();
                try {
                    blocker.join(); // Waiting without timeout on blocker
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "State-WAITING-Thread");
            t4.start();
            waitLatch.await();
            Thread.sleep(100);
            System.out.printf("4. State: %-15s -> Thread blocked in join() without timeout\n", t4.getState());
            blocker.interrupt();
            t4.join();
            blocker.join();

            // 5. State: BLOCKED
            Object monitorLock = new Object();
            CountDownLatch lockHeldLatch = new CountDownLatch(1);
            CountDownLatch blockAttemptLatch = new CountDownLatch(1);

            Thread lockHolder = new Thread(() -> {
                synchronized (monitorLock) {
                    lockHeldLatch.countDown();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "LockHolder-Thread");

            Thread t5 = new Thread(() -> {
                blockAttemptLatch.countDown();
                synchronized (monitorLock) {
                    // Blocked until lockHolder releases monitorLock
                }
            }, "State-BLOCKED-Thread");

            lockHolder.start();
            lockHeldLatch.await();
            t5.start();
            blockAttemptLatch.await();
            Thread.sleep(100);
            System.out.printf("5. State: %-15s -> Thread waiting to acquire synchronized monitor lock\n", t5.getState());
            lockHolder.join();
            t5.join();

            // 6. State: TERMINATED
            Thread t6 = new Thread(() -> {}, "State-TERMINATED-Thread");
            t6.start();
            t6.join();
            System.out.printf("6. State: %-15s -> Thread completed execution\n", t6.getState());

        } catch (InterruptedException e) {
            System.out.println("[LifecycleDemo Error] Interrupted during demonstration: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        System.out.println("============================================================\n");
    }
}
