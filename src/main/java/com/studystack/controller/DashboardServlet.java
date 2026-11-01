package com.studystack.controller;

import com.studystack.dao.MissionDAO;
import com.studystack.dao.SubmissionDAO;
import com.studystack.dao.UserDAO;
import com.studystack.model.Mission;
import com.studystack.model.MissionSubmission;
import com.studystack.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(urlPatterns = "/student/dashboard", initParams = @WebInitParam(name = "recommendedMissionLimit", value = "3"))
public class DashboardServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final MissionDAO missionDAO = new MissionDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private int recommendedMissionLimit = 3;

    @Override
    public void init() {
        String configured = getServletConfig().getInitParameter("recommendedMissionLimit");
        if (configured != null) {
            try { recommendedMissionLimit = Math.max(1, Math.min(10, Integer.parseInt(configured))); }
            catch (NumberFormatException ex) { getServletContext().log("Invalid recommendedMissionLimit; using 3."); }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int userId = (Integer) request.getSession(false).getAttribute("USER_ID");
        try {
            User user = userDAO.findById(userId).orElseThrow(SQLException::new);
            List<MissionSubmission> submissions = submissionDAO.findForUser(userId);
            List<Mission> missions = missionDAO.findActive(null, null);
            Set<Integer> attempted = new HashSet<>();
            long completed = submissions.stream().filter(MissionSubmission::isCorrect).count();
            submissions.forEach(submission -> attempted.add(submission.getMissionId()));
            List<Mission> recommended = missions.stream().filter(mission -> !attempted.contains(mission.getMissionId()))
                    .limit(recommendedMissionLimit).toList();
            request.setAttribute("user", user);
            request.setAttribute("completedCount", completed);
            int available = (int) missions.stream().filter(mission -> !attempted.contains(mission.getMissionId())).count();
            request.setAttribute("availableCount", available);
            request.setAttribute("recentActivity", submissions.stream().limit(5).toList());
            request.setAttribute("recommendedMissions", recommended);
            request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Unable to load the student dashboard.", ex);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Dashboard data is temporarily unavailable.");
        }
    }
}
