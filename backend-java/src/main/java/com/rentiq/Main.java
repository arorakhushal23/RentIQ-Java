package com.rentiq;

import com.rentiq.service.BookingService;
import com.rentiq.service.UserService;
import com.rentiq.service.VehicleService;
import com.rentiq.ui.ConsoleApp;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("   Welcome to RentIQ - Vehicle Rental Management System     ");
        System.out.println("============================================================\n");

        UserService userService = new UserService();
        VehicleService vehicleService = new VehicleService();
        BookingService bookingService = new BookingService();

        try {
            vehicleService.loadVehiclesFromDatabase();

        } catch (SQLException e) {
            System.out.println("[Database Error] Unable to load vehicles from MySQL.");
            System.out.println("Reason: " + e.getMessage());
            System.out.println("Please check your database connection.");
            return;
        }

        System.out.println("[System Ready] Vehicles loaded from MySQL.");
        System.out.println("Launching interactive console session...\n");

        Scanner scanner = new Scanner(System.in);

        ConsoleApp app = new ConsoleApp(
                userService,
                vehicleService,
                bookingService,
                scanner
        );

        app.start();
    }
}