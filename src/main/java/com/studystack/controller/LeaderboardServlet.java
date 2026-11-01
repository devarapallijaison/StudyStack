package com.studystack.controller;

import com.studystack.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/student/leaderboard")
public class LeaderboardServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            int currentUserId = (Integer) request.getSession(false).getAttribute("USER_ID");
            request.setAttribute("leaders", userDAO.findLeaderboardWithUser(100, currentUserId));
            request.setAttribute("currentUserId", currentUserId);
            request.getRequestDispatcher("/student/leaderboard.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Unable to load the leaderboard.", ex);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Leaderboard is temporarily unavailable.");
        }
    }
}
