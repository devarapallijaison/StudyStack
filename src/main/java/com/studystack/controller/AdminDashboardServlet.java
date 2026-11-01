package com.studystack.controller;

import com.studystack.dao.MissionDAO;
import com.studystack.dao.SubmissionDAO;
import com.studystack.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final MissionDAO missionDAO = new MissionDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            request.setAttribute("studentCount", userDAO.count());
            request.setAttribute("missionCount", missionDAO.count());
            request.setAttribute("completedCount", submissionDAO.countCorrect());
            request.setAttribute("availableCount", missionDAO.findActive(null, null).size());
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Unable to load administrator statistics.", ex);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Admin data is temporarily unavailable.");
        }
    }
}
