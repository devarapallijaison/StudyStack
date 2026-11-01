package com.studystack.controller;

import com.studystack.dao.MissionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/missions")
public class PublicMissionsServlet extends HttpServlet {
    private final MissionDAO missionDAO = new MissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            request.setAttribute("missions", missionDAO.findActive(null, null));
            request.getRequestDispatcher("/public-missions.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Unable to load the public mission list.", ex);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Missions are temporarily unavailable.");
        }
    }
}
