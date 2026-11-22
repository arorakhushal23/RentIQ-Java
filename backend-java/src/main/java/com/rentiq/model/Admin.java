package com.rentiq.model;

/**
 * Represents an Administrator in the RentIQ system.
 * Extends User to demonstrate inheritance, constructor chaining using super,
 * method overriding, and runtime polymorphism.
 */
public class Admin extends User {
    private String department;
    private String adminRole;

    // Default constructor
    public Admin() {
        super();
    }

    // Parameterized constructor
    public Admin(String userId, String name, String email, String password, String phoneNumber,
                 String department, String adminRole) {
        super(userId, name, email, password, phoneNumber);
        this.department = department;
        this.adminRole = adminRole;
    }

    // Overloaded constructor without password
    public Admin(String userId, String name, String email, String phoneNumber,
                 String department, String adminRole) {
        super(userId, name, email, phoneNumber);
        this.department = department;
        this.adminRole = adminRole;
    }

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAdminRole() {
        return this.adminRole;
    }

    public void setAdminRole(String adminRole) {
        this.adminRole = adminRole;
    }

    public void displayAdminPrivileges() {
        System.out.println("[Admin Privileges] Full system access granted for department: " + this.department);
    }

    @Override
    public void displayUserInfo() {
        System.out.println("----- Admin Details -----");
        System.out.println("User ID      : " + getUserId());
        System.out.println("Role         : Administrator (" + this.adminRole + ")");
        System.out.println("Name         : " + getName());
        System.out.println("Email        : " + getEmail());
        System.out.println("Phone Number : " + getPhoneNumber());
        System.out.println("Department   : " + this.department);
        System.out.println("-------------------------");
    }
}
