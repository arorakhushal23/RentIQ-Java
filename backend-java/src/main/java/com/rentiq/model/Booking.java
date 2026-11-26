package com.rentiq.model;

public class Booking {

    private static int idCounter = 1000;
    private static int totalBookingsCount = 0;

    private String bookingId;
    private Customer customer;
    private Vehicle vehicle;
    private int rentalDays;
    private double totalAmount;
    private String bookingStatus;

    public Booking() {
        totalBookingsCount++;
    }

    public Booking(Customer customer, Vehicle vehicle, int rentalDays) {
        this(generateNextBookingId(), customer, vehicle, rentalDays);
    }

    public Booking(String bookingId, Customer customer, Vehicle vehicle, int rentalDays) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.vehicle = vehicle;
        this.rentalDays = rentalDays;
        this.totalAmount = calculateRentalCost();
        this.bookingStatus = "CONFIRMED";
        totalBookingsCount++;
    }

    public static String generateNextBookingId() {
        idCounter++;
        return "BK-" + idCounter;
    }

    public static int getTotalBookingsCount() {
        return totalBookingsCount;
    }

    public double calculateRentalCost() {
        if (this.vehicle != null && this.rentalDays > 0) {
            return this.vehicle.calculateRentalCost(this.rentalDays);
        }
        return 0.0;
    }

    public String getBookingId() {
        return this.bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.totalAmount = calculateRentalCost();
    }

    public Vehicle getVehicle() {
        return this.vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.totalAmount = calculateRentalCost();
    }

    public int getRentalDays() {
        return this.rentalDays;
    }

    public void setRentalDays(int rentalDays) {
        this.rentalDays = rentalDays;
        this.totalAmount = calculateRentalCost();
    }

    public double getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookingStatus() {
        return this.bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public void displayBookingDetails() {
        System.out.println("================ Booking Summary ================");
        System.out.println("Booking ID     : " + this.bookingId);
        System.out.println("Status         : " + this.bookingStatus);
        if (this.customer != null) {
            System.out.println("Customer       : " + this.customer.getName() + " (ID: " + this.customer.getUserId() + ")");
            System.out.println("Driving License: " + this.customer.getDrivingLicenseNumber());
            System.out.println("Contact Phone  : " + this.customer.getPhoneNumber());
        }
        if (this.vehicle != null) {
            System.out.println("Vehicle        : " + this.vehicle.getBrand() + " " + this.vehicle.getModel() + " (" + this.vehicle.getCategory() + ")");
            System.out.println("Plate Number   : " + this.vehicle.getRegistrationNumber());
            System.out.println("Rate Per Day   : $" + this.vehicle.getPricePerDay());
        }
        System.out.println("Rental Duration: " + this.rentalDays + " day(s)");
        System.out.println("Total Amount   : $" + String.format("%.2f", this.totalAmount));
        System.out.println("=================================================");
    }
}
