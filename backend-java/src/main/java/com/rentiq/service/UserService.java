package com.rentiq.service;

import com.rentiq.exception.InvalidUserException;
import com.rentiq.model.Admin;
import com.rentiq.model.Customer;
import com.rentiq.model.User;

/**
 * Service managing user registration and queries using standard arrays.
 * Demonstrates String handling, validation, custom exceptions, StringBuilder, and StringBuffer.
 */
public class UserService {
    private User[] users;
    private int userCount;
    private static int userCounter = 1000;
    private static final int DEFAULT_CAPACITY = 10;

    // StringBuffer demonstrating mutable thread-safe string buffer for audit history
    private StringBuffer auditLog;

    public UserService() {
        this(DEFAULT_CAPACITY);
    }

    public UserService(int capacity) {
        this.users = new User[capacity];
        this.userCount = 0;
        this.auditLog = new StringBuffer("=== User Service Audit Log ===\n");
    }

    /**
     * Validates user credentials and input parameters using Core Java String operations.
     * Demonstrates string methods: trim(), isEmpty(), contains(), indexOf(), startsWith(), endsWith().
     */
    public void validateUserInput(String name, String email, String phone) throws InvalidUserException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserException("Validation Failed: User name cannot be empty or null.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserException("Validation Failed: Email address cannot be empty.");
        }
        String cleanEmail = email.trim();
        int atIndex = cleanEmail.indexOf('@');
        int dotIndex = cleanEmail.lastIndexOf('.');
        if (atIndex <= 0 || dotIndex <= atIndex + 1 || dotIndex >= cleanEmail.length() - 1) {
            throw new InvalidUserException("Validation Failed: Invalid email format '" + email + "'. Must follow user@domain.com");
        }
        if (phone == null || phone.trim().isEmpty() || phone.trim().length() < 7) {
            throw new InvalidUserException("Validation Failed: Invalid phone number. Must contain at least 7 digits.");
        }
    }

    /**
     * Registers a new customer after input validation.
     * Uses StringBuilder to format profile details.
     */
    public Customer registerCustomer(String name, String email, String phone, String password,
                                    String drivingLicense, String membershipLevel) throws InvalidUserException {
        validateUserInput(name, email, phone);

        if (drivingLicense == null || drivingLicense.trim().isEmpty()) {
            throw new InvalidUserException("Validation Failed: Customer must provide a valid driving license.");
        }

        String userId = "U" + (++userCounter);
        Customer customer = new Customer(userId, name.trim(), email.trim(), password, phone.trim(),
                                         drivingLicense.trim(), (membershipLevel != null && !membershipLevel.trim().isEmpty()) ? membershipLevel.trim() : "Standard");

        addUserToArray(customer);

        // Demonstrate StringBuilder for constructing formatted message
        StringBuilder sb = new StringBuilder();
        sb.append("[UserService] Successfully registered Customer: ")
          .append(customer.getName())
          .append(" (ID: ").append(customer.getUserId())
          .append(", License: ").append(customer.getDrivingLicenseNumber()).append(")");
        System.out.println(sb.toString());

        // Demonstrate StringBuffer thread-safe log appending
        auditLog.append(System.currentTimeMillis()).append(" - Registered Customer: ").append(userId).append("\n");

        return customer;
    }

    /**
     * Registers an administrator.
     */
    public Admin registerAdmin(String name, String email, String phone, String password,
                               String department, String adminRole) throws InvalidUserException {
        validateUserInput(name, email, phone);

        String userId = "A" + (++userCounter);
        Admin admin = new Admin(userId, name.trim(), email.trim(), password, phone.trim(),
                                (department != null ? department.trim() : "General"),
                                (adminRole != null ? adminRole.trim() : "Staff"));

        addUserToArray(admin);
        auditLog.append(System.currentTimeMillis()).append(" - Registered Admin: ").append(userId).append("\n");
        return admin;
    }

    /**
     * Searches a user by ID using case-insensitive String comparison (equalsIgnoreCase).
     */
    public User findUserById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        for (int i = 0; i < userCount; i++) {
            if (users[i].getUserId().equalsIgnoreCase(userId.trim())) {
                return users[i];
            }
        }
        return null;
    }

    /**
     * Displays all users using StringBuilder.
     */
    public void displayAllUsers() {
        System.out.println("\n============== Registered RentIQ Users (" + userCount + ") ==============");
        if (userCount == 0) {
            System.out.println("No users currently registered.");
            System.out.println("============================================================\n");
            return;
        }
        for (int i = 0; i < userCount; i++) {
            users[i].displayUserInfo();
        }
        System.out.println("============================================================\n");
    }

    private void addUserToArray(User user) {
        if (userCount >= users.length) {
            User[] expanded = new User[users.length * 2];
            for (int i = 0; i < users.length; i++) {
                expanded[i] = users[i];
            }
            users = expanded;
        }
        users[userCount] = user;
        userCount++;
    }

    public int getUserCount() {
        return this.userCount;
    }

    public String getAuditLog() {
        return this.auditLog.toString();
    }
}
