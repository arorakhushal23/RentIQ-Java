package com.rentiq.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Counter implementations for demonstrating Atomicity:
 * 1. UnsafeCounter: non-atomic ++ operator leads to lost updates in concurrent scenarios.
 * 2. SynchronizedCounter: synchronized method provides mutual exclusion and atomic updates.
 * 3. AtomicCounter: AtomicInteger provides lock-free hardware-level atomic operations (CAS).
 */
public class SynchronizedCounter {

    public static class UnsafeCounter {
        private int count = 0;

        public void increment() {
            // Read-Modify-Write is NOT atomic!
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    public static class SyncCounter {
        private int count = 0;

        public synchronized void increment() {
            // Synchronized ensures mutual exclusion and atomicity
            count++;
        }

        public synchronized int getCount() {
            return count;
        }
    }

    public static class AtomicCounter {
        private final AtomicInteger count = new AtomicInteger(0);

        public void increment() {
            // Lock-free Compare-And-Swap (CAS)
            count.incrementAndGet();
        }

        public int getCount() {
            return count.get();
        }
    }
}
