package com.rentiq.service;

import com.rentiq.database.DatabaseConnection;
import com.rentiq.model.Admin;
import com.rentiq.model.Customer;
import com.rentiq.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.UUID;

public class UserService {

    public void validateUserInput(String name, String email, String phone) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Validation Failed: User name cannot be empty or null.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Validation Failed: Email address cannot be empty.");
        }

        String cleanEmail = email.trim();
        int atIndex = cleanEmail.indexOf('@');
        int dotIndex = cleanEmail.lastIndexOf('.');

        if (atIndex <= 0 || dotIndex <= atIndex + 1 || dotIndex >= cleanEmail.length() - 1) {
            throw new Exception("Validation Failed: Invalid email format.");
        }

        if (phone == null || phone.trim().isEmpty() || phone.trim().length() < 7) {
            throw new Exception("Validation Failed: Invalid phone number.");
        }

        if (emailExists(cleanEmail)) {
            throw new Exception("Registration Failed: Email is already registered.");
        }
    }

    public Customer registerCustomer(String name, String email, String phone,
            String password, String drivingLicense) throws Exception {

        validateUserInput(name, email, phone);

        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Registration Failed: Password cannot be empty.");
        }

        if (drivingLicense == null || drivingLicense.trim().isEmpty()) {
            throw new Exception("Validation Failed: Customer must provide a valid driving license.");
        }

        String userId = generateUserId("U");

        Customer customer = new Customer(
                userId,
                name.trim(),
                email.trim(),
                password,
                phone.trim(),
                drivingLicense.trim());

        if (!saveUserToDatabase(customer)) {
            throw new Exception("Registration Failed: Could not save customer to database.");
        }

        System.out.println("[UserService] Successfully registered Customer: "
                + customer.getName() + " (ID: " + customer.getUserId() + ")");

        return customer;
    }

    public Admin registerAdmin(String name, String email, String phone,
            String password, String department, String adminRole) throws Exception {

        validateUserInput(name, email, phone);

        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Registration Failed: Password cannot be empty.");
        }

        String userId = generateUserId("A");

        String dept = (department != null) ? department.trim() : "General";
        String role = (adminRole != null) ? adminRole.trim() : "Staff";

        Admin admin = new Admin(
                userId,
                name.trim(),
                email.trim(),
                password,
                phone.trim(),
                dept,
                role);

        if (!saveUserToDatabase(admin)) {
            throw new Exception("Registration Failed: Could not save admin to database.");
        }

        return admin;
    }

    private String generateUserId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public User findUserById(String userId) {
        try {
            return getUserFromDatabase(userId);
        } catch (SQLException e) {
            System.out.println("[Database Error] Unable to find user: " + e.getMessage());
            return null;
        }
    }

    public User authenticate(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        try {
            User user = getUserByEmailFromDatabase(email);

            if (user != null && hashPassword(password).equals(user.getPassword())) {
                return user;
            }

        } catch (SQLException e) {
            System.out.println("[Database Error] Login failed: " + e.getMessage());
        }

        return null;
    }

    public boolean saveUserToDatabase(User user) throws SQLException {
        if (user == null || user.getUserId() == null) {
            return false;
        }

        String sql = "INSERT INTO users (user_id, name, email, password_hash, phone, role, "
                + "driving_license_number, department, admin_role) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserId().trim());
            pstmt.setString(2, user.getName().trim());
            pstmt.setString(3, user.getEmail().trim().toLowerCase());
            pstmt.setString(4, hashPassword(user.getPassword()));
            pstmt.setString(5, user.getPhone().trim());

            if (user instanceof Admin) {
                Admin admin = (Admin) user;

                pstmt.setString(6, "ADMIN");
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setString(8, admin.getDepartment());
                pstmt.setString(9, admin.getAdminRole());

            } else if (user instanceof Customer) {
                Customer customer = (Customer) user;

                pstmt.setString(6, "CUSTOMER");
                pstmt.setString(7, customer.getDrivingLicenseNumber());
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.VARCHAR);

            } else {
                return false;
            }

            return pstmt.executeUpdate() > 0;
        }
    }

    public User getUserFromDatabase(String userId) throws SQLException {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }

        return null;
    }

    public User getUserByEmailFromDatabase(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }

        return null;
    }

    public void displayDatabaseStatistics() throws SQLException {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM users) AS total_users, "
                + "(SELECT COUNT(*) FROM users WHERE role = 'CUSTOMER') AS customer_count, "
                + "(SELECT COUNT(*) FROM users WHERE role = 'ADMIN') AS admin_count, "
                + "(SELECT COUNT(*) FROM vehicles) AS total_vehicles, "
                + "(SELECT COUNT(*) FROM bookings) AS total_bookings";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("========== MySQL Database Statistics ==========");
                System.out.println("Total Users    : " + rs.getInt("total_users"));
                System.out.println("  - Customers  : " + rs.getInt("customer_count"));
                System.out.println("  - Admins     : " + rs.getInt("admin_count"));
                System.out.println("Total Vehicles : " + rs.getInt("total_vehicles"));
                System.out.println("Total Bookings : " + rs.getInt("total_bookings"));
                System.out.println("===============================================");
            }
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String id = rs.getString("user_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        String phone = rs.getString("phone");
        String role = rs.getString("role");

        if ("ADMIN".equalsIgnoreCase(role)) {
            String dept = rs.getString("department");
            String title = rs.getString("admin_role");

            return new Admin(id, name, email, passwordHash, phone, dept, title);
        }

        String license = rs.getString("driving_license_number");

        return new Customer(id, name, email, passwordHash, phone, license);
    }

    public static String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            return "";
        }

        if (rawPassword.length() == 64 && rawPassword.matches("^[0-9a-fA-F]+$")) {
            return rawPassword.toLowerCase();
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            throw new IllegalStateException("Password hashing failed.", e);
        }
    }
}