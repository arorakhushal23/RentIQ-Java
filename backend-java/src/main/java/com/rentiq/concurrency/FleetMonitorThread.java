package com.rentiq.concurrency;

import com.rentiq.model.Vehicle;
import com.rentiq.service.VehicleService;

/**
 * Background daemon or worker thread extending Thread.
 * Periodically monitors fleet availability and demonstrates:
 * - Thread subclassing (Extending Thread)
 * - run() and start()
 * - getName() and setName()
 * - sleep() and clean interrupt() handling
 * - isDaemon() and graceful termination
 */
public class FleetMonitorThread extends Thread {
    private final VehicleService vehicleService;
    private final long intervalMillis;
    private volatile boolean running = true;

    public FleetMonitorThread(VehicleService vehicleService, long intervalMillis) {
        super("FleetMonitor-Thread");
        this.vehicleService = vehicleService;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void run() {
        System.out.printf("[%s] Fleet Monitor Thread started (Daemon: %b, Priority: %d)\n",
            getName(), isDaemon(), getPriority());
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                int availableCount = 0;
                for (Vehicle v : vehicleService.getAllVehicles()) {
                    if (v.isAvailable()) {
                        availableCount++;
                    }
                }
                System.out.printf("[%s] Periodic Audit: %d / %d vehicles available in fleet.\n",
                    getName(), availableCount, vehicleService.getVehicleCount());

                Thread.sleep(intervalMillis);
            }
        } catch (InterruptedException e) {
            System.out.printf("[%s] Monitor received interruption during sleep. Exiting cleanly...\n", getName());
            // Restore interrupt flag per Java best practices
            Thread.currentThread().interrupt();
        } finally {
            System.out.printf("[%s] Fleet Monitor Thread terminated gracefully.\n", getName());
        }
    }

    public void stopMonitoring() {
        this.running = false;
        this.interrupt();
    }
}
