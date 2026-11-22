package com.rentiq.model;

/**
 * Represents a Customer user in the RentIQ system.
 * Extends User to demonstrate inheritance, constructor chaining using super,
 * method overriding, and runtime polymorphism.
 */
public class Customer extends User {
    private String drivingLicenseNumber;
    private String membershipLevel;

    // Default constructor
    public Customer() {
        super();
    }

    // Parameterized constructor
    public Customer(String userId, String name, String email, String password, String phoneNumber,
                    String drivingLicenseNumber, String membershipLevel) {
        super(userId, name, email, password, phoneNumber);
        this.drivingLicenseNumber = drivingLicenseNumber;
        this.membershipLevel = membershipLevel;
    }

    // Overloaded constructor without password
    public Customer(String userId, String name, String email, String phoneNumber,
                    String drivingLicenseNumber, String membershipLevel) {
        super(userId, name, email, phoneNumber);
        this.drivingLicenseNumber = drivingLicenseNumber;
        this.membershipLevel = membershipLevel;
    }

    public String getDrivingLicenseNumber() {
        return this.drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public String getMembershipLevel() {
        return this.membershipLevel;
    }

    public void setMembershipLevel(String membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    @Override
    public void displayUserInfo() {
        System.out.println("----- Customer Details -----");
        System.out.println("User ID         : " + getUserId());
        System.out.println("Role            : Customer");
        System.out.println("Name            : " + getName());
        System.out.println("Email           : " + getEmail());
        System.out.println("Phone Number    : " + getPhoneNumber());
        System.out.println("Driving License : " + this.drivingLicenseNumber);
        System.out.println("Membership      : " + this.membershipLevel);
        System.out.println("----------------------------");
    }
}
