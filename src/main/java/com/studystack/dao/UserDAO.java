package com.studystack.dao;

import com.studystack.model.User;
import com.studystack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    private static final String PROFILE_COLUMNS = "user_id, full_name, username, email, total_xp, created_at";
    private static final String AUTH_COLUMNS = PROFILE_COLUMNS + ", password_hash";

    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT " + PROFILE_COLUMNS + " FROM users WHERE user_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapUser(result, false)) : Optional.empty();
            }
        }
    }

    public Optional<User> findByUsernameOrEmail(String identifier) throws SQLException {
        boolean email = identifier.indexOf('@') >= 0;
        String sql = "SELECT " + AUTH_COLUMNS + " FROM users WHERE " + (email ? "email" : "username") + " = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, identifier);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapUser(result, true)) : Optional.empty();
            }
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        return exists("SELECT 1 FROM users WHERE username = ?", username);
    }

    public boolean emailExists(String email) throws SQLException {
        return exists("SELECT 1 FROM users WHERE email = ?", email);
    }

    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, username, email, password_hash) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPasswordHash());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Creating the user did not return an ID.");
                int id = keys.getInt(1);
                user.setUserId(id);
                return id;
            }
        }
    }

    public int count() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users");
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        }
    }

    public List<User> findLeaderboard(int limit) throws SQLException {
        if (limit < 1 || limit > 100_000) throw new IllegalArgumentException("Leaderboard limit must be 1–100000.");
        String sql = "SELECT u.user_id, u.full_name, u.username, u.total_xp, u.created_at, "
                + "COUNT(CASE WHEN s.is_correct = TRUE THEN 1 END) AS completed_count "
                + "FROM users u LEFT JOIN mission_submissions s ON s.user_id = u.user_id "
                + "GROUP BY u.user_id ORDER BY u.total_xp DESC, completed_count DESC, u.username ASC LIMIT ?";
        List<User> users = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    User user = new User();
                    user.setUserId(result.getInt("user_id"));
                    user.setFullName(result.getString("full_name"));
                    user.setUsername(result.getString("username"));
                    user.setTotalXp(result.getInt("total_xp"));
                    user.setCompletedMissions(result.getInt("completed_count"));
                    if (result.getTimestamp("created_at") != null) {
                        user.setCreatedAt(result.getTimestamp("created_at").toLocalDateTime());
                    }
                    users.add(user);
                }
            }
        }
        return users;
    }

    /** Returns the top rows and always includes the signed-in student if they rank below the cutoff. */
    public List<User> findLeaderboardWithUser(int limit, int currentUserId) throws SQLException {
        if (limit < 1 || limit > 10_000) throw new IllegalArgumentException("Leaderboard limit must be 1–10000.");
        String sql = "WITH ranked AS (SELECT u.user_id, u.full_name, u.username, u.total_xp, u.created_at, "
                + "COUNT(CASE WHEN s.is_correct = TRUE THEN 1 END) AS completed_count, "
                + "ROW_NUMBER() OVER (ORDER BY u.total_xp DESC, COUNT(CASE WHEN s.is_correct = TRUE THEN 1 END) DESC, u.username ASC) AS rank_position "
                + "FROM users u LEFT JOIN mission_submissions s ON s.user_id = u.user_id GROUP BY u.user_id) "
                + "SELECT user_id, full_name, username, total_xp, created_at, completed_count, rank_position "
                + "FROM ranked WHERE rank_position <= ? OR user_id = ? ORDER BY rank_position";
        List<User> users = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            statement.setInt(2, currentUserId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    User user = new User();
                    user.setUserId(result.getInt("user_id"));
                    user.setFullName(result.getString("full_name"));
                    user.setUsername(result.getString("username"));
                    user.setTotalXp(result.getInt("total_xp"));
                    user.setCompletedMissions(result.getInt("completed_count"));
                    user.setLeaderboardRank(result.getInt("rank_position"));
                    if (result.getTimestamp("created_at") != null) user.setCreatedAt(result.getTimestamp("created_at").toLocalDateTime());
                    users.add(user);
                }
            }
        }
        return users;
    }

    private boolean exists(String sql, String value) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private User mapUser(ResultSet result, boolean includePasswordHash) throws SQLException {
        User user = new User();
        user.setUserId(result.getInt("user_id"));
        user.setFullName(result.getString("full_name"));
        user.setUsername(result.getString("username"));
        user.setEmail(result.getString("email"));
        if (includePasswordHash) user.setPasswordHash(result.getString("password_hash"));
        user.setTotalXp(result.getInt("total_xp"));
        if (result.getTimestamp("created_at") != null) {
            user.setCreatedAt(result.getTimestamp("created_at").toLocalDateTime());
        }
        return user;
    }
}
