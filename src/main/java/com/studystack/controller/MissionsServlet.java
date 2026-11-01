package com.studystack.controller;

import com.studystack.dao.MissionDAO;
import com.studystack.dao.SubmissionDAO;
import com.studystack.model.Mission;
import com.studystack.model.MissionSubmission;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/student/missions")
public class MissionsServlet extends HttpServlet {
    private final MissionDAO missionDAO = new MissionDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int userId = (Integer) request.getSession(false).getAttribute("USER_ID");
        String category = allowed(request.getParameter("category"), "Java", "SQL", "DSA");
        String difficulty = allowed(request.getParameter("difficulty"), "Easy", "Medium", "Hard");
        String status = allowed(request.getParameter("status"), "available", "attempted", "completed");
        try {
            List<Mission> missions = missionDAO.findActive(category, difficulty);
            Map<Integer, MissionSubmission> attempts = new HashMap<>();
            for (MissionSubmission submission : submissionDAO.findForUser(userId)) attempts.put(submission.getMissionId(), submission);
            if (status != null) {
                missions = missions.stream().filter(mission -> {
                    MissionSubmission attempt = attempts.get(mission.getMissionId());
                    return switch (status) {
                        case "available" -> attempt == null;
                        case "completed" -> attempt != null && attempt.isCorrect();
                        case "attempted" -> attempt != null && !attempt.isCorrect();
                        default -> true;
                    };
                }).toList();
            }
            request.setAttribute("missions", missions);
            request.setAttribute("attempts", attempts);
            request.setAttribute("selectedCategory", category == null ? "" : category);
            request.setAttribute("selectedDifficulty", difficulty == null ? "" : difficulty);
            request.setAttribute("selectedStatus", status == null ? "" : status);
            request.getRequestDispatcher("/student/missions.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Unable to load missions.", ex);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Missions are temporarily unavailable.");
        }
    }

    private String allowed(String value, String... options) {
        if (value == null || value.isBlank()) return null;
        for (String option : options) if (option.equals(value)) return option;
        return null;
    }
}
