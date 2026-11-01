package com.studystack.controller;

import com.studystack.dao.SubmissionDAO;
import com.studystack.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/student/profile")
public class ProfileServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int userId = (Integer) request.getSession(false).getAttribute("USER_ID");
        try {
            request.setAttribute("user", userDAO.findById(userId).orElseThrow(SQLException::new));
            request.setAttribute("submissions", submissionDAO.findForUser(userId));
            request.getRequestDispatcher("/student/profile.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Unable to load the student profile.", ex);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Profile is temporarily unavailable.");
        }
    }
}
