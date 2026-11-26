# RentIQ Database Scripts & Documentation
## Database: `rentiq_db`

This directory provides the relational database definitions and seed data for the **RentIQ Vehicle Rental Management System**.

---

## Files

1. **`schema.sql`**: Full DDL schema script creating database `rentiq_db`, tables (`users`, `vehicles`, `bookings`), primary keys, foreign keys with cascade rules, check constraints, and performance indexes.
2. **`sample-data.sql`**: Realistic initial seed data inserting 1 Administrator, 4 Customers, 7 Cars, 5 Bikes, and 5 historical/active Bookings matching the in-memory Java domain models.

---

## Entity-Relationship (ER) Overview

```mermaid
erDiagram
    users ||--o{ bookings : places
    vehicles ||--o{ bookings : "reserved in"

    users {
        VARCHAR(50) user_id PK
        VARCHAR(100) name
        VARCHAR(100) email UK
        VARCHAR(255) password_hash
        VARCHAR(20) phone
        VARCHAR(20) role "CUSTOMER | ADMIN"
        VARCHAR(50) driving_license_number "NULL for Admin"
        VARCHAR(100) department "NULL for Customer"
        VARCHAR(100) admin_role "NULL for Customer"
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    vehicles {
        VARCHAR(50) vehicle_id PK
        VARCHAR(100) name
        VARCHAR(20) vehicle_type "Car | Bike"
        VARCHAR(50) brand
        VARCHAR(50) model
        VARCHAR(30) category
        VARCHAR(50) registration_number UK
        DECIMAL price_per_day
        DECIMAL rating "1.00 - 5.00"
        VARCHAR(20) availability_status "AVAILABLE | RENTED | MAINTENANCE"
        INT seating_capacity "NULL for Bike"
        VARCHAR(30) fuel_type "NULL for Bike"
        INT engine_capacity_cc "NULL for Car"
        BOOLEAN helmet_included "NULL for Car"
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    bookings {
        VARCHAR(50) booking_id PK
        VARCHAR(50) user_id FK
        VARCHAR(50) vehicle_id FK
        DATE start_date
        DATE end_date
        INT rental_days ">= 1"
        DECIMAL total_amount ">= 0.00"
        VARCHAR(20) booking_status "CONFIRMED | CANCELLED | PENDING | COMPLETED"
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
```

---

## Setup & Execution

### 1. Execute Schema Creation
```bash
mysql -u root -p < schema.sql
```

### 2. Populate Sample Data
```bash
mysql -u root -p < sample-data.sql
```

### 3. Verify in MySQL Client
```sql
USE rentiq_db;
SHOW TABLES;
SELECT 'users' AS tbl, COUNT(*) AS count FROM users
UNION ALL SELECT 'vehicles', COUNT(*) FROM vehicles
UNION ALL SELECT 'bookings', COUNT(*) FROM bookings;
```
