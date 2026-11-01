package com.studystack.controller;

import com.studystack.model.User;
import com.studystack.service.AuthService;
import com.studystack.service.AuthService.ValidationException;
import com.studystack.util.CsrfToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/auth/login", "/auth/register", "/logout"})
public class AuthServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getServletPath();
        response.sendRedirect(request.getContextPath() + (path.endsWith("register") ? "/auth/register.jsp" : "/auth/login.jsp"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        if (!CsrfToken.isValid(request)) {
            if ("/logout".equals(request.getServletPath())) {
                response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
                return;
            }
            request.setAttribute("error", "Your form expired. Please try again.");
            request.getRequestDispatcher(request.getServletPath().endsWith("register")
                    ? "/auth/register.jsp" : "/auth/login.jsp").forward(request, response);
            return;
        }
        if ("/logout".equals(request.getServletPath())) {
            logout(request, response);
            return;
        }
        if (request.getServletPath().endsWith("register")) register(request, response);
        else login(request, response);
    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            authService.register(request.getParameter("fullName"), request.getParameter("username"),
                    request.getParameter("email"), request.getParameter("password"), request.getParameter("confirmPassword"));
            request.getSession().setAttribute("success", "Your account is ready. Sign in to continue.");
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        } catch (ValidationException ex) {
            request.setAttribute("error", ex.getMessage());
            request.getRequestDispatcher("/auth/register.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Unable to register a student account.", ex);
            request.setAttribute("error", "We could not create your account right now. Please try again.");
            request.getRequestDispatcher("/auth/register.jsp").forward(request, response);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            User user = authService.authenticateStudent(request.getParameter("identifier"), request.getParameter("password")).orElse(null);
            if (user == null) {
                request.setAttribute("error", "The username/email or password is incorrect.");
                request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
                return;
            }
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) oldSession.invalidate();
            HttpSession session = request.getSession(true);
            session.setAttribute("USER_ID", user.getUserId());
            session.setAttribute("USERNAME", user.getUsername());
            session.setAttribute("ROLE", "STUDENT");
            CsrfToken.getOrCreate(session);
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        } catch (SQLException ex) {
            getServletContext().log("Unable to authenticate a student.", ex);
            request.setAttribute("error", "Sign in is temporarily unavailable. Please try again.");
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
    }
}
