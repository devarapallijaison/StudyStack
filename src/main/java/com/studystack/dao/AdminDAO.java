package com.studystack.dao;

import com.studystack.model.Admin;
import com.studystack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class AdminDAO {
    public Optional<Admin> findByUsername(String username) throws SQLException {
        String sql = "SELECT admin_id, username, password_hash FROM admins WHERE username = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return Optional.empty();
                Admin admin = new Admin();
                admin.setAdminId(result.getInt("admin_id"));
                admin.setUsername(result.getString("username"));
                admin.setPasswordHash(result.getString("password_hash"));
                return Optional.of(admin);
            }
        }
    }

    public int count() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM admins");
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        }
    }

    public int insert(String username, String passwordHash) throws SQLException {
        String sql = "INSERT INTO admins (username, password_hash) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Creating the administrator did not return an ID.");
                return keys.getInt(1);
            }
        }
    }
}
