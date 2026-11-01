package com.studystack.controller;

import com.studystack.dao.MissionDAO;
import com.studystack.util.CsrfToken;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/mission/archive")
public class MissionDeleteServlet extends HttpServlet {
    private final MissionDAO missionDAO = new MissionDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!CsrfToken.isValid(request)) { response.sendRedirect(request.getContextPath() + "/admin/missions"); return; }
        try {
            int id = Integer.parseInt(request.getParameter("missionId"));
            request.getSession().setAttribute("adminFlash", missionDAO.archive(id) == 1
                    ? "Mission archived. Its submission history was preserved." : "The mission was already archived.");
            response.sendRedirect(request.getContextPath() + "/admin/missions");
        } catch (NumberFormatException ex) { response.sendError(HttpServletResponse.SC_BAD_REQUEST); }
        catch (SQLException ex) {
            getServletContext().log("Unable to archive mission.", ex);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "The mission could not be archived.");
        }
    }
}
