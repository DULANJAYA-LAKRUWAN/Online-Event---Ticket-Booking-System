package com.eventcart.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized application configuration manager.
 * Loads defaults from application.properties and supports environment variable overrides.
 */
public final class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private AppConfig() {
        // Utility / Configuration class - prevent instantiation
    }

    private static void loadProperties() {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                logger.info("application.properties loaded successfully.");
            } else {
                logger.warn("application.properties not found on classpath. Falling back to defaults.");
            }
        } catch (IOException e) {
            logger.error("Failed to load application.properties: {}", e.getMessage(), e);
        }
    }

    public static String get(String key, String defaultValue) {
        // Environment variable override (e.g., db.url -> DB_URL)
        String envKey = key.toUpperCase().replace('.', '_');
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }

        // System property override
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp.trim();
        }

        // Properties file fallback
        return properties.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String val = get(key, null);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer config for '{}': '{}'. Using default: {}", key, val, defaultValue);
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key, null);
        if (val == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(val.trim());
    }

    // Typed convenience getters
    public static String getAppName() {
        return get("app.name", "EventCart");
    }

    public static String getAppTagline() {
        return get("app.tagline", "Discover. Book. Experience.");
    }

    public static String getDbUrl() {
        return get("db.url", "jdbc:mysql://localhost:3306/eventcart_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    }

    public static String getDbUsername() {
        return get("db.username", "root");
    }

    public static String getDbPassword() {
        return get("db.password", "20021115");
    }

    public static String getDbDriver() {
        return get("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    public static String getHibernateDialect() {
        return get("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
    }

    public static boolean isHibernateShowSql() {
        return getBoolean("hibernate.show_sql", false);
    }
}
