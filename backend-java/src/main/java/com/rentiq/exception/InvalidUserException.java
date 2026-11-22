package com.rentiq.exception;

/**
 * Custom checked exception thrown when user registration or profile data (e.g. invalid email format or blank name) fails validation.
 */
public class InvalidUserException extends Exception {
    public InvalidUserException(String message) {
        super(message);
    }

    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
