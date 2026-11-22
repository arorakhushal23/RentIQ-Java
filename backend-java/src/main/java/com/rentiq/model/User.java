package com.rentiq.model;

/**
 * Represents a user in the RentIQ system.
 * Demonstrates Core Java fundamentals: classes, objects, encapsulation,
 * access modifiers, constructors, and the 'this' keyword.
 */
public class User {
    private String userId;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    // Default constructor
    public User() {
    }

    // Parameterized constructor
    public User(String userId, String name, String email, String password, String phoneNumber) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    // Overloaded constructor without password
    public User(String userId, String name, String email, String phoneNumber) {
        this(userId, name, email, "", phoneNumber);
    }

    // Getters and Setters
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhone() {
        return this.phoneNumber;
    }

    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }

    // Method to display user details
    public void displayUserInfo() {
        System.out.println("----- User Details -----");
        System.out.println("User ID      : " + this.userId);
        System.out.println("Name         : " + this.name);
        System.out.println("Email        : " + this.email);
        System.out.println("Phone Number : " + this.phoneNumber);
        System.out.println("------------------------");
    }
}
