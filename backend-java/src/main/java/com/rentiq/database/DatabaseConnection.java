package com.rentiq.database;

import com.rentiq.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for establishing and managing MySQL JDBC connections.
 * Responsibilities:
 * - Loads MySQL Connector/J driver.
 * - Establishes Connection via DriverManager using DatabaseConfig.
 * - Validates connections and provides health check diagnostics.
 * - Encourages try-with-resources patterns to prevent connection leaks.
 */
public class DatabaseConnection {
    private static final DatabaseConfig defaultConfig = new DatabaseConfig();
    private static boolean driverLoaded = false;

    static {
        try {
            Class.forName(DatabaseConfig.DRIVER_CLASS);
            driverLoaded = true;
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }
    }

    /**
     * Establishes a new JDBC Connection using default configuration.
     * Callers MUST close the connection, preferably using try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        return getConnection(defaultConfig);
    }

    /**
     * Establishes a new JDBC Connection using a custom DatabaseConfig.
     */
    public static Connection getConnection(DatabaseConfig config) throws SQLException {
        if (!driverLoaded) {
            try {
                Class.forName(DatabaseConfig.DRIVER_CLASS);
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new SQLException("Cannot establish connection: MySQL JDBC Driver '"
                        + DatabaseConfig.DRIVER_CLASS + "' is missing from classpath.", e);
            }
        }

        if (config == null) {
            throw new IllegalArgumentException("DatabaseConfig cannot be null.");
        }

        return DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
    }

    /**
     * Validates whether a connection is open and responsive within timeout seconds.
     */
    public static boolean isConnectionValid(Connection connection, int timeoutSeconds) {
        if (connection == null) {
            return false;
        }
        try {
            return !connection.isClosed() && connection.isValid(timeoutSeconds);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Tests connectivity and returns a diagnostic result without throwing uncaught exceptions.
     */
    public static ConnectionResult testConnection() {
        return testConnection(defaultConfig);
    }

    public static ConnectionResult testConnection(DatabaseConfig config) {
        long start = System.currentTimeMillis();
        try (Connection conn = getConnection(config)) {
            boolean valid = conn.isValid(3);
            long elapsed = System.currentTimeMillis() - start;
            if (valid) {
                String metaDb = conn.getCatalog();
                String serverVersion = conn.getMetaData().getDatabaseProductVersion();
                return new ConnectionResult(true, "Successfully connected to MySQL database '" + metaDb + "'",
                        serverVersion, metaDb, elapsed);
            } else {
                return new ConnectionResult(false, "Connection was established but failed validation check.",
                        null, null, elapsed);
            }
        } catch (SQLException e) {
            long elapsed = System.currentTimeMillis() - start;
            String userFriendlyMessage = translateSqlException(e, config);
            return new ConnectionResult(false, userFriendlyMessage, null, null, elapsed);
        }
    }

    private static String translateSqlException(SQLException e, DatabaseConfig config) {
        String msg = e.getMessage();
        if (msg != null && msg.contains("Communications link failure")) {
            return "MySQL Server is not reachable at " + config.getUrl()
                    + ". Verify that MySQL service is running on port 3306.";
        } else if (msg != null && (msg.contains("Access denied for user") || e.getErrorCode() == 1045)) {
            return "Access denied for user '" + config.getUsername()
                    + "'. Check password in RENTIQ_DB_PASSWORD or .env.";
        } else if (msg != null && (msg.contains("Unknown database") || e.getErrorCode() == 1049)) {
            return "Database does not exist on MySQL server. Run schema.sql to create 'rentiq_db'.";
        }
        return "JDBC Error [" + e.getErrorCode() + " / " + e.getSQLState() + "]: " + e.getMessage();
    }

    public static DatabaseConfig getDefaultConfig() {
        return defaultConfig;
    }

    /**
     * Shuts down MySQL Connector/J background cleanup threads.
     * Prevents thread lingering warnings on application shutdown.
     */
    public static void shutdown() {
        try {
            com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Throwable ignored) {
        }
    }

    /**
     * DTO representing connection test outcome.
     */
    public static class ConnectionResult {
        private final boolean success;
        private final String message;
        private final String serverVersion;
        private final String databaseName;
        private final long responseTimeMs;

        public ConnectionResult(boolean success, String message, String serverVersion, String databaseName, long responseTimeMs) {
            this.success = success;
            this.message = message;
            this.serverVersion = serverVersion;
            this.databaseName = databaseName;
            this.responseTimeMs = responseTimeMs;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getServerVersion() {
            return serverVersion;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        public long getResponseTimeMs() {
            return responseTimeMs;
        }
    }
}
