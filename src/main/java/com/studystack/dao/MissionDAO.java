package com.studystack.dao;

import com.studystack.model.Mission;
import com.studystack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MissionDAO {
    private static final String COLUMNS = "mission_id, title, description, category, difficulty, option_a, option_b, "
            + "option_c, option_d, correct_answer, xp_reward, is_active, created_at";
    private static final String INSERT = "INSERT INTO missions (title, description, category, difficulty, option_a, "
            + "option_b, option_c, option_d, correct_answer, xp_reward) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /** Student-safe lookup: deliberately does not select the answer key. */
    public Optional<Mission> findActiveForStudent(int missionId) throws SQLException {
        String sql = "SELECT mission_id, title, description, category, difficulty, option_a, option_b, option_c, "
                + "option_d, xp_reward, is_active, created_at FROM missions WHERE mission_id = ? AND is_active = TRUE";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, missionId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapMission(result, false)) : Optional.empty();
            }
        }
    }

    /** Internal server-side evaluation only. Never attach this result to a student view. */
    public Optional<String> findCorrectAnswer(int missionId) throws SQLException {
        String sql = "SELECT correct_answer FROM missions WHERE mission_id = ? AND is_active = TRUE";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, missionId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(result.getString(1)) : Optional.empty();
            }
        }
    }

    public List<Mission> findActive(String category, String difficulty) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT mission_id, title, description, category, difficulty, option_a, "
                + "option_b, option_c, option_d, xp_reward, is_active, created_at FROM missions WHERE is_active = TRUE");
        if (category != null && !category.isBlank()) sql.append(" AND category = ?");
        if (difficulty != null && !difficulty.isBlank()) sql.append(" AND difficulty = ?");
        sql.append(" ORDER BY created_at DESC, mission_id DESC");
        List<Mission> missions = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int index = 1;
            if (category != null && !category.isBlank()) statement.setString(index++, category);
            if (difficulty != null && !difficulty.isBlank()) statement.setString(index, difficulty);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) missions.add(mapMission(result, false));
            }
        }
        return missions;
    }

    /** Admin-only listing includes the answer key. */
    public List<Mission> findAllForAdmin() throws SQLException {
        List<Mission> missions = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM missions ORDER BY created_at DESC, mission_id DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) missions.add(mapMission(result, true));
        }
        return missions;
    }

    public Optional<Mission> findByIdForAdmin(int missionId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM missions WHERE mission_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, missionId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapMission(result, true)) : Optional.empty();
            }
        }
    }

    public int count() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM missions");
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        }
    }

    public int insert(Mission mission) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            bindMission(statement, mission);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Creating the mission did not return an ID.");
                mission.setMissionId(keys.getInt(1));
                return mission.getMissionId();
            }
        }
    }

    public int update(Mission mission) throws SQLException {
        String sql = "UPDATE missions SET title = ?, description = ?, category = ?, difficulty = ?, option_a = ?, "
                + "option_b = ?, option_c = ?, option_d = ?, correct_answer = ?, xp_reward = ? "
                + "WHERE mission_id = ? AND is_active = TRUE "
                + "AND NOT EXISTS (SELECT 1 FROM mission_submissions WHERE mission_id = ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindMission(statement, mission);
            statement.setInt(11, mission.getMissionId());
            statement.setInt(12, mission.getMissionId());
            return statement.executeUpdate();
        }
    }

    /** Soft delete preserves the mission row referenced by historical submissions. */
    public int archive(int missionId) throws SQLException {
        String sql = "UPDATE missions SET is_active = FALSE WHERE mission_id = ? AND is_active = TRUE";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, missionId);
            return statement.executeUpdate();
        }
    }

    private void bindMission(PreparedStatement statement, Mission mission) throws SQLException {
        statement.setString(1, mission.getTitle());
        statement.setString(2, mission.getDescription());
        statement.setString(3, mission.getCategory());
        statement.setString(4, mission.getDifficulty());
        statement.setString(5, mission.getOptionA());
        statement.setString(6, mission.getOptionB());
        statement.setString(7, mission.getOptionC());
        statement.setString(8, mission.getOptionD());
        statement.setString(9, mission.getCorrectAnswer());
        statement.setInt(10, mission.getXpReward());
    }

    private Mission mapMission(ResultSet result, boolean includeAnswer) throws SQLException {
        Mission mission = new Mission();
        mission.setMissionId(result.getInt("mission_id"));
        mission.setTitle(result.getString("title"));
        mission.setDescription(result.getString("description"));
        mission.setCategory(result.getString("category"));
        mission.setDifficulty(result.getString("difficulty"));
        mission.setOptionA(result.getString("option_a"));
        mission.setOptionB(result.getString("option_b"));
        mission.setOptionC(result.getString("option_c"));
        mission.setOptionD(result.getString("option_d"));
        if (includeAnswer) mission.setCorrectAnswer(result.getString("correct_answer"));
        mission.setXpReward(result.getInt("xp_reward"));
        mission.setActive(result.getBoolean("is_active"));
        if (result.getTimestamp("created_at") != null) {
            mission.setCreatedAt(result.getTimestamp("created_at").toLocalDateTime());
        }
        return mission;
    }
}
