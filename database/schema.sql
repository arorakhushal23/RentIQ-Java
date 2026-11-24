-- ============================================================================
-- RentIQ Database Schema Definition
-- Database: rentiq_db
-- System: Vehicle Rental Management System
-- Compatible with MySQL 8.0+
-- ============================================================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS rentiq_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE rentiq_db;

-- ----------------------------------------------------------------------------
-- Table 1: users
-- Stores both Customers and Administrators with role-based attributes
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    driving_license_number VARCHAR(50) NULL,
    membership_level VARCHAR(30) NULL DEFAULT 'Standard',
    department VARCHAR(100) NULL,
    admin_role VARCHAR(100) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_user_role CHECK (role IN ('CUSTOMER', 'ADMIN')),
    CONSTRAINT chk_membership_level CHECK (membership_level IS NULL OR membership_level IN ('Standard', 'Gold VIP', 'Platinum VIP'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_users_role ON users (role);
CREATE INDEX idx_users_email ON users (email);

-- ----------------------------------------------------------------------------
-- Table 2: vehicles
-- Stores polymorphic vehicle fleet (Cars and Bikes) with domain attributes
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    category VARCHAR(30) NOT NULL,
    registration_number VARCHAR(50) NOT NULL,
    price_per_day DECIMAL(10, 2) NOT NULL,
    rating DECIMAL(3, 2) NOT NULL DEFAULT 5.00,
    availability_status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    seating_capacity INT NULL,
    fuel_type VARCHAR(30) NULL,
    engine_capacity_cc INT NULL,
    helmet_included BOOLEAN NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_vehicles PRIMARY KEY (vehicle_id),
    CONSTRAINT uq_vehicles_registration UNIQUE (registration_number),
    CONSTRAINT chk_vehicle_type CHECK (vehicle_type IN ('Car', 'Bike')),
    CONSTRAINT chk_vehicle_price CHECK (price_per_day >= 0.00),
    CONSTRAINT chk_vehicle_rating CHECK (rating >= 0.00 AND rating <= 5.00),
    CONSTRAINT chk_vehicle_availability CHECK (availability_status IN ('AVAILABLE', 'RENTED', 'MAINTENANCE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_vehicles_type ON vehicles (vehicle_type);
CREATE INDEX idx_vehicles_brand ON vehicles (brand);
CREATE INDEX idx_vehicles_price ON vehicles (price_per_day);
CREATE INDEX idx_vehicles_availability ON vehicles (availability_status);
CREATE INDEX idx_vehicles_rating ON vehicles (rating);

-- ----------------------------------------------------------------------------
-- Table 3: bookings
-- Stores rental reservations linking users and vehicles with date ranges
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    booking_id VARCHAR(50) NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    vehicle_id VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    rental_days INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    booking_status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT pk_bookings PRIMARY KEY (booking_id),
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) 
        REFERENCES users (user_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT fk_bookings_vehicle FOREIGN KEY (vehicle_id) 
        REFERENCES vehicles (vehicle_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT chk_booking_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_booking_days CHECK (rental_days >= 1),
    CONSTRAINT chk_booking_amount CHECK (total_amount >= 0.00),
    CONSTRAINT chk_booking_status CHECK (booking_status IN ('CONFIRMED', 'CANCELLED', 'PENDING', 'COMPLETED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_bookings_user ON bookings (user_id);
CREATE INDEX idx_bookings_vehicle ON bookings (vehicle_id);
CREATE INDEX idx_bookings_status ON bookings (booking_status);
CREATE INDEX idx_bookings_date_range ON bookings (vehicle_id, start_date, end_date, booking_status);
