package com.rentiq.model;

/**
 * Interface representing bookable rental assets in the RentIQ system.
 * Defines the contract for booking lifecycle operations (booking and cancellation),
 * allowing any conforming asset to be booked uniformly without tight coupling.
 */
public interface Bookable {

    /**
     * Attempts to book the asset.
     * @return true if successfully booked; false if already booked / unavailable.
     */
    boolean bookVehicle();

    /**
     * Cancels an existing booking on the asset, restoring its availability.
     * @return true if successfully cancelled; false if not currently booked.
     */
    boolean cancelBooking();

    /**
     * Checks whether the asset is currently available for rental.
     * @return true if available; false otherwise.
     */
    boolean isAvailable();
}
