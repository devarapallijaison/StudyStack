package com.studystack.service;

import com.studystack.dao.AdminDAO;
import com.studystack.dao.UserDAO;
import com.studystack.model.Admin;
import com.studystack.model.User;
import com.studystack.util.PasswordUtil;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public class AuthService {
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,30}$");
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final UserDAO userDAO;
    private final AdminDAO adminDAO;

    public AuthService() {
        this(new UserDAO(), new AdminDAO());
    }

    public AuthService(UserDAO userDAO, AdminDAO adminDAO) {
        this.userDAO = userDAO;
        this.adminDAO = adminDAO;
    }

    public User register(String fullName, String username, String email, String password, String confirmation)
            throws SQLException, ValidationException {
        String name = clean(fullName);
        String user = clean(username);
        String normalizedEmail = clean(email).toLowerCase(Locale.ROOT);
        if (name.isEmpty() || name.length() > 100) throw new ValidationException("Enter your full name (up to 100 characters).");
        if (!USERNAME.matcher(user).matches()) throw new ValidationException("Username must be 3–30 characters using letters, numbers, or underscores.");
        if (normalizedEmail.length() > 254 || !EMAIL.matcher(normalizedEmail).matches()) {
            throw new ValidationException("Enter a valid email address.");
        }
        if (password == null || password.length() < 10 || password.length() > 128
                || password.chars().noneMatch(Character::isDigit)
                || password.chars().noneMatch(Character::isLetter)) {
            throw new ValidationException("Password must be 10–128 characters and include a letter and a number.");
        }
        if (!password.equals(confirmation)) throw new ValidationException("The passwords do not match.");
        if (userDAO.usernameExists(user)) throw new ValidationException("That username is already in use.");
        if (userDAO.emailExists(normalizedEmail)) throw new ValidationException("That email address is already registered.");

        User created = new User(name, user, normalizedEmail, PasswordUtil.hash(password));
        userDAO.insert(created);
        return created;
    }

    public Optional<User> authenticateStudent(String identifier, String password) throws SQLException {
        if (identifier == null || password == null) return Optional.empty();
        return userDAO.findByUsernameOrEmail(identifier.trim())
                .filter(user -> PasswordUtil.verify(password, user.getPasswordHash()));
    }

    public Optional<Admin> authenticateAdmin(String username, String password) throws SQLException {
        if (username == null || password == null) return Optional.empty();
        return adminDAO.findByUsername(username.trim())
                .filter(admin -> PasswordUtil.verify(password, admin.getPasswordHash()));
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }
}
