package com.studystack.controller;

import com.studystack.dao.MissionDAO;
import com.studystack.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final MissionDAO missionDAO = new MissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            request.setAttribute("featuredMissions", missionDAO.findActive(null, null).stream().limit(3).toList());
            request.setAttribute("leaders", userDAO.findLeaderboard(3));
            request.getRequestDispatcher("/landing.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Unable to load public StudyStack data.", ex);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "StudyStack is temporarily unavailable.");
        }
    }
}
