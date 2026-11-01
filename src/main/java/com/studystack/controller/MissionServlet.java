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

@WebServlet("/student/mission")
public class MissionServlet extends HttpServlet {
    private final MissionDAO missionDAO = new MissionDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int missionId;
        try { missionId = Integer.parseInt(request.getParameter("id")); }
        catch (RuntimeException ex) { response.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
        int userId = (Integer) request.getSession(false).getAttribute("USER_ID");
        try {
            Mission mission = missionDAO.findActiveForStudent(missionId).orElse(null);
            if (mission == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            MissionSubmission attempt = submissionDAO.findForUser(userId).stream()
                    .filter(item -> item.getMissionId() == missionId).findFirst().orElse(null);
            request.setAttribute("mission", mission);
            request.setAttribute("attempt", attempt);
            request.setAttribute("csrfToken", com.studystack.util.CsrfToken.getOrCreate(request.getSession()));
            request.setAttribute("result", request.getSession().getAttribute("submissionResult"));
            request.getSession().removeAttribute("submissionResult");
            request.getRequestDispatcher("/student/mission-details.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Unable to load mission details.", ex);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Mission details are temporarily unavailable.");
        }
    }
}
