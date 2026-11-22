package com.rentiq.exception;

/**
 * Custom checked exception thrown when booking parameters (such as invalid duration or missing associations) fail business validation.
 */
public class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }

    public InvalidBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
