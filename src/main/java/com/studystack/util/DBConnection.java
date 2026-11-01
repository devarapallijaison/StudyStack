package com.studystack.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Creates short-lived JDBC connections using environment-based configuration. */
public final class DBConnection {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/studystack_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = setting("STUDYSTACK_DB_URL", DEFAULT_URL);
        String username = setting("STUDYSTACK_DB_USERNAME", null);
        String password = setting("STUDYSTACK_DB_PASSWORD", null);

        if (username == null || username.isBlank()) {
            throw new SQLException("Database configuration is incomplete: STUDYSTACK_DB_USERNAME is required.");
        }
        if (password == null) {
            throw new SQLException("Database configuration is incomplete: STUDYSTACK_DB_PASSWORD is required.");
        }

        return DriverManager.getConnection(url, username, password);
    }

    private static String setting(String name, String fallback) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            value = System.getProperty(name);
        }
        return value == null || value.isBlank() ? fallback : value;
    }
}
