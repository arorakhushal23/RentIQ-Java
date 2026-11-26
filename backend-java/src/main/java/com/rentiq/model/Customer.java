package com.rentiq.model;

public class Customer extends User {
    private String drivingLicenseNumber;

    public Customer() {
        super();
    }

    public Customer(String userId, String name, String email, String password, String phoneNumber,
                    String drivingLicenseNumber) {
        super(userId, name, email, password, phoneNumber);
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public Customer(String userId, String name, String email, String phoneNumber,
                    String drivingLicenseNumber) {
        super(userId, name, email, phoneNumber);
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public String getDrivingLicenseNumber() {
        return this.drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
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
        System.out.println("----------------------------");
    }
}
