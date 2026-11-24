package com.rentiq.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration manager for RentIQ database connectivity.
 * Responsibilities:
 * 1. Reads database URL, username, and password.
 * 2. Prioritizes environment variables (RENTIQ_DB_URL, RENTIQ_DB_USERNAME, RENTIQ_DB_PASSWORD, DB_PASSWORD).
 * 3. Supports local .env file parsing (without third-party dependencies).
 * 4. Provides safe defaults for local development.
 * 5. Masks credentials so passwords are never exposed in logs or strings.
 */
public class DatabaseConfig {
    public static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_DB_NAME = "rentiq_db";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/" + DEFAULT_DB_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final Map<String, String> envFileProperties = loadLocalEnvFile();

    private final String url;
    private final String username;
    private final String password;

    public DatabaseConfig() {
        this.url = resolveConfigProperty("RENTIQ_DB_URL", "DB_URL", DEFAULT_URL);
        this.username = resolveConfigProperty("RENTIQ_DB_USERNAME", "DB_USERNAME",
                resolveConfigProperty("DB_USER", null, DEFAULT_USERNAME));
        this.password = resolveConfigProperty("RENTIQ_DB_PASSWORD", "DB_PASSWORD", DEFAULT_PASSWORD);
    }

    public DatabaseConfig(String url, String username, String password) {
        this.url = (url != null && !url.trim().isEmpty()) ? url.trim() : DEFAULT_URL;
        this.username = (username != null && !username.trim().isEmpty()) ? username.trim() : DEFAULT_USERNAME;
        this.password = (password != null) ? password : DEFAULT_PASSWORD;
    }

    /**
     * Resolves configuration property checking:
     * 1. System environment variable primary key
     * 2. System environment variable fallback key
     * 3. .env file value
     * 4. Provided default value
     */
    private static String resolveConfigProperty(String primaryKey, String fallbackKey, String defaultValue) {
        if (primaryKey != null) {
            String sysVal = System.getenv(primaryKey);
            if (sysVal != null && !sysVal.trim().isEmpty()) {
                return sysVal.trim();
            }
        }

        if (fallbackKey != null) {
            String fallbackVal = System.getenv(fallbackKey);
            if (fallbackVal != null && !fallbackVal.trim().isEmpty()) {
                return fallbackVal.trim();
            }
        }

        if (primaryKey != null && envFileProperties.containsKey(primaryKey)) {
            String envVal = envFileProperties.get(primaryKey);
            if (envVal != null && !envVal.trim().isEmpty()) {
                return envVal.trim();
            }
        }

        if (fallbackKey != null && envFileProperties.containsKey(fallbackKey)) {
            String envVal = envFileProperties.get(fallbackKey);
            if (envVal != null && !envVal.trim().isEmpty()) {
                return envVal.trim();
            }
        }

        return defaultValue;
    }

    /**
     * Simple parser for local .env files without external dependencies.
     * Searches current directory and parent directory.
     */
    private static Map<String, String> loadLocalEnvFile() {
        Map<String, String> props = new HashMap<>();
        String[] candidatePaths = {
            ".env",
            "../.env",
            "backend-java/.env",
            "RentIQ-Java/.env"
        };

        for (String path : candidatePaths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String val = line.substring(eqIdx + 1).trim();
                            // Strip enclosing quotes if present
                            if ((val.startsWith("\"") && val.endsWith("\"")) ||
                                (val.startsWith("'") && val.endsWith("'"))) {
                                val = val.substring(1, val.length() - 1);
                            }
                            props.put(key, val);
                        }
                    }
                    break; // Stop after first matched .env file
                } catch (IOException ignored) {
                }
            }
        }
        return props;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getMaskedPassword() {
        if (password == null || password.isEmpty()) {
            return "[EMPTY/UNSET]";
        }
        return "******** (length: " + password.length() + ")";
    }

    public void displayConfig() {
        System.out.println("----- Database Configuration -----");
        System.out.println("Driver Class : " + DRIVER_CLASS);
        System.out.println("JDBC URL     : " + url);
        System.out.println("Username     : " + username);
        System.out.println("Password     : " + getMaskedPassword());
        System.out.println("----------------------------------");
    }

    @Override
    public String toString() {
        return "DatabaseConfig{url='" + url + "', username='" + username + "', password='" + getMaskedPassword() + "'}";
    }
}
