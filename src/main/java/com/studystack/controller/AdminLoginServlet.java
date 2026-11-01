package com.studystack.controller;

import com.studystack.model.Admin;
import com.studystack.service.AuthService;
import com.studystack.util.CsrfToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/auth")
public class AdminLoginServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        if (!CsrfToken.isValid(request)) {
            request.setAttribute("error", "Your form expired. Please try again.");
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
            return;
        }
        try {
            Admin admin = authService.authenticateAdmin(request.getParameter("username"), request.getParameter("password")).orElse(null);
            if (admin == null) {
                request.setAttribute("error", "The username or password is incorrect.");
                request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
                return;
            }
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) oldSession.invalidate();
            HttpSession session = request.getSession(true);
            session.setAttribute("ADMIN_ID", admin.getAdminId());
            session.setAttribute("USERNAME", admin.getUsername());
            session.setAttribute("ROLE", "ADMIN");
            CsrfToken.getOrCreate(session);
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } catch (SQLException ex) {
            getServletContext().log("Unable to authenticate an administrator.", ex);
            request.setAttribute("error", "Admin sign in is temporarily unavailable. Please try again.");
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
        }
    }
}
