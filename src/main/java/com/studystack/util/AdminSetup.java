package com.studystack.util;

import com.studystack.dao.AdminDAO;

import java.io.Console;
import java.sql.SQLException;
import java.util.regex.Pattern;

/** Run once from a terminal to provision the first admin. The password is never echoed or committed. */
public final class AdminSetup {
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,50}$");

    private AdminSetup() { }

    public static void main(String[] args) throws SQLException {
        Console console = System.console();
        if (console == null) throw new IllegalStateException("Run AdminSetup from a terminal that supports hidden password input.");
        AdminDAO admins = new AdminDAO();
        if (admins.count() > 0) throw new IllegalStateException("An administrator already exists; initial setup is disabled.");
        String username = console.readLine("Initial admin username: ").trim();
        if (!USERNAME.matcher(username).matches()) throw new IllegalArgumentException("Use 3–50 letters, numbers, or underscores.");
        char[] password = console.readPassword("Initial admin password (at least 12 characters): ");
        char[] confirmation = console.readPassword("Confirm password: ");
        try {
            if (password == null || password.length < 12 || password.length > 128) {
                throw new IllegalArgumentException("Password must be 12–128 characters.");
            }
            if (!java.util.Arrays.equals(password, confirmation)) throw new IllegalArgumentException("Passwords do not match.");
            admins.insert(username, PasswordUtil.hash(password));
            console.printf("Administrator '%s' created.%n", username);
        } finally {
            if (password != null) java.util.Arrays.fill(password, '\0');
            if (confirmation != null) java.util.Arrays.fill(confirmation, '\0');
        }
    }
}
