package com.studystack.controller;

import com.studystack.dao.MissionDAO;
import com.studystack.model.Mission;
import com.studystack.service.MissionService;
import com.studystack.service.MissionService.ValidationException;
import com.studystack.util.CsrfToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/admin/missions", "/admin/missions/new"})
public class MissionManagementServlet extends HttpServlet {
    private final MissionDAO missionDAO = new MissionDAO();
    private final MissionService missionService = new MissionService(missionDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if ("/admin/missions/new".equals(request.getServletPath())) {
            request.setAttribute("mission", new Mission());
            request.getRequestDispatcher("/admin/create-mission.jsp").forward(request, response);
            return;
        }
        String id = request.getParameter("edit");
        if (id != null) {
            try {
                Mission mission = missionDAO.findByIdForAdmin(Integer.parseInt(id)).orElse(null);
                if (mission == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                if (!mission.isActive()) { request.setAttribute("error", "Archived missions cannot be edited."); }
                request.setAttribute("mission", mission);
                request.getRequestDispatcher("/admin/create-mission.jsp").forward(request, response);
            } catch (NumberFormatException ex) { response.sendError(HttpServletResponse.SC_BAD_REQUEST); }
            catch (SQLException ex) { fail(request, response, ex); }
            return;
        }
        try {
            request.setAttribute("missions", missionDAO.findAllForAdmin());
            request.getRequestDispatcher("/admin/missions.jsp").forward(request, response);
        } catch (SQLException ex) { fail(request, response, ex); }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        if (!CsrfToken.isValid(request)) {
            response.sendRedirect(request.getContextPath() + "/admin/missions");
            return;
        }
        Mission mission = readMission(request);
        try {
            missionService.save(mission);
            request.getSession().setAttribute("adminFlash", "Mission saved.");
            response.sendRedirect(request.getContextPath() + "/admin/missions");
        } catch (ValidationException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("mission", mission);
            request.getRequestDispatcher("/admin/create-mission.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Unable to save mission.", ex);
            request.setAttribute("error", "The mission could not be saved. Please check the form and try again.");
            request.setAttribute("mission", mission);
            request.getRequestDispatcher("/admin/create-mission.jsp").forward(request, response);
        }
    }

    private Mission readMission(HttpServletRequest request) {
        Mission mission = new Mission();
        String id = request.getParameter("missionId");
        try { mission.setMissionId(id == null || id.isBlank() ? 0 : Integer.parseInt(id)); }
        catch (RuntimeException ignored) { mission.setMissionId(-1); }
        mission.setTitle(request.getParameter("title"));
        mission.setDescription(request.getParameter("description"));
        mission.setCategory(request.getParameter("category"));
        mission.setDifficulty(request.getParameter("difficulty"));
        mission.setOptionA(request.getParameter("optionA"));
        mission.setOptionB(request.getParameter("optionB"));
        mission.setOptionC(request.getParameter("optionC"));
        mission.setOptionD(request.getParameter("optionD"));
        mission.setCorrectAnswer(request.getParameter("correctAnswer"));
        try { mission.setXpReward(Integer.parseInt(request.getParameter("xpReward"))); }
        catch (RuntimeException ignored) { mission.setXpReward(0); }
        return mission;
    }

    private void fail(HttpServletRequest request, HttpServletResponse response, SQLException ex) throws IOException {
        getServletContext().log("Unable to load mission management.", ex);
        response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Mission management is temporarily unavailable.");
    }
}
