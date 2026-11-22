package com.rentiq.exception;

/**
 * Custom checked exception thrown when attempting to book a vehicle that is currently unavailable or rented out.
 */
public class VehicleNotAvailableException extends Exception {
    private String vehicleId;

    public VehicleNotAvailableException(String message) {
        super(message);
    }

    public VehicleNotAvailableException(String message, String vehicleId) {
        super(message);
        this.vehicleId = vehicleId;
    }

    public String getVehicleId() {
        return this.vehicleId;
    }
}
