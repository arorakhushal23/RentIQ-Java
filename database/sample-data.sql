-- ============================================================================
-- RentIQ Database Sample Data
-- Database: rentiq_db
-- System: Vehicle Rental Management System
-- Compatible with MySQL 8.0+
-- ============================================================================

USE rentiq_db;

-- ----------------------------------------------------------------------------
-- 1. Insert Sample Users (1 Administrator, 4 Customers)
-- Password hashes represent SHA-256 digest of sample passphrases (Never plain-text)
-- ----------------------------------------------------------------------------
INSERT INTO users (user_id, name, email, password_hash, phone, role, driving_license_number, department, admin_role)
VALUES
('U9001', 'David Miller', 'admin@rentiq.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', '+1-555-0999', 'ADMIN', NULL, 'Fleet Management', 'Operations Lead'),
('U1001', 'Alice Johnson', 'alice@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', '+1-555-0101', 'CUSTOMER', 'DL-98765-XYZ', NULL, NULL),
('U1002', 'Bob Smith', 'bob@example.com', '4b227777d4dd1fc61c6f884f48641d02b4d121d3fd328cb08b5531fcacdabf8a', '+1-555-0102', 'CUSTOMER', 'DL-12345-ABC', NULL, NULL),
('U1003', 'Elena Rostova', 'elena@example.com', 'ef2d127de37b942baad06145e54b0c619a1f22327b2ebbcfbec78f5564afe39d', '+1-555-0103', 'CUSTOMER', 'DL-77889-ER', NULL, NULL),
('U1004', 'Carlos Mendez', 'carlos@example.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', '+1-555-0104', 'CUSTOMER', 'DL-33445-CM', NULL, NULL)
ON DUPLICATE KEY UPDATE name = VALUES(name), phone = VALUES(phone);

-- ----------------------------------------------------------------------------
-- 2. Insert Sample Vehicles (7 Cars, 5 Bikes)
-- Matches in-memory fleet initialized in Main.java
-- ----------------------------------------------------------------------------
INSERT INTO vehicles (vehicle_id, name, vehicle_type, brand, model, category, registration_number, price_per_day, rating, availability_status, seating_capacity, fuel_type, engine_capacity_cc, helmet_included)
VALUES
-- Cars
('V101', 'Tesla Model 3', 'Car', 'Tesla', 'Model 3', 'Car', 'TS-01-EV', 85.00, 4.90, 'AVAILABLE', 5, 'Electric', NULL, NULL),
('V102', 'Toyota RAV4', 'Car', 'Toyota', 'RAV4', 'Car', 'TY-02-SUV', 65.00, 4.60, 'AVAILABLE', 5, 'Hybrid', NULL, NULL),
('V103', 'Honda Civic', 'Car', 'Honda', 'Civic', 'Car', 'HN-03-SD', 55.00, 4.40, 'AVAILABLE', 5, 'Gasoline', NULL, NULL),
('V104', 'BMW 3 Series', 'Car', 'BMW', '3 Series', 'Car', 'BM-04-SD', 95.00, 4.80, 'AVAILABLE', 5, 'Gasoline', NULL, NULL),
('V105', 'Ford Mustang Mach-E', 'Car', 'Ford', 'Mustang Mach-E', 'Car', 'FD-05-EV', 90.00, 4.70, 'AVAILABLE', 5, 'Electric', NULL, NULL),
('V106', 'Audi Q5', 'Car', 'Audi', 'Q5', 'Car', 'AD-06-SUV', 80.00, 4.60, 'AVAILABLE', 5, 'Gasoline', NULL, NULL),
('V107', 'Mercedes-Benz C300', 'Car', 'Mercedes-Benz', 'C300', 'Car', 'MB-07-SD', 100.00, 4.90, 'AVAILABLE', 5, 'Gasoline', NULL, NULL),
-- Bikes
('B201', 'Yamaha YZF-R3', 'Bike', 'Yamaha', 'YZF-R3', 'Bike', 'YM-01-BK', 45.00, 4.70, 'AVAILABLE', NULL, NULL, 321, TRUE),
('B202', 'Harley-Davidson Iron 883', 'Bike', 'Harley-Davidson', 'Iron 883', 'Bike', 'HD-02-BK', 75.00, 4.80, 'AVAILABLE', NULL, NULL, 883, TRUE),
('B203', 'Kawasaki Ninja 400', 'Bike', 'Kawasaki', 'Ninja 400', 'Bike', 'KW-03-BK', 50.00, 4.50, 'AVAILABLE', NULL, NULL, 399, TRUE),
('B204', 'Ducati Scrambler', 'Bike', 'Ducati', 'Scrambler', 'Bike', 'DC-04-BK', 85.00, 4.90, 'AVAILABLE', NULL, NULL, 803, TRUE),
('B205', 'Triumph Bonneville T100', 'Bike', 'Triumph', 'Bonneville T100', 'Bike', 'TR-05-BK', 70.00, 4.70, 'AVAILABLE', NULL, NULL, 900, TRUE)
ON DUPLICATE KEY UPDATE price_per_day = VALUES(price_per_day), rating = VALUES(rating);

-- ----------------------------------------------------------------------------
-- 3. Insert Sample Bookings
-- Demonstrates active, completed, and cancelled bookings with date integrity
-- ----------------------------------------------------------------------------
INSERT INTO bookings (booking_id, user_id, vehicle_id, start_date, end_date, rental_days, total_amount, booking_status)
VALUES
('BK-1001', 'U1001', 'V101', '2026-12-01', '2026-12-04', 3, 255.00, 'CONFIRMED'),
('BK-1002', 'U1002', 'V103', '2026-12-05', '2026-12-07', 2, 110.00, 'CONFIRMED'),
('BK-1003', 'U1003', 'B201', '2026-12-10', '2026-12-11', 1, 45.00, 'COMPLETED'),
('BK-1004', 'U1004', 'V102', '2026-12-15', '2026-12-18', 3, 195.00, 'CANCELLED'),
('BK-1005', 'U1001', 'B202', '2026-12-20', '2026-12-24', 4, 300.00, 'CONFIRMED')
ON DUPLICATE KEY UPDATE total_amount = VALUES(total_amount), booking_status = VALUES(booking_status);
