package com.rentiq.service;

import com.rentiq.exception.InvalidUserException;
import com.rentiq.model.Admin;
import com.rentiq.model.Customer;
import com.rentiq.model.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service managing user registration and queries.
 * Integrates:
 * - ArrayList: dynamic user storage.
 * - HashMap: O(1) User ID to User entity lookup.
 * - LinkedHashMap: tracks recent user registrations in exact insertion order.
 */
public class UserService {
    private List<User> users;
    private Map<String, User> userMap;
    private LinkedHashMap<String, User> recentRegistrations;
    private static int userCounter = 1000;

    // StringBuffer demonstrating mutable thread-safe string buffer for audit history
    private StringBuffer auditLog;

    public UserService() {
        this.users = new ArrayList<>();
        this.userMap = new HashMap<>();
        this.recentRegistrations = new LinkedHashMap<>();
        this.auditLog = new StringBuffer("=== User Service Audit Log ===\n");
    }

    public UserService(int initialCapacity) {
        this.users = new ArrayList<>(initialCapacity);
        this.userMap = new HashMap<>(initialCapacity);
        this.recentRegistrations = new LinkedHashMap<>(initialCapacity);
        this.auditLog = new StringBuffer("=== User Service Audit Log ===\n");
    }

    /**
     * Validates user credentials and input parameters using Core Java String operations.
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
     * Prevents duplicate IDs and synchronizes ArrayList, HashMap, and LinkedHashMap.
     */
    public Customer registerCustomer(String name, String email, String phone, String password,
                                    String drivingLicense, String membershipLevel) throws InvalidUserException {
        validateUserInput(name, email, phone);

        if (drivingLicense == null || drivingLicense.trim().isEmpty()) {
            throw new InvalidUserException("Validation Failed: Customer must provide a valid driving license.");
        }

        String userId = "U" + (++userCounter);

        // Guard against duplicate user IDs
        if (userMap.containsKey(userId.toUpperCase())) {
            throw new InvalidUserException("Registration Failed: Duplicate User ID '" + userId + "' detected.");
        }

        Customer customer = new Customer(userId, name.trim(), email.trim(), password, phone.trim(),
                                         drivingLicense.trim(), (membershipLevel != null && !membershipLevel.trim().isEmpty()) ? membershipLevel.trim() : "Standard");

        // Synchronize list and maps
        users.add(customer);
        userMap.put(customer.getUserId().toUpperCase(), customer);
        recentRegistrations.put(customer.getUserId(), customer);

        StringBuilder sb = new StringBuilder();
        sb.append("[UserService] Successfully registered Customer: ")
          .append(customer.getName())
          .append(" (ID: ").append(customer.getUserId())
          .append(", Tier: ").append(customer.getMembershipLevel())
          .append(", License: ").append(customer.getDrivingLicenseNumber()).append(")");
        System.out.println(sb.toString());

        auditLog.append(System.currentTimeMillis()).append(" - Registered Customer: ").append(userId).append("\n");
        return customer;
    }

    /**
     * Registers an administrator.
     * Synchronizes ArrayList, HashMap, and LinkedHashMap.
     */
    public Admin registerAdmin(String name, String email, String phone, String password,
                               String department, String adminRole) throws InvalidUserException {
        validateUserInput(name, email, phone);

        String userId = "A" + (++userCounter);

        if (userMap.containsKey(userId.toUpperCase())) {
            throw new InvalidUserException("Registration Failed: Duplicate Admin ID '" + userId + "' detected.");
        }

        Admin admin = new Admin(userId, name.trim(), email.trim(), password, phone.trim(),
                                (department != null ? department.trim() : "General"),
                                (adminRole != null ? adminRole.trim() : "Staff"));

        users.add(admin);
        userMap.put(admin.getUserId().toUpperCase(), admin);
        recentRegistrations.put(admin.getUserId(), admin);

        auditLog.append(System.currentTimeMillis()).append(" - Registered Admin: ").append(userId).append("\n");
        return admin;
    }

    /**
     * Searches a user by ID in O(1) time using HashMap lookup.
     */
    public User findUserById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        return userMap.get(userId.trim().toUpperCase());
    }

    /**
     * Displays all users.
     */
    public void displayAllUsers() {
        System.out.println("\n============== Registered RentIQ Users (" + users.size() + ") ==============");
        if (users.isEmpty()) {
            System.out.println("No users currently registered.");
            System.out.println("============================================================\n");
            return;
        }
        for (User user : users) {
            user.displayUserInfo();
        }
        System.out.println("============================================================\n");
    }

    /**
     * Displays recently registered users preserving exact insertion order using LinkedHashMap.
     */
    public void displayRecentRegistrations() {
        System.out.println("\n===== Recent User Registrations (LinkedHashMap - Insertion Order) =====");
        if (recentRegistrations.isEmpty()) {
            System.out.println("No user registrations recorded.");
            System.out.println("=======================================================================\n");
            return;
        }
        int rank = 1;
        for (Map.Entry<String, User> entry : recentRegistrations.entrySet()) {
            User u = entry.getValue();
            System.out.printf("%d. [%s] %s (%s) - %s\n",
                rank++, entry.getKey(), u.getName(),
                (u instanceof Customer ? "Customer: " + ((Customer) u).getMembershipLevel() : "Admin"),
                u.getEmail());
        }
        System.out.println("=======================================================================\n");
    }

    public int getUserCount() {
        return this.users.size();
    }

    public List<User> getAllUsers() {
        return Collections.unmodifiableList(this.users);
    }

    public Map<String, User> getUserMap() {
        return Collections.unmodifiableMap(this.userMap);
    }

    public LinkedHashMap<String, User> getRecentRegistrations() {
        return new LinkedHashMap<>(this.recentRegistrations);
    }

    public String getAuditLog() {
        return this.auditLog.toString();
    }
}
