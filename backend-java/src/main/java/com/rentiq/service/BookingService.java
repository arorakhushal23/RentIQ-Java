package com.rentiq.service;

import com.rentiq.database.DatabaseConnection;
import com.rentiq.model.Bike;
import com.rentiq.model.Booking;
import com.rentiq.model.Car;
import com.rentiq.model.Customer;
import com.rentiq.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BookingService {

    public Booking createBooking(Customer customer, Vehicle vehicle, int rentalDays) throws Exception {
        if (customer == null) {
            throw new Exception("Booking Creation Failed: Customer cannot be null.");
        }

        if (vehicle == null) {
            throw new Exception("Booking Creation Failed: Vehicle cannot be null.");
        }

        if (rentalDays <= 0) {
            throw new Exception("Booking Creation Failed: Rental duration must be at least 1 day.");
        }

        String bookingId = "BK-" + UUID.randomUUID().toString()
                .substring(0, 8).toUpperCase();

        Booking booking = new Booking(bookingId, customer, vehicle, rentalDays);

        String checkSql = "SELECT availability_status FROM vehicles "
                + "WHERE vehicle_id = ? FOR UPDATE";

        String updateVehicleSql = "UPDATE vehicles SET availability_status = 'RENTED' "
                + "WHERE vehicle_id = ? AND availability_status = 'AVAILABLE'";

        String insertBookingSql = "INSERT INTO bookings "
                + "(booking_id, user_id, vehicle_id, start_date, end_date, "
                + "rental_days, total_amount, booking_status) "
                + "VALUES (?, ?, ?, CURRENT_DATE, "
                + "DATE_ADD(CURRENT_DATE, INTERVAL ? DAY), ?, ?, 'CONFIRMED')";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
                    checkStatement.setString(1, vehicle.getVehicleId());

                    try (ResultSet resultSet = checkStatement.executeQuery()) {
                        if (!resultSet.next()
                                || !"AVAILABLE".equalsIgnoreCase(
                                        resultSet.getString("availability_status"))) {
                            throw new SQLException("Vehicle is not available for booking.");
                        }
                    }
                }

                try (PreparedStatement updateStatement = connection.prepareStatement(updateVehicleSql)) {
                    updateStatement.setString(1, vehicle.getVehicleId());

                    if (updateStatement.executeUpdate() != 1) {
                        throw new SQLException("Vehicle availability could not be updated.");
                    }
                }

                try (PreparedStatement insertStatement = connection.prepareStatement(insertBookingSql)) {
                    insertStatement.setString(1, booking.getBookingId());
                    insertStatement.setString(2, customer.getUserId());
                    insertStatement.setString(3, vehicle.getVehicleId());
                    insertStatement.setInt(4, rentalDays);
                    insertStatement.setInt(5, rentalDays);
                    insertStatement.setDouble(6, booking.getTotalAmount());

                    insertStatement.executeUpdate();
                }

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new Exception("Booking could not be saved: " + e.getMessage(), e);
        }

        vehicle.bookVehicle();

        System.out.println("[BookingService] Booking successfully created! Booking ID: "
                + booking.getBookingId());

        return booking;
    }

    public boolean cancelBooking(String bookingId) throws Exception {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new Exception("Cancellation Failed: Booking ID cannot be empty.");
        }

        String cleanId = bookingId.trim();

        String findSql = "SELECT vehicle_id, booking_status FROM bookings "
                + "WHERE booking_id = ? FOR UPDATE";

        String cancelSql = "UPDATE bookings SET booking_status = 'CANCELLED' "
                + "WHERE booking_id = ? AND booking_status <> 'CANCELLED'";

        String vehicleSql = "UPDATE vehicles SET availability_status = 'AVAILABLE' "
                + "WHERE vehicle_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                String vehicleId;
                String status;

                try (PreparedStatement findStatement = connection.prepareStatement(findSql)) {
                    findStatement.setString(1, cleanId);

                    try (ResultSet resultSet = findStatement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new SQLException("Booking '" + cleanId + "' was not found.");
                        }

                        vehicleId = resultSet.getString("vehicle_id");
                        status = resultSet.getString("booking_status");
                    }
                }

                if ("CANCELLED".equalsIgnoreCase(status)) {
                    throw new SQLException("This booking is already cancelled.");
                }

                try (PreparedStatement cancelStatement = connection.prepareStatement(cancelSql)) {
                    cancelStatement.setString(1, cleanId);

                    if (cancelStatement.executeUpdate() != 1) {
                        throw new SQLException("Booking cancellation could not be completed.");
                    }
                }

                try (PreparedStatement vehicleStatement = connection.prepareStatement(vehicleSql)) {
                    vehicleStatement.setString(1, vehicleId);
                    vehicleStatement.executeUpdate();
                }

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new Exception("Cancellation failed: " + e.getMessage(), e);
        }

        System.out.println("[BookingService] Booking " + cleanId + " cancelled.");
        return true;
    }

    public List<Booking> getBookingsByCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<Booking> bookings = new ArrayList<>();
        String sql = bookingSelectSql() + " WHERE b.user_id = ? ORDER BY b.created_at DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customerId.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookings.add(mapResultSetToBooking(resultSet));
                }
            }

        } catch (SQLException e) {
            System.out.println("[Database Error] Unable to retrieve customer bookings: " + e.getMessage());
        }

        return bookings;
    }

    private String bookingSelectSql() {
        return "SELECT b.booking_id, b.user_id, b.vehicle_id, b.rental_days, "
                + "b.total_amount, b.booking_status, "
                + "u.name AS customer_name, u.email AS customer_email, "
                + "u.phone AS customer_phone, u.driving_license_number, "
                + "v.vehicle_type, v.brand, v.model, v.category, "
                + "v.registration_number, v.price_per_day, v.rating, "
                + "v.availability_status, v.seating_capacity, v.fuel_type, "
                + "v.engine_capacity_cc, v.helmet_included "
                + "FROM bookings b "
                + "JOIN users u ON b.user_id = u.user_id "
                + "JOIN vehicles v ON b.vehicle_id = v.vehicle_id";
    }

    private Booking mapResultSetToBooking(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer(
                resultSet.getString("user_id"),
                resultSet.getString("customer_name"),
                resultSet.getString("customer_email"),
                resultSet.getString("customer_phone"),
                resultSet.getString("driving_license_number")
        );

        String vehicleId = resultSet.getString("vehicle_id");
        String vehicleType = resultSet.getString("vehicle_type");
        String brand = resultSet.getString("brand");
        String model = resultSet.getString("model");
        String registrationNumber = resultSet.getString("registration_number");
        double pricePerDay = resultSet.getDouble("price_per_day");
        double rating = resultSet.getDouble("rating");
        boolean available = "AVAILABLE".equalsIgnoreCase(
                resultSet.getString("availability_status"));

        Vehicle vehicle;

        if ("Bike".equalsIgnoreCase(vehicleType)) {
            vehicle = new Bike(
                    vehicleId,
                    brand,
                    model,
                    registrationNumber,
                    pricePerDay,
                    available,
                    resultSet.getInt("engine_capacity_cc"),
                    resultSet.getBoolean("helmet_included"),
                    rating
            );
        } else {
            vehicle = new Car(
                    vehicleId,
                    brand,
                    model,
                    registrationNumber,
                    pricePerDay,
                    available,
                    resultSet.getInt("seating_capacity"),
                    resultSet.getString("fuel_type"),
                    rating
            );
        }

        Booking booking = new Booking(
                resultSet.getString("booking_id"),
                customer,
                vehicle,
                resultSet.getInt("rental_days")
        );

        booking.setTotalAmount(resultSet.getDouble("total_amount"));
        booking.setBookingStatus(resultSet.getString("booking_status"));

        return booking;
    }

}
