package com.studystack.controller;

import com.studystack.dao.SubmissionDAO;
import com.studystack.model.MissionSubmission;
import com.studystack.service.MissionSubmissionService;
import com.studystack.util.CsrfToken;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/student/submit")
public class SubmissionServlet extends HttpServlet {
    private final MissionSubmissionService service = new MissionSubmissionService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CsrfToken.isValid(request)) {
            response.sendRedirect(request.getContextPath() + "/student/missions");
            return;
        }
        int missionId;
        try { missionId = Integer.parseInt(request.getParameter("missionId")); }
        catch (RuntimeException ex) { response.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }
        int userId = (Integer) request.getSession(false).getAttribute("USER_ID");
        try {
            MissionSubmission submission = service.submit(userId, missionId, request.getParameter("answer"));
            String message = submission.isCorrect()
                    ? "Correct. You earned " + submission.getXpEarned() + " XP."
                    : "That answer is incorrect. This mission attempt has been used.";
            request.getSession().setAttribute("submissionResult", message);
        } catch (SubmissionDAO.DuplicateAttemptException ex) {
            request.getSession().setAttribute("submissionResult", "You have already attempted this mission.");
        } catch (IllegalArgumentException ex) {
            request.getSession().setAttribute("submissionResult", ex.getMessage());
        } catch (SQLException ex) {
            getServletContext().log("Unable to record a mission submission.", ex);
            request.getSession().setAttribute("submissionResult", "Your answer could not be recorded. Please try again.");
        }
        response.sendRedirect(request.getContextPath() + "/student/mission?id=" + missionId);
    }
}
