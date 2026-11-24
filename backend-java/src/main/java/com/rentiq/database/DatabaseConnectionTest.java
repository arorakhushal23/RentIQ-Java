package com.rentiq.database;

import com.rentiq.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Educational test harness demonstrating JDBC connectivity for Milestone 2:
 * 1. Loading database configuration.
 * 2. Establishing MySQL connection via DriverManager.
 * 3. Checking connection status and metadata.
 * 4. Executing simple SELECT verification queries.
 * 5. Safely closing connection resources with try-with-resources.
 * 6. Graceful failure handling without crashing when MySQL is unavailable.
 */
public class DatabaseConnectionTest {

    /**
     * Runs full JDBC connection test workflow.
     */
    public static boolean runConnectionTest() {
        System.out.println("\n============================================================");
        System.out.println("   RentIQ Phase 3 - Milestone 2: MySQL JDBC Connection Test  ");
        System.out.println("============================================================\n");

        DatabaseConfig config = DatabaseConnection.getDefaultConfig();
        System.out.println(">>> 1. Loading Database Configuration:");
        config.displayConfig();

        System.out.println("\n>>> 2. Establishing JDBC Connection via DriverManager...");
        long start = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection(config)) {
            long connectionTime = System.currentTimeMillis() - start;
            System.out.printf("[Success] Connected in %d ms.\n", connectionTime);

            System.out.println("\n>>> 3. Checking Connection Status & Metadata:");
            boolean isValid = conn.isValid(2);
            System.out.println("  Connection isOpen() : " + !conn.isClosed());
            System.out.println("  Connection isValid(): " + isValid);

            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("  DBMS Product Name   : " + meta.getDatabaseProductName());
            System.out.println("  DBMS Version        : " + meta.getDatabaseProductVersion());
            System.out.println("  JDBC Driver Name    : " + meta.getDriverName());
            System.out.println("  JDBC Driver Version : " + meta.getDriverVersion());
            System.out.println("  Active Catalog / DB : " + conn.getCatalog());

            System.out.println("\n>>> 4. Executing Verification SELECT Queries:");
            try (Statement stmt = conn.createStatement()) {
                // Test 1: Simple ping
                try (ResultSet rs = stmt.executeQuery("SELECT 1 AS ping, CURRENT_TIMESTAMP AS server_time, DATABASE() AS active_db")) {
                    if (rs.next()) {
                        System.out.printf("  [Ping Query] Ping: %d | Server Time: %s | Active DB: %s\n",
                                rs.getInt("ping"), rs.getString("server_time"), rs.getString("active_db"));
                    }
                }

                // Test 2: Query table record counts if rentiq_db tables exist
                try {
                    String countSql = "SELECT 'users' AS tbl, COUNT(*) AS cnt FROM users "
                            + "UNION ALL SELECT 'vehicles', COUNT(*) FROM vehicles "
                            + "UNION ALL SELECT 'bookings', COUNT(*) FROM bookings";
                    try (ResultSet rs = stmt.executeQuery(countSql)) {
                        System.out.println("  [Table Counts]");
                        while (rs.next()) {
                            System.out.printf("    - %-10s : %d records\n", rs.getString("tbl"), rs.getInt("cnt"));
                        }
                    }
                } catch (SQLException tableCheckEx) {
                    System.out.println("  [Note] rentiq_db tables not yet populated or accessible: " + tableCheckEx.getMessage());
                }
            }

            System.out.println("\n>>> 5. Closing Connection:");
            System.out.println("  Connection will be cleanly closed automatically by try-with-resources.");
            System.out.println("\n============================================================");
            System.out.println("   JDBC Connection Test: SUCCESS                            ");
            System.out.println("============================================================\n");
            return true;

        } catch (SQLException e) {
            System.out.println("\n[JDBC Connection Failed]");
            System.out.println("  Error Code : " + e.getErrorCode());
            System.out.println("  SQL State  : " + e.getSQLState());
            System.out.println("  Message    : " + e.getMessage());
            System.out.println("\n[Diagnostic Guide]");
            System.out.println("  1. Ensure MySQL Server is running locally on port 3306.");
            System.out.println("  2. Verify credentials in environment variables (RENTIQ_DB_PASSWORD) or .env file.");
            System.out.println("  3. Verify that database 'rentiq_db' exists (run schema.sql).");
            System.out.println("  4. Note: RentIQ in-memory operations continue to work without MySQL.");
            System.out.println("============================================================\n");
            return false;
        }
    }

    /**
     * Executes an arbitrary verification SELECT query safely.
     */
    public static void executeVerificationQuery(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            System.out.println("[Error] Query cannot be empty.");
            return;
        }

        String trimmed = sql.trim();
        if (!trimmed.toLowerCase().startsWith("select") && !trimmed.toLowerCase().startsWith("show")) {
            System.out.println("[Security Restriction] Only SELECT and SHOW queries are allowed in this test interface.");
            return;
        }

        System.out.println("\nExecuting SQL: " + trimmed);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(trimmed)) {

            int colCount = rs.getMetaData().getColumnCount();
            StringBuilder header = new StringBuilder();
            for (int i = 1; i <= colCount; i++) {
                header.append(String.format("%-22s", rs.getMetaData().getColumnLabel(i)));
            }
            System.out.println(header);
            System.out.println("-".repeat(Math.min(header.length(), 100)));

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= colCount; i++) {
                    String val = rs.getString(i);
                    row.append(String.format("%-22s", val != null ? val : "NULL"));
                }
                System.out.println(row);
                if (rowCount >= 20) {
                    System.out.println("... (Output limited to 20 rows)");
                    break;
                }
            }
            System.out.printf("(%d rows returned)\n\n", rowCount);

        } catch (SQLException e) {
            System.out.println("[Query Error] " + e.getMessage());
        }
    }
}
