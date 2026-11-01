package com.studystack.dao;

import com.studystack.model.MissionSubmission;
import com.studystack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDAO {
    public MissionSubmission submitAnswerAndAwardXp(int userId, int missionId, String answer)
            throws SQLException, DuplicateAttemptException, IllegalArgumentException {
        if (userId < 1 || missionId < 1 || answer == null || !answer.matches("[A-D]")) {
            throw new IllegalArgumentException("A valid student, mission, and answer are required.");
        }
        String missionSql = "SELECT correct_answer, xp_reward FROM missions WHERE mission_id = ? AND is_active = TRUE FOR UPDATE";
        String attemptedSql = "SELECT 1 FROM mission_submissions WHERE user_id = ? AND mission_id = ?";
        String insertSql = "INSERT INTO mission_submissions (user_id, mission_id, selected_answer, is_correct, xp_earned) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String correctAnswer;
                int reward;
                try (PreparedStatement missionStatement = connection.prepareStatement(missionSql)) {
                    missionStatement.setInt(1, missionId);
                    try (ResultSet mission = missionStatement.executeQuery()) {
                        if (!mission.next()) throw new IllegalArgumentException("That mission is no longer available.");
                        correctAnswer = mission.getString("correct_answer");
                        reward = mission.getInt("xp_reward");
                    }
                }
                try (PreparedStatement attemptedStatement = connection.prepareStatement(attemptedSql)) {
                    attemptedStatement.setInt(1, userId);
                    attemptedStatement.setInt(2, missionId);
                    try (ResultSet attempted = attemptedStatement.executeQuery()) {
                        if (attempted.next()) throw new DuplicateAttemptException();
                    }
                }

                boolean correct = answer.equals(correctAnswer);
                int earned = correct ? reward : 0;
                if (correct) {
                    try (PreparedStatement xpStatement = connection.prepareStatement(
                            "UPDATE users SET total_xp = total_xp + ? WHERE user_id = ?")) {
                        xpStatement.setInt(1, earned);
                        xpStatement.setInt(2, userId);
                        if (xpStatement.executeUpdate() != 1) throw new SQLException("Student account was not found.");
                    }
                }
                long id;
                try (PreparedStatement insert = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insert.setInt(1, userId);
                    insert.setInt(2, missionId);
                    insert.setString(3, answer);
                    insert.setBoolean(4, correct);
                    insert.setInt(5, earned);
                    insert.executeUpdate();
                    try (ResultSet keys = insert.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Submission insert did not return an ID.");
                        id = keys.getLong(1);
                    }
                }
                connection.commit();
                MissionSubmission submission = new MissionSubmission();
                submission.setSubmissionId(id);
                submission.setUserId(userId);
                submission.setMissionId(missionId);
                submission.setSelectedAnswer(answer);
                submission.setCorrect(correct);
                submission.setXpEarned(earned);
                return submission;
            } catch (SQLException | RuntimeException | DuplicateAttemptException ex) {
                connection.rollback();
                throw ex;
            }
        }
    }

    public boolean hasAttempted(int userId, int missionId) throws SQLException {
        String sql = "SELECT 1 FROM mission_submissions WHERE user_id = ? AND mission_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, missionId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public int countCorrect() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM mission_submissions WHERE is_correct = TRUE");
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        }
    }

    public List<MissionSubmission> findForUser(int userId) throws SQLException {
        String sql = "SELECT s.submission_id, s.user_id, s.mission_id, s.selected_answer, s.is_correct, s.xp_earned, "
                + "s.submitted_at, m.title AS mission_title FROM mission_submissions s "
                + "JOIN missions m ON m.mission_id = s.mission_id WHERE s.user_id = ? "
                + "ORDER BY s.submitted_at DESC, s.submission_id DESC";
        List<MissionSubmission> submissions = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    MissionSubmission submission = new MissionSubmission();
                    submission.setSubmissionId(result.getLong("submission_id"));
                    submission.setUserId(result.getInt("user_id"));
                    submission.setMissionId(result.getInt("mission_id"));
                    submission.setSelectedAnswer(result.getString("selected_answer"));
                    submission.setCorrect(result.getBoolean("is_correct"));
                    submission.setXpEarned(result.getInt("xp_earned"));
                    submission.setMissionTitle(result.getString("mission_title"));
                    if (result.getTimestamp("submitted_at") != null) {
                        submission.setSubmittedAt(result.getTimestamp("submitted_at").toLocalDateTime());
                    }
                    submissions.add(submission);
                }
            }
        }
        return submissions;
    }

    public static class DuplicateAttemptException extends Exception {
        public DuplicateAttemptException() { super("You have already attempted this mission."); }
    }
}
